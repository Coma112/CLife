package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.events.MatchSpectatorEvent;
import coma112.clife.item.IItemBuilder;
import coma112.clife.managers.Match;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        player.getInventory().setItem(0, IItemBuilder.createItemFromSection(Objects.requireNonNull(CLife.getInstance().getConfiguration().getSection("spectator.leave-item"))));
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Match match = Match.getMatchById(player.getWorld().getName());

        if (player.getInventory().getItemInMainHand().equals(IItemBuilder.createItemFromSection(Objects.requireNonNull(CLife.getInstance().getConfiguration().getSection("spectator.leave-item"))))) {
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK, LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> match.removePlayer(player);
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
