package coma112.clife.enums.keys;

import coma112.clife.CLife;
import coma112.clife.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum MessageKeys {
    RELOAD("messages.reload");

    private final String path;

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public String getMessage() {
        return MessageProcessor.process(CLife.getInstance().getLanguage().getString(path));
    }

}
