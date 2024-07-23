package coma112.clife.managers;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchEndEvent;
import coma112.clife.events.MatchSpectatorEvent;
import coma112.clife.events.MatchStartEvent;
import coma112.clife.utils.LifeLogger;
import coma112.clife.utils.LifeUtils;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Match {
    @Getter public static final Map<String, Match> activeMatchesById = new ConcurrentHashMap<>();
    @Getter private final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    @Getter private final List<Player> spectators = Collections.synchronizedList(new ArrayList<>());
    @Getter public static final List<Player> startingPlayers = Collections.synchronizedList(new ArrayList<>());
    @Getter public static final List<Location> chestLocations = Collections.synchronizedList(new ArrayList<>());
    @Getter public static final Map<String, String> defeatedPlayers = new ConcurrentHashMap<>();
    @Getter private Player winner;
    @Getter public static final String id = "MatchID";
    @Getter private final Set<String> disconnectedSpectators = Collections.synchronizedSet(new HashSet<>());
    @Getter public final List<Player> availablePlayers = Collections.synchronizedList(new ArrayList<>());
    @Getter public final Map<Player, Integer> playerTimes = new ConcurrentHashMap<>();
    @Getter private int countdown;
    private boolean matchEnded = false;

    public Match() {
        World world = LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("match-center")).getWorld();
        getAvailablePlayers().addAll(Bukkit.getServer().getOnlinePlayers());
        selectPlayersForMatch();

        getPlayers().forEach(player -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20.0);
            player.setExp(0.0f);
            player.setFoodLevel(20);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            getPlayerTimes().put(player, ConfigKeys.STARTING_TIME.getInt());
        });

        startCountdown();
        startActionBarUpdate();

        CLife.getActiveMatches().add(this);
        getActiveMatchesById().put(getId(), this);

        if (ConfigKeys.ALWAYS_DAY.getBoolean()) {
            CLife.getInstance().getScheduler().runTaskTimer(() -> {
                if (world.getTime() > 12000) world.setTime(1000);
            }, 0, 100);
        }
    }

    public static Match getMatchById(@NotNull String id) {
        return getActiveMatchesById().get(id);
    }

    public void handleDisconnecting(@NotNull Player player) {
        if (getDefeatedPlayers().containsKey(player.getName()) || getDisconnectedSpectators().contains(player.getName())) {
            getSpectators().add(player);
            player.setGameMode(GameMode.SPECTATOR);
            getDisconnectedSpectators().remove(player.getName());
            CLife.getInstance().getServer().getPluginManager().callEvent(new MatchSpectatorEvent(Match.this, player));
        }
    }

    public void endMatch() {
        if (matchEnded) return;
        matchEnded = true;

        getChestLocations().forEach(location -> location.getBlock().setType(Material.AIR));
        getChestLocations().clear();

        getPlayers().forEach(player -> {
            CLife.getInstance().getColorManager().removeColor(player);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
        });

        getPlayers().clear();

        getSpectators().forEach(player -> player.setGameMode(GameMode.SURVIVAL));
        getSpectators().clear();

        winner = null;

        CLife.getActiveMatches().remove(this);
        getActiveMatchesById().remove(getId());
        getActiveMatchesById().clear();

        getDefeatedPlayers().clear();

        getDisconnectedSpectators().clear();
        getPlayerTimes().clear();
        getAvailablePlayers().clear();
    }


    public boolean isInMatch(@NotNull Player player) {
        return getPlayers().contains(player);
    }

    public void addTime(@NotNull Player player, int timeToAdd) {
        getPlayerTimes().put(player, getPlayerTimes().getOrDefault(player, 0) + timeToAdd);
    }

    public void removeTime(@NotNull Player player, int timeToRemove) {
        getPlayerTimes().put(player, getPlayerTimes().getOrDefault(player, 0) - timeToRemove);
    }

    public void setTime(@NotNull Player player, int time) {
        getPlayerTimes().put(player, time);
    }

    public Color getColor(@NotNull Player player) {
        return CLife.getInstance().getColorManager().getColor(player);
    }

    public int getTime(@NotNull Player player) {
        return getPlayerTimes().getOrDefault(player, 0);
    }

    private void selectPlayersForMatch() {
        Collections.shuffle(getAvailablePlayers());

        for (int i = 0; i < Math.min(ConfigKeys.MINIMUM_PLAYERS.getInt(), getAvailablePlayers().size()); i++) getPlayers().add(getAvailablePlayers().get(i));
    }

    private void startPlayerCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (getPlayerTimes()) {
                    Iterator<Player> iterator = getPlayers().iterator();

                    while (iterator.hasNext()) {
                        Player player = iterator.next();
                        int remaining = getPlayerTimes().getOrDefault(player, 0);

                        if (remaining > 0) {
                            getPlayerTimes().put(player, remaining - 1);
                        } else {
                            iterator.remove();
                            CLife.getInstance().getColorManager().removeColor(player);
                            getSpectators().add(player);
                            getDefeatedPlayers().put(player.getName(), getId());
                            CLife.getInstance().getServer().getPluginManager().callEvent(new MatchSpectatorEvent(Match.this, player));
                        }
                    }

                    updatePlayerColor();
                    checkForWinner();
                }
            }
        }.runTaskTimer(CLife.getInstance(), 0L, 20L);
    }


    private void startCountdown() {
        countdown = ConfigKeys.COUNTDOWN.getInt();
        Location center = LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("match-center"));
        double radius = CLife.getInstance().getConfiguration().getYml().getDouble("match-radius");

        getStartingPlayers().addAll(getPlayers());
        LifeUtils.setWorldBorder(center, radius);

        if (ConfigKeys.RTP_ENABLED.getBoolean()) randomTeleport();
        if (ConfigKeys.CHEST_ENABLED.getBoolean()) placeChests();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getCountdown() > 0) {
                    getPlayers().forEach(player -> LifeUtils.sendTitle(player,
                            ConfigKeys.COUNTDOWN_TITLE
                                    .getString()
                                    .replace("{time}", String.valueOf(getCountdown())),

                            ConfigKeys.COUNTDOWN_SUBTITLE
                                    .getString()
                                    .replace("{time}", String.valueOf(getCountdown()))));
                    countdown--;
                } else {
                    cancel();
                    startPlayerCountdown();
                    getStartingPlayers().clear();
                    CLife.getInstance().getServer().getPluginManager().callEvent(new MatchStartEvent(Match.this));
                }
            }
        }.runTaskTimer(CLife.getInstance(), 0L, 20L);
    }

    private void startActionBarUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayers().forEach(player -> {
                    Color playerColor = getColor(player);

                    LifeUtils.sendActionBar(player, ConfigKeys.ACTION_BAR.getString()
                            .replace("{time}", LifeUtils.formatTime(getTime(player)))
                            .replace("{color}", playerColor != null ? playerColor.getColorCode() : "&2"));
                });
            }
        }.runTaskTimer(CLife.getInstance(), 0L, 20L);
    }

    private void updatePlayerColor() {
        synchronized (getPlayerTimes()) {
            getPlayers().forEach(player -> {
                Color color = Color.getColorForTime(getPlayerTimes().getOrDefault(player, 0));

                if (!Objects.equals(getColor(player), color)) CLife.getInstance().getColorManager().setColor(player, color);
            });
        }
    }

    private void checkForWinner() {
        long alivePlayers = getPlayers()
                .stream()
                .filter(player -> getPlayerTimes().getOrDefault(player, 0) > 0)
                .count();

        if (alivePlayers <= 1) {
            winner = getPlayers()
                    .stream()
                    .filter(player -> getPlayerTimes().getOrDefault(player, 0) > 0)
                    .findFirst()
                    .orElse(null);

            if (getWinner() != null) {
                getPlayers().forEach(player -> player.sendMessage(ConfigKeys.END_BROADCAST
                        .getString()
                        .replace("{winner}", getWinner().getName())));

                getPlayers().forEach(players -> LifeUtils.sendTitle(players,
                        ConfigKeys.END_TITLE
                                .getString()
                                .replace("{winner}", getWinner().getName()),

                        ConfigKeys.END_SUBTITLE
                                .getString()
                                .replace("{winner}", getWinner().getName())));

                CLife.getDatabase().addWin(getWinner());
                CLife.getInstance().getServer().getPluginManager().callEvent(new MatchEndEvent(Match.this));
            }
            endMatch();
        }
    }


    private void randomTeleport() {
        Location center = LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("match-center"));
        double radius = CLife.getInstance().getConfiguration().getYml().getDouble("match-radius");

        if (center == null) {
            LifeLogger.warn("Match center location is not configured correctly. Cannot teleport players.");
            return;
        }

        getPlayers().forEach(player -> {
            Location teleportLocation = LifeUtils.findSafeLocation(center, radius);
            if (teleportLocation != null) {
                World world = teleportLocation.getWorld();
                int x = teleportLocation.getBlockX();
                int z = teleportLocation.getBlockZ();

                int y = world.getHighestBlockYAt(x, z);
                Location safeLocation = new Location(world, x, y + 1, z);

                Block blockAbove = world.getBlockAt(safeLocation);
                if (blockAbove.getType() == Material.AIR) {
                    player.teleport(safeLocation);
                } else {
                    int retries = 5;
                    while (retries > 0 && blockAbove.getType() != Material.AIR) {
                        safeLocation.setY(safeLocation.getY() + 1);
                        blockAbove = world.getBlockAt(safeLocation);
                        retries--;
                    }

                    if (blockAbove.getType() == Material.AIR) {
                        player.teleport(safeLocation);
                    } else {
                        LifeLogger.warn("No safe location found for player " + player.getName());
                    }
                }
            } else {
                LifeLogger.warn("No safe location found for player " + player.getName());
            }
        });
    }

    private void placeChests() {
        Location center = LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("match-center"));
        double radius = CLife.getInstance().getConfiguration().getYml().getDouble("match-radius");

        if (center == null) {
            LifeLogger.warn("Match center location is not configured correctly. Cannot place chests.");
            return;
        }

        for (Location location : getChestLocations()) {
            if (location.getBlock().getType() == Material.CHEST) {
                location.getBlock().setType(Material.AIR);
            }
        }
        chestLocations.clear();

        for (int i = 0; i < ConfigKeys.CHEST_COUNT.getInt(); i++) {
            Location chestLocation = LifeUtils.findSafeLocation(center, radius);

            if (chestLocation != null) {
                World world = chestLocation.getWorld();
                int x = chestLocation.getBlockX();
                int z = chestLocation.getBlockZ();
                int y = world.getHighestBlockYAt(x, z);

                Location safeLocation = new Location(world, x, y + 1, z);
                Block blockAbove = world.getBlockAt(safeLocation);
                int retries = 5;

                while (retries > 0 && blockAbove.getType() != Material.AIR) {
                    safeLocation.setY(safeLocation.getY() + 1);
                    blockAbove = world.getBlockAt(safeLocation);
                    retries--;
                }

                if (blockAbove.getType() == Material.AIR) {
                    safeLocation.getBlock().setType(Material.CHEST);
                    fillChestWithLoot(safeLocation);
                    chestLocations.add(safeLocation);
                } else {
                    LifeLogger.warn("No safe location found for chest placement.");
                }
            } else {
                LifeLogger.warn("No safe location found for chest placement.");
            }
        }
    }

    private void fillChestWithLoot(@NotNull Location chestLocation) {
        Chest chest = (Chest) chestLocation.getBlock().getState();
        Inventory inventory = chest.getBlockInventory();
        ChestManager.generateLoot(inventory, "chest-loot");
    }
}

