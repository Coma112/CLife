package coma112.clife.listeners.webhook;

import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchKillEvent;
import coma112.clife.hooks.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

import static coma112.clife.hooks.Webhook.replacePlaceholdersMatchKill;

public class MatchKillListener implements Listener {
    @EventHandler
    public void onStart(final MatchKillEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Webhook.sendWebhook(
                ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_URL.getString(),
                ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_ENABLED.getBoolean(),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_COLOR.getString(),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_AUTHOR_NAME.getString(), event),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_AUTHOR_URL.getString(), event),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_AUTHOR_ICON.getString(), event),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_FOOTER_TEXT.getString(), event),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_FOOTER_ICON.getString(), event),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_THUMBNAIL.getString(), event),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_TITLE.getString(), event),
                replacePlaceholdersMatchKill(ConfigKeys.WEBHOOK_MATCH_KILL_EMBED_IMAGE.getString(), event)
        );
    }
}
