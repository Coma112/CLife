package coma112.clife.utils;

import coma112.clife.processor.MessageProcessor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class PlayerUtils {
    public static final String DEFAULT_LOCATION_SERIALIZED = Bukkit.getWorlds().getFirst().getName() + ";0;0;0;0.0;0.0";
    public static final Location DEFAULT_LOCATION_DESERIALIZED = new Location(Bukkit.getWorlds().getFirst(), 0.0, 0.0, 0.0, 0.0F, 0.0F);

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
        return location == null ? DEFAULT_LOCATION_SERIALIZED : Objects.requireNonNull(location.getWorld()).getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Location convertStringToLocation(@Nullable String s) {
        if (s == null) return null;
        String[] split = s.split(";");

        return split.length != 6 ? DEFAULT_LOCATION_DESERIALIZED : new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }
}
