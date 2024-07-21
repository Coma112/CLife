package coma112.clife.utils;

import coma112.clife.enums.Color;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
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
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static coma112.clife.enums.Color.getUpperLimit;

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
}
