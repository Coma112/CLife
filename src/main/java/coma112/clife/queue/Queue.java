package coma112.clife.queue;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeUtils;
import coma112.clife.world.WorldGenerator;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Queue {
    @Getter
    public static final List<Player> queue = Collections.synchronizedList(new ArrayList<>());
    private static MyScheduledTask task;

    public static void addPlayerToQueue(@NotNull Player player) {
        synchronized (getQueue()) {
            if (getQueue().size() < ConfigKeys.QUEUE_MAX.getInt()) {
                if (getQueue().contains(player)) {
                    player.sendMessage(MessageKeys.IN_QUEUE.getMessage());
                } else {
                    getQueue().add(player);
                    notifyQueuePlayersOnJoin();

                    if (task == null) startQueueActionBarUpdate();
                    if (getQueue().size() == ConfigKeys.QUEUE_MAX.getInt()) processQueue();
                }
            } else player.sendMessage(MessageKeys.MAX_QUEUE.getMessage());
        }
    }

    public static void removePlayerFromQueue(@NotNull Player player) {
        synchronized (getQueue()) {
            if (getQueue().contains(player)) {
                getQueue().remove(player);
                player.sendMessage(MessageKeys.REMOVE_FROM_QUEUE.getMessage());
                notifyQueuePlayersOnLeave();

                if (getQueue().size() >= ConfigKeys.QUEUE_MAX.getInt()) processQueue();

            } else player.sendMessage(MessageKeys.NOT_IN_QUEUE.getMessage());

            if (getQueue().isEmpty() && task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    public static void processQueue() {
        World world = WorldGenerator.generateWorld();

        CLife.getInstance().getScheduler().runTaskLater(() -> {
            List<Player> playersToProcess;

            synchronized (getQueue()) {
                if (getQueue().size() < ConfigKeys.QUEUE_MAX.getInt()) return;

                playersToProcess = new ArrayList<>(getQueue().subList(0, ConfigKeys.QUEUE_MAX.getInt()));

                getQueue().subList(0, ConfigKeys.QUEUE_MAX.getInt()).clear();
            }

            playersToProcess.forEach(player -> {
                Match.getAvailablePlayers().add(player);
                getQueue().remove(player);
            });

            new Match(world);
        }, 100L);
    }

    private static void notifyQueuePlayersOnJoin() {
        synchronized (getQueue()) {
            getQueue().forEach(player -> player.sendMessage(MessageKeys.QUEUE_UPDATE_JOIN.getMessage()
                    .replace("{queueSize}", String.valueOf(getQueue().size()))
                    .replace("{queueMax}", String.valueOf(ConfigKeys.QUEUE_MAX.getInt()))));
        }
    }

    private static void notifyQueuePlayersOnLeave() {
        synchronized (getQueue()) {
            getQueue().forEach(player -> player.sendMessage(MessageKeys.QUEUE_UPDATE_LEFT.getMessage()
                    .replace("{queueSize}", String.valueOf(getQueue().size()))
                    .replace("{queueMax}", String.valueOf(ConfigKeys.QUEUE_MAX.getInt()))));
        }
    }

    private static void startQueueActionBarUpdate() {
        task = CLife.getInstance().getScheduler().runTaskTimer(() -> {
            synchronized (getQueue()) {
                getQueue().forEach(player -> LifeUtils.sendActionBar(player, ConfigKeys.QUEUE_ACTIONBAR.getString()
                        .replace("{queueSize}", String.valueOf(getQueue().size()))
                        .replace("{queueMax}", String.valueOf(ConfigKeys.QUEUE_MAX.getInt()))));
            }
        }, 0L, 20L);
    }
}
