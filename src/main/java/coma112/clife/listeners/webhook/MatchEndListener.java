package coma112.clife.listeners.webhook;

import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchEndEvent;
import coma112.clife.hooks.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

import static coma112.clife.hooks.Webhook.replacePlaceholdersMatchEnd;

public class MatchEndListener implements Listener {
    @EventHandler
    public void onStart(final MatchEndEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Webhook.sendWebhook(
                ConfigKeys.WEBHOOK_MATCH_END_EMBED_URL.getString(),
                ConfigKeys.WEBHOOK_MATCH_END_EMBED_ENABLED.getBoolean(),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_MATCH_END_EMBED_COLOR.getString(),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_AUTHOR_NAME.getString(), event),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_AUTHOR_URL.getString(), event),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_AUTHOR_ICON.getString(), event),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_FOOTER_TEXT.getString(), event),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_FOOTER_ICON.getString(), event),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_THUMBNAIL.getString(), event),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_TITLE.getString(), event),
                replacePlaceholdersMatchEnd(ConfigKeys.WEBHOOK_MATCH_END_EMBED_IMAGE.getString(), event)
        );
    }
}
