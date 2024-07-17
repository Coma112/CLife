package coma112.clife.listeners;

import coma112.clife.managers.Match;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisconnectListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getMatchById(Match.getId());

        if (match != null) match.handleDisconnecting(player);
        else player.setGameMode(GameMode.SURVIVAL);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getMatchById(Match.getId());

        if (match != null && match.getSpectators().contains(player)) {
            match.getSpectators().remove(player);
            match.getDisconnectedSpectators().add(player.getName());
        }
    }
}
