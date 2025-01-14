package coma112.clife.enums.keys;

import coma112.clife.CLife;
import coma112.clife.processor.MessageProcessor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConfigKeys {
    // Language and Database
    LANGUAGE("language"),
    DATABASE("database.type"),

    // Colors
    COLOR_RED("color-name.red"),
    COLOR_LIME("color-name.lime"),
    COLOR_DARK_GREEN("color-name.dark-green"),
    COLOR_YELLOW("color-name.yellow"),
    COLOR_ORANGE("color-name.orange"),
    COLOR_VIOLET("color-name.violet"),
    COLOR_BROADCAST("color.broadcast"),
    COLOR_PLAYER_TITLE("color.player-title"),
    COLOR_PLAYER_SUBTITLE("color.player-subtitle"),

    // Countdown and Timing
    COUNTDOWN("countdown.time"),
    COUNTDOWN_TITLE("countdown.title"),
    COUNTDOWN_SUBTITLE("countdown.subtitle"),
    STARTING_TIME("time"),
    ENCHANTED_GOLDEN_APPLE_TIME("egapple-time"),
    GOLDEN_APPLE_TIME("gapple-time"),

    // Players and Damage
    MINIMUM_PLAYERS("minimum-players"),
    DAMAGE("damage-per-half-heart"),
    KILLER_DAMAGE("add-time-per-half-heart"),
    EVERYONE_CAN_ATTACK("everyone-can-attack"),

    // Game Settings
    RTP_ENABLED("rtp-enabled"),
    CHEST_ENABLED("chest-enabled"),
    CHEST_COUNT("chest-in-area"),
    LOOT_IN_ONE_CHEST("loot-in-one-chest"),
    ALWAYS_DAY("always-day"),

    // End Game
    END_TITLE("end.title"),
    END_BROADCAST("end.broadcast"),
    END_SUBTITLE("end.subtitle"),

    // Action Bars and Menus
    ACTION_BAR("action-bar"),
    QUEUE_ACTIONBAR("queue.action-bar"),
    MENU_TICK("menu.update-tick"),
    MENU_SIZE("menu.size"),
    MENU_TITLE("menu.title"),
    BACK_ITEM_SLOT("menu.back-item.slot"),
    FORWARD_ITEM_SLOT("menu.forward-item.slot"),

    // Spectator Items
    LEAVE_ITEM_SLOT("spectator.leave-item.slot"),
    PLAYERFINDER_ITEM_SLOT("spectator.playerfinder-item.slot"),

    // Scoreboards
    MATCH_SCOREBOARD_TITLE("scoreboards.match-scoreboard.title"),
    MATCH_SCOREBOARD_LINES("scoreboards.match-scoreboard.lines"),

    // Death Messages
    DEATH_VICTIM_TITLE("death.victim-title"),
    DEATH_VICTIM_SUBTITLE("death.victim-subtitle"),
    DEATH_BROADCAST_PLAYER("death.broadcast-player"),
    DEATH_BROADCAST_NOPLAYER("death.broadcast-noplayer"),

    // World Settings
    RADIUS("world-radius"),

    // Queue Settings
    QUEUE_MAX("queue.maxplayer"),

    // Potions
    HEALING_POTION("potions.healing"),
    REGENERATION_POTION("potions.regeneration"),

    // Webhooks for Match Start
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

    // Webhooks for Match End
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

    // Webhooks for Match Kill
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

    public List<String> getScoreboardList(@NotNull Player player) {
        return MessageProcessor.processList(PlaceholderAPI.setPlaceholders(player, CLife.getInstance().getConfiguration().getList(path)));
    }
}
