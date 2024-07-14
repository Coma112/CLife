package coma112.clife.enums.keys;

import coma112.clife.CLife;
import coma112.clife.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum ConfigKeys {
    LANGUAGE("language"),
    DATABASE("database"),
    COLOR_RED("color-name.red"),
    COLOR_LIME("color-name.lime"),
    COLOR_DARK_GREEN("color-name.dark-green"),
    COLOR_YELLOW("color-name.yellow"),
    COLOR_ORANGE("color-name.orange"),
    COLOR_VIOLET("color-name.violet"),
    COUNTDOWN("countdown.time"),
    MINIMUM_PLAYERS("minimum-players"),
    COUNTDOWN_TITLE("countdown.title"),
    DAMAGE("damage-per-half-heart"),
    KILLER_DAMAGE("add-time-per-half-heart"),
    END_TITLE("end.title"),
    END_BROADCAST("end.broadcast"),
    END_SUBTITLE("end.subtitle"),
    COUNTDOWN_SUBTITLE("countdown.subtitle"),
    ACTION_BAR("action-bar"),
    ENCHANTED_GOLDEN_APPLE_TIME("egapple-time"),
    GOLDEN_APPLE_TIME("gapple-time"),
    HEALING_POTION("potions.healing"),
    REGENERATION_POTION("potions.regeneration"),
    STARTING_TIME("time");


    private final String path;

    ConfigKeys(@NotNull final String path) {
        this.path = path;
    }

    public String getString() {
        return MessageProcessor.process(CLife.getInstance().getConfiguration().getString(path));
    }

    public boolean getBoolean() {
        return CLife.getInstance().getConfiguration().getBoolean(path);
    }

    public int getInt() {
        return CLife.getInstance().getConfiguration().getInt(path);
    }
}
