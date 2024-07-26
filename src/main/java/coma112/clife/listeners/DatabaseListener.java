package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.utils.LifeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Objects;

public class DatabaseListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CLife.getDatabase().createPlayer(player);

        Bukkit.getOnlinePlayers().forEach(other -> {
            if (player.getWorld() != other.getWorld()) {
                player.hidePlayer(CLife.getInstance(), other);
                other.hidePlayer(CLife.getInstance(), player);
            } else {
                player.showPlayer(CLife.getInstance(), other);
                other.showPlayer(CLife.getInstance(), player);
            }
        });

        if (CLife.getInstance().getConfiguration().getYml().get("lobby") == null) {
            player.sendMessage(MessageKeys.NO_LOBBY.getMessage());
            return;
        }

        Location location = LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("lobby"));

        player.teleport(Objects.requireNonNull(location));
        player.setVelocity(new Vector(0, 0, 0));
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[0]);
        player.setHealth(20.0);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setHealthScale(20.0);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect effects : player.getActivePotionEffects()) player.removePotionEffect(effects.getType());
        }
}
