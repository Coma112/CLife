package coma112.clife.listeners;

import coma112.clife.CLife;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DatabaseListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CLife.getDatabase().createPlayer(event.getPlayer());
    }
}
