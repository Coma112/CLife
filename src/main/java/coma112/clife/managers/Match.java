package coma112.clife.managers;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchStartedEvent;
import coma112.clife.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

public class Match {
    private static final List<Player> players = new ArrayList<>();
    private static final List<Player> availablePlayers = new ArrayList<>();

    public Match() {
        availablePlayers.addAll(Bukkit.getServer().getOnlinePlayers());
        selectPlayersForMatch();
        assignColors();
        startCountdown();

        players.forEach(player -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20.0);
            player.setExp(0.0f);
            player.setFoodLevel(20);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setGameMode(GameMode.SURVIVAL);

            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        });
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void endMatch() {
        players.clear();
    }

    public static boolean isInMatch(@NotNull Player player) {
        return players.contains(player);
    }

    public Color getColor(@NotNull Player player) {
        for (Color color : Color.values()) {
            if (color.getPlayer() != null && color.getPlayer().equals(player)) return color;
        }

        return null;
    }

    private void selectPlayersForMatch() {
        Collections.shuffle(availablePlayers);
        for (int i = 0; i < 6 && i < availablePlayers.size(); i++) players.add(availablePlayers.get(i));
    }

    private void startCountdown() {
        ScheduledExecutorService scheduler = newScheduledThreadPool(1);

        Runnable task = new Runnable() {
            int countdown = ConfigKeys.COUNTDOWN.getInt();

            @Override
            public void run() {
                if (countdown > 0) {
                    players.forEach(player -> PlayerUtils.sendTitle(player,
                            ConfigKeys.COUNTDOWN_TITLE
                            .getString()
                            .replace("{time}", String.valueOf(countdown)),

                            ConfigKeys.COUNTDOWN_SUBTITLE
                            .getString()
                            .replace("{time}", String.valueOf(countdown))));
                    countdown--;
                } else {
                    scheduler.shutdown();
                    Bukkit.getServer().getPluginManager().callEvent(new MatchStartedEvent(Match.this));
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private void assignColors() {
        Collections.shuffle(availablePlayers);
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Color color = Color.values()[i % Color.values().length];
            color.setPlayer(player);
        }
    }
}
