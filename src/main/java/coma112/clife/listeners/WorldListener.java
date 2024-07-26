package coma112.clife.listeners;

import coma112.clife.CLife;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldListener implements Listener {
    @EventHandler
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        Bukkit.getOnlinePlayers().forEach(other -> {
            if (player.getWorld() != other.getWorld()) {
                player.hidePlayer(CLife.getInstance(), other);
                other.hidePlayer(CLife.getInstance(), player);
            } else {
                player.showPlayer(CLife.getInstance(), other);
                other.showPlayer(CLife.getInstance(), player);
            }
        });
    }
}
