package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.ItemKeys;
import coma112.clife.events.MatchSpectatorEvent;
import coma112.clife.managers.Match;
import coma112.clife.menu.menus.AvailablePlayersMenu;
import coma112.clife.utils.MenuUtils;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class SpectatorListener implements Listener {
    @EventHandler
    public void onSpectator(final MatchSpectatorEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        event.getMatch().getPlayers().forEach(players -> players.hidePlayer(CLife.getInstance(), player));
        player.getInventory().setItem(ConfigKeys.LEAVE_ITEM_SLOT.getInt(), ItemKeys.LEAVE_ITEM.getItem());
        player.getInventory().setItem(ConfigKeys.PLAYERFINDER_ITEM_SLOT.getInt(), ItemKeys.PLAYERFINDER_ITEM.getItem());
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Match match = Match.getMatchById(player.getWorld().getName());

        if (player.getInventory().getItemInMainHand().equals(ItemKeys.LEAVE_ITEM.getItem())) {
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK, LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> match.removePlayer(player);
            }
        }

        if (player.getInventory().getItemInMainHand().equals(ItemKeys.PLAYERFINDER_ITEM.getItem())) {
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK, LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> new AvailablePlayersMenu(MenuUtils.getMenuUtils(player)).open();
            }
        }
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (isSpectator(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onClick(final InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (isSpectator(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onClick(final PlayerPickItemEvent event) {
        if (isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    private boolean isSpectator(@NotNull Player player) {
        Match match = CLife.getInstance().getMatch(player);

        if (match == null) return false;

        return match.getSpectators().contains(player);
    }
}
