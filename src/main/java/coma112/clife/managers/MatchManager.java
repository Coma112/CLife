package coma112.clife.managers;

import coma112.clife.events.MatchStartedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager implements Listener {
    private static final Map<Player, Match> playerMatchMap = new ConcurrentHashMap<>();

    public static Match getMatch(@NotNull Player player) {
        return playerMatchMap.get(player);
    }

    @EventHandler
    public void onMatchStarted(final MatchStartedEvent event) {
        Match match = event.getMatch();

        match.getPlayers().forEach(player -> playerMatchMap.put(player, match));
    }
}
