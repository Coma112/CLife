package coma112.clife.listeners;

import coma112.clife.managers.Match;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {
    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        if (Match.getStartingPlayers().contains(event.getPlayer())) event.setCancelled(true);
    }
}
