package coma112.clife.utils;

import coma112.clife.processor.MessageProcessor;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("deprecation")
public class LifeUtils {
    private static final String DEFAULT_SERIALIZED_LOCATION = Bukkit.getWorlds().getFirst().getName() + ";0;0;0;0.0;0.0";
    private static final Location DEFAULT_DESERIALIZED_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 0.0, 0.0, 0.0, 0.0F, 0.0F);
    @Getter public static HashSet<Material> blockedBlocks = new HashSet<>();
    @Getter private static double originalBorderSize;
    @Getter private static Location originalBorderCenter;

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
}
