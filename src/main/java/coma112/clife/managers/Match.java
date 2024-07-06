package coma112.clife.managers;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchStartedEvent;
import coma112.clife.utils.PlayerUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

@SuppressWarnings("deprecation")
public class Match implements Listener {
    @Getter
    private final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    private final List<Player> availablePlayers = Collections.synchronizedList(new ArrayList<>());
    private final Map<Player, Integer> playerTimes = new ConcurrentHashMap<>();
    private final Map<Player, Match> matchByPlayer = new ConcurrentHashMap<>();

    public Match() {
        availablePlayers.addAll(Bukkit.getServer().getOnlinePlayers());
        selectPlayersForMatch();
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
            playerTimes.put(player, ConfigKeys.STARTING_TIME.getInt());
        });

        startActionBarUpdate();
        CLife.getActiveMatches().add(this);
    }

    public void endMatch() {
        players.clear();
        matchByPlayer.clear();
        CLife.getActiveMatches().remove(this);
    }

    public Match getMatchByPlayer(@NotNull Player player) {
        return matchByPlayer.get(player);
    }

    public boolean isInMatch(@NotNull Player player) {
        return players.contains(player);
    }

    public Color getColor(@NotNull Player player) {
        for (Color color : Color.values()) {
            if (color.getPlayer() != null && color.getPlayer().equals(player)) return color;
        }

        return null;
    }

    public String getTime(@NotNull Player player) {
        return formatTime(playerTimes.getOrDefault(player, 0));
    }

    private void selectPlayersForMatch() {
        Collections.shuffle(availablePlayers);
        int numPlayersToAdd = Math.min(ConfigKeys.MINIMUM_PLAYERS.getInt(), availablePlayers.size());
        for (int i = 0; i < numPlayersToAdd; i++) {
            Player player = availablePlayers.get(i);
            players.add(player);
            matchByPlayer.put(player, this);
        }
    }

    private void startPlayerCountdown() {
        ScheduledExecutorService scheduler = newScheduledThreadPool(1);

        Runnable task = () -> {
            for (Player player : players) {
                int remainingTime = playerTimes.getOrDefault(player, 0);

                if (remainingTime > 0) playerTimes.put(player, remainingTime - 1);
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
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
                    startPlayerCountdown();
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private void startActionBarUpdate() {
        ScheduledExecutorService scheduler = newScheduledThreadPool(1);

        Runnable task = () -> {
            for (Player player : players) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getTime(player)));
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        Match match = event.getMatch();

        match.getPlayers().forEach(player -> playerTimes.put(player, ConfigKeys.STARTING_TIME.getInt()));
    }
}
