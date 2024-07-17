package coma112.clife.managers;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchEndEvent;
import coma112.clife.events.MatchStartEvent;
import coma112.clife.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Match {
    @Getter private final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    @Getter private Player winner;
    private final List<Player> availablePlayers = Collections.synchronizedList(new ArrayList<>());
    private final Map<Player, Integer> playerTimes = new ConcurrentHashMap<>();
    private int countdown;

    public Match() {
        availablePlayers.addAll(Bukkit.getServer().getOnlinePlayers());
        selectPlayersForMatch();
        startCountdown();

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
            playerTimes.put(player, ConfigKeys.STARTING_TIME.getInt());
        });

        startActionBarUpdate();
        CLife.getActiveMatches().add(this);
    }

    public void endMatch() {
        getPlayers().forEach(player -> CLife.getInstance().getColorManager().removeColor(player));
        getPlayers().clear();
        winner = null;
        CLife.getActiveMatches().remove(this);
    }

    public boolean isInMatch(@NotNull Player player) {
        return getPlayers().contains(player);
    }

    public void addTime(@NotNull Player player, int timeToAdd) {
        playerTimes.put(player, playerTimes.getOrDefault(player, 0) + timeToAdd);
    }

    public void removeTime(@NotNull Player player, int timeToRemove) {
        playerTimes.put(player, playerTimes.getOrDefault(player, 0) - timeToRemove);
    }

    public Color getColor(@NotNull Player player) {
        return CLife.getInstance().getColorManager().getColor(player);
    }

    public int getTime(@NotNull Player player) {
        return playerTimes.getOrDefault(player, 0);
    }

    private void selectPlayersForMatch() {
        Collections.shuffle(availablePlayers);

        for (int i = 0; i < Math.min(ConfigKeys.MINIMUM_PLAYERS.getInt(), availablePlayers.size()); i++) getPlayers().add(availablePlayers.get(i));
    }

    private void startPlayerCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (playerTimes) {
                    getPlayers().forEach(player -> {
                        int remaining = playerTimes.getOrDefault(player, 0);

                        if (remaining > 0) playerTimes.put(player, remaining - 1);
                        else if (remaining == 0) {
                            getPlayers().remove(player);
                            CLife.getInstance().getColorManager().removeColor(player);
                        }
                    });

                    updatePlayerColor();
                    checkForWinner();
                }
            }
        }.runTaskTimer(CLife.getInstance(), 0L, 20L);
    }

    private void startCountdown() {
        countdown = ConfigKeys.COUNTDOWN.getInt();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    getPlayers().forEach(player -> PlayerUtils.sendTitle(player,
                            ConfigKeys.COUNTDOWN_TITLE
                                    .getString()
                                    .replace("{time}", String.valueOf(countdown)),

                            ConfigKeys.COUNTDOWN_SUBTITLE
                                    .getString()
                                    .replace("{time}", String.valueOf(countdown))));
                    countdown--;
                } else {
                    cancel();
                    startPlayerCountdown();
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

                    PlayerUtils.sendActionBar(player, ConfigKeys.ACTION_BAR.getString()
                            .replace("{time}", PlayerUtils.formatTime(getTime(player)))
                            .replace("{color}", playerColor != null ? playerColor.getColorCode() : "&2"));
                });
            }
        }.runTaskTimer(CLife.getInstance(), 0L, 20L);
    }

    private void updatePlayerColor() {
        synchronized (playerTimes) {
            getPlayers().forEach(player -> {
                Color color = Color.getColorForTime(playerTimes.getOrDefault(player, 0));

                if (!Objects.equals(getColor(player), color)) CLife.getInstance().getColorManager().setColor(player, color);
            });
        }
    }

    private void checkForWinner() {
        long alivePlayers = getPlayers()
                .stream()
                .filter(player -> playerTimes.getOrDefault(player, 0) > 0)
                .count();

        if (alivePlayers <= 1) {
            winner = getPlayers()
                    .stream()
                    .filter(player -> playerTimes.getOrDefault(player, 0) > 0)
                    .findFirst()
                    .orElse(null);

            if (getWinner() != null) {
                getPlayers().forEach(player -> player.sendMessage(ConfigKeys.END_BROADCAST
                        .getString()
                        .replace("{winner}", getWinner().getName())));

                getPlayers().forEach(players -> PlayerUtils.sendTitle(players,
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
}
