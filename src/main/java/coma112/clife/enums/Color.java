package coma112.clife.enums;

import coma112.clife.enums.keys.ConfigKeys;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    private Player player;

    public static Color fromString(@NotNull String color) {
        for (Color playerColor : values()) if (playerColor.name().equalsIgnoreCase(color)) return playerColor;
        return null;
    }

    public ChatColor getChatColor() {
        return switch (this) {
            case DARK_GREEN -> ChatColor.DARK_GREEN;
            case LIME -> ChatColor.GREEN;
            case YELLOW -> ChatColor.YELLOW;
            case ORANGE -> ChatColor.GOLD;
            case RED -> ChatColor.RED;
            case VIOLET -> ChatColor.LIGHT_PURPLE;
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@NotNull Player player) {
        this.player = player;
    }
}
