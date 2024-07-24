package coma112.clife.queue;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Queue {

    @Getter public static final List<Player> queue  = Collections.synchronizedList(new ArrayList<>());

    public static void addPlayerToQueue(Player player) {
        synchronized (getQueue()) {
            if (getQueue().size() < ConfigKeys.QUEUE_MAX.getInt()) {
                if (getQueue().contains(player)) {
                    player.sendMessage(MessageKeys.IN_QUEUE.getMessage());
                } else {
                    getQueue().add(player);
                    notifyQueuePlayersOnJoin();
                    if (getQueue().size() == ConfigKeys.QUEUE_MAX.getInt()) processQueue();
                }
            } else player.sendMessage(MessageKeys.NOT_IN_QUEUE.getMessage());
        }

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

    public static void removePlayerFromQueue(Player player) {
        synchronized (getQueue()) {
            if (getQueue().contains(player)) {
                getQueue().remove(player);
                player.sendMessage(MessageKeys.REMOVE_FROM_QUEUE.getMessage());
                notifyQueuePlayersOnLeave();
                if (getQueue().size() >= ConfigKeys.QUEUE_MAX.getInt()) processQueue();
            }

            player.sendMessage(MessageKeys.NOT_IN_QUEUE.getMessage());
        }
    }


    public static void processQueue() {
        new BukkitRunnable() {
            @Override
            public void run() {
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

                new Match();
            }


        }.runTaskLater(CLife.getInstance(), 100L);
    }
}
