package coma112.clife.listeners;

import coma112.clife.events.MatchSpectatorEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpectatorListener implements Listener {
    @EventHandler
    public void onSpectator(final MatchSpectatorEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }
}
