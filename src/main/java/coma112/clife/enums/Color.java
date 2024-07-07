package coma112.clife.enums;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("deprecation")
public enum Color {
    DARK_GREEN,
    LIME,
    YELLOW,
    ORANGE,
    RED,
    VIOLET;

    // YELLOW & ORANGE -> DARK GREEN & LIME
    // RED -> EVERYONE EXCEPT VIOLET
    // VIOLET -> EVERYONE

    public static Color fromString(@NotNull String color) {
        for (Color playerColor : values()) if (playerColor.name().equalsIgnoreCase(color)) return playerColor;
        return null;
    }

    public static Color getColorForTime(int time) {
        Color highestColor = DARK_GREEN;
        int highestUpperLimit = Integer.MIN_VALUE;

        for (Color color : values()) {
            String range = CLife.getInstance().getConfiguration().getString("color-per-life." + color.name().toLowerCase().replace("_", "-"));

            if (range != null) {
                String[] limits = range.split("-");
                int lower = Integer.parseInt(limits[1]);
                int upper = Integer.parseInt(limits[0]);

                if (time >= lower && time < upper) return color;

                if (upper > highestUpperLimit) {
                    highestUpperLimit = upper;
                    highestColor = color;
                }
            }
        }

        if (time >= highestUpperLimit) return highestColor;
        return VIOLET;
    }

    public String getColorCode() {
        return switch (this) {
            case DARK_GREEN -> "&2";
            case LIME -> "&a";
            case YELLOW -> "&e";
            case ORANGE -> "&6";
            case RED -> "&c";
            case VIOLET -> "&d";
        };
    }

    public String getName() {
        return switch (this) {
            case DARK_GREEN -> ConfigKeys.COLOR_DARK_GREEN.getString();
            case LIME -> ConfigKeys.COLOR_LIME.getString();
            case YELLOW -> ConfigKeys.COLOR_YELLOW.getString();
            case ORANGE -> ConfigKeys.COLOR_ORANGE.getString();
            case RED -> ConfigKeys.COLOR_RED.getString();
            case VIOLET -> ConfigKeys.COLOR_VIOLET.getString();
        };
    }
}
