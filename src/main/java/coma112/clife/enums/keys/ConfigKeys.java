package coma112.clife.enums.keys;

import coma112.clife.CLife;
import coma112.clife.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum ConfigKeys {
    LANGUAGE("language"),
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
