package coma112.clife.utils;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.ChestManager;
import coma112.clife.managers.Match;
import coma112.clife.processor.MessageProcessor;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Objects;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.List;

import static coma112.clife.enums.Color.getUpperLimit;

@SuppressWarnings("deprecation")
public class LifeUtils {
    private static final String DEFAULT_SERIALIZED_LOCATION = Bukkit.getWorlds().getFirst().getName() + ";0;0;0;0.0;0.0";
    private static final Location DEFAULT_DESERIALIZED_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 0.0, 0.0, 0.0, 0.0F, 0.0F);
    @Getter public static HashSet<Material> blockedBlocks = new HashSet<>();
    @Getter private static double originalBorderSize;
    @Getter private static Location originalBorderCenter;
    @Getter private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    @Getter private static final SecureRandom random = new SecureRandom();

    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageProcessor.process(message)));
    }

    public static void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle) {
        player.sendTitle(MessageProcessor.process(title), MessageProcessor.process(subTitle));
    }

    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public static String convertLocationToString(@Nullable Location location) {
        return location == null ? DEFAULT_SERIALIZED_LOCATION : Objects.requireNonNull(location.getWorld()).getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Location convertStringToLocation(@Nullable String string) {
        if (string == null) return null;
        String[] split = string.split(";");

        return split.length != 6 ? DEFAULT_DESERIALIZED_LOCATION : new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

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
        Map<coma112.clife.enums.Color, String> colorMessages = Map.of(
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
