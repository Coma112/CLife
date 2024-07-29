package coma112.clife.menu;

import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.item.IItemBuilder;
import coma112.clife.utils.MenuUtils;
import lombok.Getter;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    @Getter protected int maxItemsPerPage = ConfigKeys.MENU_SIZE.getInt() - 2;

    public PaginatedMenu(MenuUtils menuUtils) {
        super(menuUtils);
    }

    public void addMenuBorder() {
        inventory.setItem(ConfigKeys.BACK_ITEM_SLOT.getInt(), IItemBuilder.createItemFromString("menu.back-item"));
        inventory.setItem(ConfigKeys.FORWARD_ITEM_SLOT.getInt(), IItemBuilder.createItemFromString("menu.forward-item"));
    }
}

