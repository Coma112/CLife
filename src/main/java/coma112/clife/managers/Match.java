package coma112.clife.managers;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.GameState;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchEndEvent;
import coma112.clife.events.MatchKillEvent;
import coma112.clife.events.MatchSpectatorEvent;
import coma112.clife.events.MatchStartEvent;
import coma112.clife.services.ScoreboardService;
import coma112.clife.utils.LifeLogger;
import coma112.clife.utils.LifeUtils;
import coma112.clife.world.WorldGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.GameMode;
import org.bukkit.GameRule;


import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Match {
    @Getter private final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    @Getter private final List<Player> spectators = Collections.synchronizedList(new ArrayList<>());
    @Getter public static final List<Player> startingPlayers = Collections.synchronizedList(new ArrayList<>());
    @Getter private static final List<Location> chestLocations = Collections.synchronizedList(new ArrayList<>());
    @Getter private static final List<Player> availablePlayers = Collections.synchronizedList(new ArrayList<>());
    @Getter private static List<Material> blockedBlocks = Collections.synchronizedList(new ArrayList<>());
    @Getter public static final List<Player> queue  = Collections.synchronizedList(new ArrayList<>());

    @Getter public static final Map<String, String> defeatedPlayers = new ConcurrentHashMap<>();
    @Getter private static final Map<String, Match> activeMatchesById = new ConcurrentHashMap<>();
    @Getter private final Map<Player, Integer> playerTimes = new ConcurrentHashMap<>();
    @Getter private final Map<Player, Player> lastAttacker = new ConcurrentHashMap<>();
    private static MyScheduledTask countdownTask;

    @Getter private Player winner;
    @Getter private String id;
    @Getter private int countdown;
    @Getter private boolean matchEnded = false;
    @Getter private GameState gameState;

    public Match(@NotNull World world) {
        setStatus(GameState.WAITING);

        CLife.getInstance().getScheduler().runTask(() -> {

            id = world.getName();

            getActiveMatchesById().put(getId(), Match.this);
            WorldGenerator.setFalse();
            setupMatchWorld(world);
            selectPlayersForMatch();

            startCountdown();
            startActionBarUpdate();

            CLife.getActiveMatches().add(Match.this);
            initializePlayers();
            getAvailablePlayers().clear();
        });
    }

    public static Match getMatchById(@NotNull String id) {
        return getActiveMatchesById().get(id);
    }

    public void endMatch() {
        World world = Bukkit.getWorld(id);
        setStatus(GameState.RESTARTING);
        if (matchEnded) return;
        matchEnded = true;

        synchronized (getChestLocations()) {
            getChestLocations().stream()
                    .filter(location -> location.getWorld().equals(Bukkit.getWorld(id)))
                    .forEach(location -> {
                        Block block = location.getBlock();
                        if (block.getType() != Material.AIR) block.setType(Material.AIR);
                    });
            getChestLocations().removeIf(location -> location.getWorld().equals(Bukkit.getWorld(id)));
        }

        synchronized (getPlayers()) {
            Iterator<Player> playerIterator = getPlayers().iterator();

            while (playerIterator.hasNext()) {
                Player player = playerIterator.next();
                player.teleport(LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("lobby")));
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);
                CLife.getInstance().getColorManager().removeColor(player);
                playerIterator.remove();
                ScoreboardService.removeMatchBoard(player);
            }
        }

        synchronized (getSpectators()) {
            Iterator<Player> spectatorIterator = getSpectators().iterator();

            while (spectatorIterator.hasNext()) {
                Player player = spectatorIterator.next();
                player.teleport(LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("lobby")));
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);
                CLife.getInstance().getColorManager().removeColor(player);
                spectatorIterator.remove();
                ScoreboardService.removeMatchBoard(player);
            }
        }

        synchronized (getDefeatedPlayers()) {
            getDefeatedPlayers().entrySet().removeIf(entry -> entry.getValue().equals(getId()));
        }

        LifeUtils.deleteWorld(world);
        CLife.getActiveMatches().remove(this);
        getActiveMatchesById().remove(getId());

        getPlayerTimes().clear();
        getAvailablePlayers().clear();
        getLastAttacker().clear();
        CLife.getInstance().getServer().getPluginManager().callEvent(new MatchEndEvent(Match.this));
        winner = null;
    }

    public void removePlayer(@NotNull Player player) {
        getSpectators().remove(player);
        player.teleport(LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("lobby")));
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        ScoreboardService.removeMatchBoard(player);
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

    public GameState getStatus() {
        return gameState;
    }

    public void setStatus(@NotNull GameState gameState) {
        this.gameState = gameState;
    }

    public void recordAttack(@Nullable Player attacker, @NotNull Player victim, double finalDamage) {
        int victimTime = getTime(victim);
        int damageTime = (int) finalDamage;

        if (attacker != null) {
            if (victimTime - damageTime <= 0) handlePlayerDeath(victim, attacker);
            else {
                lastAttacker.put(victim, attacker);
                removeTime(victim, damageTime);
            }
        } else {
            if (victimTime - damageTime <= 0) handlePlayerDeath(victim, null);
            else removeTime(victim, damageTime);
        }
    }

    private void selectPlayersForMatch() {
        Collections.shuffle(getAvailablePlayers());

        for (int i = 0; i < Math.min(ConfigKeys.MINIMUM_PLAYERS.getInt(), getAvailablePlayers().size()); i++) getPlayers().add(getAvailablePlayers().get(i));
    }

    private void startPlayerCountdown() {
        setStatus(GameState.PLAYING);

        CLife.getInstance().getScheduler().runTaskTimer(() -> {
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
            }
        }, 0L, 20L);
    }


    private void startCountdown() {
        setStatus(GameState.STARTING);
        countdown = ConfigKeys.COUNTDOWN.getInt();

        getStartingPlayers().addAll(getPlayers());

        if (ConfigKeys.CHEST_ENABLED.getBoolean()) placeChests();
        if (ConfigKeys.RTP_ENABLED.getBoolean()) randomTeleport();
        else getPlayers().forEach(player -> player.teleport(Objects.requireNonNull(Bukkit.getWorld(getId())).getSpawnLocation()));

        countdownTask = CLife.getInstance().getScheduler().runTaskTimer(() -> {
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
                countdownTask.cancel();
                startPlayerCountdown();
                getStartingPlayers().clear();
                CLife.getInstance().getServer().getPluginManager().callEvent(new MatchStartEvent(Match.this));
                getPlayers().forEach(ScoreboardService::giveMatchBoard);
            }
        }, 0L, 20L);
    }


    private void startActionBarUpdate() {
        CLife.getInstance().getScheduler().runTaskTimer(() -> {
            getPlayers().forEach(player -> {
                Color playerColor = getColor(player);

                LifeUtils.sendActionBar(player, ConfigKeys.ACTION_BAR.getString()
                        .replace("{time}", LifeUtils.formatTime(getTime(player)))
                        .replace("{color}", playerColor != null ? playerColor.getColorCode() : "&2"));
            });
        }, 0L, 20L);
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

                getSpectators().forEach(player -> player.sendMessage(ConfigKeys.END_BROADCAST
                        .getString()
                        .replace("{winner}", getWinner().getName())));

                getSpectators().forEach(players -> LifeUtils.sendTitle(players,
                        ConfigKeys.END_TITLE
                                .getString()
                                .replace("{winner}", getWinner().getName()),

                        ConfigKeys.END_SUBTITLE
                                .getString()
                                .replace("{winner}", getWinner().getName())));

                CLife.getDatabase().addWin(getWinner());

            }

            endMatch();
        }
    }

    private void handlePlayerDeath(@NotNull Player victim, @Nullable Player killer) {
        getPlayers().remove(victim);
        CLife.getInstance().getColorManager().removeColor(victim);
        getSpectators().add(victim);
        getDefeatedPlayers().put(victim.getName(), getId());
        CLife.getInstance().getServer().getPluginManager().callEvent(new MatchSpectatorEvent(Match.this, victim));

        if (killer != null) {
            getPlayers().forEach(players -> players.sendMessage(ConfigKeys.DEATH_BROADCAST_PLAYER
                    .getString()
                    .replace("{victim}", victim.getName())
                    .replace("{killer}", killer.getName())));

            LifeUtils.sendTitle(victim, ConfigKeys.DEATH_VICTIM_TITLE.getString(), ConfigKeys.DEATH_VICTIM_SUBTITLE.getString());
            CLife.getDatabase().addDeath(victim);
            CLife.getDatabase().addKill(killer);
            CLife.getInstance().getServer().getPluginManager().callEvent(new MatchKillEvent(victim, killer));
        } else {
            getPlayers().forEach(players -> players.sendMessage(ConfigKeys.DEATH_BROADCAST_NOPLAYER
                    .getString()
                    .replace("{victim}", victim.getName())));

            LifeUtils.sendTitle(victim, ConfigKeys.DEATH_VICTIM_TITLE.getString(), ConfigKeys.DEATH_VICTIM_SUBTITLE.getString());
        }

        updatePlayerColor();
        if (getPlayers().stream().filter(player -> getPlayerTimes().getOrDefault(player, 0) > 0).count() <= 1) checkForWinner();
    }

    private void setupMatchWorld(@NotNull World world) {
        if (ConfigKeys.ALWAYS_DAY.getBoolean()) {
            world.setTime(6000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
    }

    private void initializePlayers() {
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
    }

    private void placeChests() {
        World world = Bukkit.getWorld(id);
        Location center = Objects.requireNonNull(world).getSpawnLocation();
        double radius = ConfigKeys.RADIUS.getInt();

        for (int i = 0; i < ConfigKeys.CHEST_COUNT.getInt(); i++) {
            Location chestLocation = LifeUtils.findSafeLocation(center, radius);

            if (chestLocation != null) {
                World word = chestLocation.getWorld();
                int x = chestLocation.getBlockX();
                int z = chestLocation.getBlockZ();
                int y = word.getHighestBlockYAt(x, z);
                Location safeLocation = new Location(word, x, y + 1, z);
                Block blockAbove = word.getBlockAt(safeLocation);
                int retries = 5;

                while (retries > 0 && blockAbove.getType() != Material.AIR) {
                    safeLocation.setY(safeLocation.getY() + 1);
                    blockAbove = word.getBlockAt(safeLocation);
                    retries--;
                }

                if (blockAbove.getType() == Material.AIR) {
                    safeLocation.getBlock().setType(Material.CHEST);
                    LifeUtils.fillChestWithLoot(safeLocation);
                    chestLocations.add(safeLocation);
                } else LifeLogger.warn("No safe location found for chest placement.");
            } else LifeLogger.warn("No safe location found for chest placement.");
        }
    }

    private void randomTeleport() {
        World world = Bukkit.getWorld(getId());
        Location center = Objects.requireNonNull(world).getSpawnLocation();
        double radius = ConfigKeys.RADIUS.getInt();

        getPlayers().forEach(player -> {
            Location teleportLocation = LifeUtils.findSafeLocation(center, radius);

            if (teleportLocation != null) {
                World world2 = teleportLocation.getWorld();
                int x = teleportLocation.getBlockX();
                int z = teleportLocation.getBlockZ();
                int y = world2.getHighestBlockYAt(x, z);
                Location safeLocation = new Location(world2, x, y + 1, z);
                Block blockAbove = world2.getBlockAt(safeLocation);

                if (blockAbove.getType() == Material.AIR) player.teleport(safeLocation);
                else {
                    int retries = 5;

                    while (retries > 0 && blockAbove.getType() != Material.AIR) {
                        safeLocation.setY(safeLocation.getY() + 1);
                        blockAbove = world2.getBlockAt(safeLocation);
                        retries--;
                    }

                    if (blockAbove.getType() == Material.AIR) player.teleport(safeLocation);
                    else LifeLogger.warn("No safe location found for player " + player.getName());
                }
            } else {
                LifeLogger.warn("No safe location found for player " + player.getName());
            }
        });
    }
}

