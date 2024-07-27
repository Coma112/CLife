package coma112.clife.enums.keys;

import coma112.clife.item.IItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ItemKeys {
    LEAVE_ITEM("spectator.leave-item"),
    PLAYERFINDER_ITEM("spectator.playerfinder-item");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return IItemBuilder.createItemFromString(path);
    };
}
