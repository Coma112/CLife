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
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

public class ChestManager {
    public static void generateLoot(@NotNull Inventory chestInventory, @NotNull String chestPath) {
        ConfigurationSection chestLootSection = CLife.getInstance().getConfiguration().getSection(chestPath);
        List<ItemStack> potentialLoot = new ArrayList<>();

        if (chestLootSection == null) {
            LifeLogger.warn("Chest loot section is null.");
            return;
        }

        chestLootSection
                .getKeys(false)
                .stream()
                .map(chestLootSection::getConfigurationSection)
                .filter(Objects::nonNull)
                .forEach(itemSection -> {
                    if (itemSection.getString("material") != null) {
                        IntStream.range(0, itemSection.getInt("chance", 100))
                                .mapToObj(i -> IItemBuilder.createItemFromSection(itemSection))
                                .forEach(potentialLoot::add);
                    }
                });

        IntStream
                .range(0, Math.min(ConfigKeys.LOOT_IN_ONE_CHEST.getInt(), potentialLoot.size()))
                .mapToObj(i -> potentialLoot.remove(new Random().nextInt(potentialLoot.size())))
                .forEach(chestInventory::addItem);
    }
}
