package coma112.clife.utils;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.ChestManager;
import coma112.clife.managers.Match;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Material;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.io.File;
import java.security.SecureRandom;
import java.util.List;
import org.bukkit.GameRule;
import org.bukkit.Bukkit;
import static coma112.clife.enums.Color.getUpperLimit;

public class MatchUtils {
    @Getter
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    @Getter
    private static final SecureRandom random = new SecureRandom();
    @Getter
    public static HashSet<Material> blockedBlocks = new HashSet<>();

    public static void setWorldBorder(@NotNull Location center, double radius) {
        World world = center.getWorld();

        if (world == null) {
            LifeLogger.warn("World is not available to set the world border.");
            return;
        }

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(center);
        worldBorder.setSize(radius * 2);
        worldBorder.setDamageAmount(2.0);
        worldBorder.setDamageBuffer(5.0);
        worldBorder.setWarningDistance(10);
        worldBorder.setWarningTime(5);
    }

    public static void updateMatchColor(@NotNull Match match, @NotNull Player player, @NotNull Player target, @NotNull Color color) {
        Map<Color, String> colorMessages = Map.of(
                coma112.clife.enums.Color.DARK_GREEN, MessageKeys.SUCCESSFUL_SETCOLOR.getMessage().replace("{color}", coma112.clife.enums.Color.DARK_GREEN.getName()).replace("{target}", target.getName()),
                coma112.clife.enums.Color.LIME, MessageKeys.SUCCESSFUL_SETCOLOR.getMessage().replace("{color}", coma112.clife.enums.Color.LIME.getName()).replace("{target}", target.getName()),
                coma112.clife.enums.Color.YELLOW, MessageKeys.SUCCESSFUL_SETCOLOR.getMessage().replace("{color}", coma112.clife.enums.Color.YELLOW.getName()).replace("{target}", target.getName()),
                coma112.clife.enums.Color.ORANGE, MessageKeys.SUCCESSFUL_SETCOLOR.getMessage().replace("{color}", coma112.clife.enums.Color.ORANGE.getName()).replace("{target}", target.getName()),
                coma112.clife.enums.Color.RED, MessageKeys.SUCCESSFUL_SETCOLOR.getMessage().replace("{color}", coma112.clife.enums.Color.RED.getName()).replace("{target}", target.getName()),
                coma112.clife.enums.Color.VIOLET, MessageKeys.SUCCESSFUL_SETCOLOR.getMessage().replace("{color}", Color.VIOLET.getName()).replace("{target}", target.getName())
        );

        match.setTime(target, getUpperLimit(color));
        player.sendMessage(colorMessages.get(color).replace("{target}", target.getName()));
    }

    public static Location findSafeLocation(@NotNull Location center, double radius) {
        Random random = new Random();
        int maxAttempts = 5;
        int maxRetries = 50;
        Set<Location> triedLocations = new HashSet<>();
        Location safeLocation = null;

        for (int retry = 0; retry < maxRetries; retry++) {
            int attempt = 0;
            boolean foundSafeLocation = false;

            while (attempt < maxAttempts) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double distance = random.nextDouble() * radius;
                double xOffset = Math.cos(angle) * distance;
                double zOffset = Math.sin(angle) * distance;
                int x = (int) Math.floor(center.getX() + xOffset);
                int z = (int) Math.floor(center.getZ() + zOffset);
                Location potentialLocation = new Location(center.getWorld(), x, center.getWorld().getHighestBlockYAt(x, z), z);

                if (!triedLocations.contains(potentialLocation) && isSafeLocation(potentialLocation)) {
                    safeLocation = potentialLocation;
                    foundSafeLocation = true;
                    break;
                }

                triedLocations.add(potentialLocation);
                attempt++;
            }

            if (foundSafeLocation) return safeLocation;
        }

        return safeLocation;
    }

    public static boolean isSafeLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;

        int x = location.getBlockX();
        int z = location.getBlockZ();
        int y = world.getHighestBlockYAt(x, z);

        Block block = world.getBlockAt(x, y, z);
        Block below = world.getBlockAt(x, y - 1, z);
        Block above = world.getBlockAt(x, y + 1, z);

        if (getBlockedBlocks().contains(block.getType())) return false;
        if (getBlockedBlocks().contains(below.getType())) return false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block adjacentBlock = world.getBlockAt(location.clone().add(dx, dy, dz));
                    if (getBlockedBlocks().contains(adjacentBlock.getType())) return false;
                }
            }
        }

        return above.getType() == Material.AIR;
    }

    public static void loadBlockedBlocks() {
        List<String> blockedBlocks = CLife.getInstance().getConfiguration().getList("blocked-blocks");

        if (blockedBlocks == null || blockedBlocks.isEmpty()) {
            LifeLogger.error("No 'blocked-blocks' section found in configuration or it is empty.");
            return;
        }

        for (String key : blockedBlocks) {
            try {
                Match.getBlockedBlocks().add(Material.valueOf(key.toUpperCase()));
            } catch (IllegalArgumentException exception) {
                LifeLogger.error(exception.getMessage());
            }
        }
    }

    public static void setupMatchWorld(@NotNull World world) {
        if (ConfigKeys.ALWAYS_DAY.getBoolean()) {
            world.setTime(6000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
    }

    public static void fillChestWithLoot(@NotNull Location chestLocation) {
        Chest chest = (Chest) chestLocation.getBlock().getState();
        Inventory inventory = chest.getBlockInventory();
        ChestManager.generateLoot(inventory, "chest-loot");
    }

    public static String generateUniqueID() {
        String uniqueID;

        do {
            uniqueID = generateRandomID();
        } while (CLife.getDatabase().isIDExists(uniqueID));
        return uniqueID;
    }

    public static void deleteWorld(World world) {
        if (world == null) {
            LifeLogger.warn("Cannot delete world because the world is null.");
            return;
        }

        CLife.getInstance().getScheduler().runTask(() -> {
            Bukkit.unloadWorld(world, false);
            File worldFolder = world.getWorldFolder();
            CLife.getDatabase().removeWorldID(world.getName());
            deleteRecursively(worldFolder);
        });
    }

    private static void deleteRecursively(@NotNull File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            if (files != null) {
                for (File f : files) deleteRecursively(f);
            }
        }

        file.delete();
    }


    private static String generateRandomID() {
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int index = getRandom().nextInt(getAlphabet().length());
            id.append(getAlphabet().charAt(index));
        }

        return id.toString();
    }
}
