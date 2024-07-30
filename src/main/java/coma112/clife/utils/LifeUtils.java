package coma112.clife.utils;

import coma112.clife.processor.MessageProcessor;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class LifeUtils {
    private static final String DEFAULT_SERIALIZED_LOCATION = Bukkit.getWorlds().getFirst().getName() + ";0;0;0;0.0;0.0";
    private static final Location DEFAULT_DESERIALIZED_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 0.0, 0.0, 0.0, 0.0F, 0.0F);

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
}
