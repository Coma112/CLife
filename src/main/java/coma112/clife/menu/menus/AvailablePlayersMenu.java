package coma112.clife.menu.menus;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.item.IItemBuilder;
import coma112.clife.item.ItemBuilder;
import coma112.clife.managers.Match;
import coma112.clife.menu.PaginatedMenu;
import coma112.clife.processor.MessageProcessor;
import coma112.clife.utils.MenuUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public class AvailablePlayersMenu extends PaginatedMenu implements Listener {
    public AvailablePlayersMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.MENU_SIZE.getInt();
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        Match match = Match.getMatchById(player.getWorld().getName());
        List<Player> players = match.getPlayers();
        int clickedIndex = event.getSlot();

        if (clickedIndex == ConfigKeys.FORWARD_ITEM_SLOT.getInt()) {
            int nextPageIndex = page + 1;
            int totalPages = (int) Math.ceil((double) players.size() / getMaxItemsPerPage());

            if (nextPageIndex >= totalPages) {
                player.sendMessage(MessageKeys.LAST_PAGE.getMessage());
                return;
            } else {
                page++;
                super.open();
            }
        }

        if (clickedIndex == ConfigKeys.BACK_ITEM_SLOT.getInt()) {
            if (page == 0) {
                player.sendMessage(MessageKeys.FIRST_PAGE.getMessage());
                return;
            } else {
                page--;
                super.open();
            }
        }

        if (clickedIndex >= 0 && clickedIndex < players.size()) {
            Player selectedPlayer = players.get(clickedIndex);

            player.teleport(selectedPlayer.getLocation());
            inventory.close();
        }
    }

    @Override
    public void setMenuItems() {
        Match match = Match.getMatchById(menuUtils.getOwner().getWorld().getName());
        List<Player> players = match.getPlayers();

        inventory.clear();
        addMenuBorder();

        int startIndex = page * getMaxItemsPerPage();
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(), players.size());

        for (int index = startIndex; index < endIndex; index++) {
            ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD, 1);
            ItemMeta playerMeta = playerItem.getItemMeta();

            playerMeta.setDisplayName(MessageProcessor.process("&a") + players.get(index).getName());
            playerMeta.getPersistentDataContainer().set(new NamespacedKey(CLife.getInstance(), "uuid"), PersistentDataType.STRING, players.get(index).getUniqueId().toString());
            playerItem.setItemMeta(playerMeta);
            inventory.addItem(playerItem);
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) close();
    }
}
