package coma112.clife.enums.keys;

import coma112.clife.CLife;
import coma112.clife.processor.MessageProcessor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public enum ConfigKeys {
    LANGUAGE("language"),
    DATABASE("database.type"),
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
    RTP_ENABLED("rtp-enabled"),
    CHEST_ENABLED("chest-enabled"),
    CHEST_COUNT("chest-in-area"),
    LOOT_IN_ONE_CHEST("loot-in-one-chest"),
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
    STARTING_TIME("time"),

    WEBHOOK_MATCH_START_EMBED_URL("webhook.match-start-embed.url"),
    WEBHOOK_MATCH_START_EMBED_ENABLED("webhook.match-start-embed.enabled"),
    WEBHOOK_MATCH_START_EMBED_TITLE("webhook.match-start-embed.title"),
    WEBHOOK_MATCH_START_EMBED_DESCRIPTION("webhook.match-start-embed.description"),
    WEBHOOK_MATCH_START_EMBED_COLOR("webhook.match-start-embed.color"),
    WEBHOOK_MATCH_START_EMBED_AUTHOR_NAME("webhook.match-start-embed.author-name"),
    WEBHOOK_MATCH_START_EMBED_AUTHOR_URL("webhook.match-start-embed.author-url"),
    WEBHOOK_MATCH_START_EMBED_AUTHOR_ICON("webhook.match-start-embed.author-icon"),
    WEBHOOK_MATCH_START_EMBED_FOOTER_TEXT("webhook.match-start-embed.footer-text"),
    WEBHOOK_MATCH_START_EMBED_FOOTER_ICON("webhook.match-start-embed.footer-icon"),
    WEBHOOK_MATCH_START_EMBED_THUMBNAIL("webhook.match-start-embed.thumbnail"),
    WEBHOOK_MATCH_START_EMBED_IMAGE("webhook.match-start-embed.image"),

    WEBHOOK_MATCH_END_EMBED_URL("webhook.match-end-embed.url"),
    WEBHOOK_MATCH_END_EMBED_ENABLED("webhook.match-end-embed.enabled"),
    WEBHOOK_MATCH_END_EMBED_TITLE("webhook.match-end-embed.title"),
    WEBHOOK_MATCH_END_EMBED_DESCRIPTION("webhook.match-end-embed.description"),
    WEBHOOK_MATCH_END_EMBED_COLOR("webhook.match-end-embed.color"),
    WEBHOOK_MATCH_END_EMBED_AUTHOR_NAME("webhook.match-end-embed.author-name"),
    WEBHOOK_MATCH_END_EMBED_AUTHOR_URL("webhook.match-end-embed.author-url"),
    WEBHOOK_MATCH_END_EMBED_AUTHOR_ICON("webhook.match-end-embed.author-icon"),
    WEBHOOK_MATCH_END_EMBED_FOOTER_TEXT("webhook.match-end-embed.footer-text"),
    WEBHOOK_MATCH_END_EMBED_FOOTER_ICON("webhook.match-end-embed.footer-icon"),
    WEBHOOK_MATCH_END_EMBED_THUMBNAIL("webhook.match-end-embed.thumbnail"),
    WEBHOOK_MATCH_END_EMBED_IMAGE("webhook.match-end-embed.image"),

    WEBHOOK_MATCH_KILL_EMBED_URL("webhook.match-kill-embed.url"),
    WEBHOOK_MATCH_KILL_EMBED_ENABLED("webhook.match-kill-embed.enabled"),
    WEBHOOK_MATCH_KILL_EMBED_TITLE("webhook.match-kill-embed.title"),
    WEBHOOK_MATCH_KILL_EMBED_DESCRIPTION("webhook.match-kill-embed.description"),
    WEBHOOK_MATCH_KILL_EMBED_COLOR("webhook.match-kill-embed.color"),
    WEBHOOK_MATCH_KILL_EMBED_AUTHOR_NAME("webhook.match-kill-embed.author-name"),
    WEBHOOK_MATCH_KILL_EMBED_AUTHOR_URL("webhook.match-kill-embed.author-url"),
    WEBHOOK_MATCH_KILL_EMBED_AUTHOR_ICON("webhook.match-kill-embed.author-icon"),
    WEBHOOK_MATCH_KILL_EMBED_FOOTER_TEXT("webhook.match-kill-embed.footer-text"),
    WEBHOOK_MATCH_KILL_EMBED_FOOTER_ICON("webhook.match-kill-embed.footer-icon"),
    WEBHOOK_MATCH_KILL_EMBED_THUMBNAIL("webhook.match-kill-embed.thumbnail"),
    WEBHOOK_MATCH_KILL_EMBED_IMAGE("webhook.match-kill-embed.image");


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

    public Location getLocation() {
        return CLife.getInstance().getConfiguration().getYml().getLocation(path);
    }

    public double getDouble() {
        return CLife.getInstance().getConfiguration().getYml().getDouble(path);
    }
}
