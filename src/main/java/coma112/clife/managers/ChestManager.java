package coma112.clife.managers;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.item.IItemBuilder;
import coma112.clife.utils.LifeLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChestManager {
    private static final Random random = new Random();

    public static void generateLoot(@NotNull Inventory chestInventory, @NotNull String chestPath) {
        ConfigurationSection chestLootSection = CLife.getInstance().getConfiguration().getSection(chestPath);

        if (chestLootSection == null) {
            LifeLogger.warn("Chest loot section is null.");
            return;
        }

        List<ItemStack> potentialLoot = new ArrayList<>();

        for (String key : chestLootSection.getKeys(false)) {
            ConfigurationSection itemSection = chestLootSection.getConfigurationSection(key);

            if (itemSection == null) continue;

            String materialName = itemSection.getString("material");
            if (materialName == null) continue;

            int chance = itemSection.getInt("chance", 100);

            for (int i = 0; i < chance; i++) {
                ItemStack item = IItemBuilder.createItemFromSection(itemSection);
                potentialLoot.add(item);
            }
        }

        for (int i = 0; i < ConfigKeys.LOOT_IN_ONE_CHEST.getInt() && !potentialLoot.isEmpty(); i++) {
            ItemStack item = potentialLoot.remove(random.nextInt(potentialLoot.size()));
            chestInventory.addItem(item);
        }
    }
}
