package coma112.clife.listeners.webhook;

import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchStartEvent;
import coma112.clife.hooks.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.io.IOException;

import static coma112.clife.hooks.Webhook.replacePlaceholdersMatchStart;

public class MatchStartListener implements Listener {
    @EventHandler
    public void onStart(final MatchStartEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Webhook.sendWebhook(
                ConfigKeys.WEBHOOK_MATCH_START_EMBED_URL.getString(),
                ConfigKeys.WEBHOOK_MATCH_START_EMBED_ENABLED.getBoolean(),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_MATCH_START_EMBED_COLOR.getString(),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_AUTHOR_NAME.getString(), event),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_AUTHOR_URL.getString(), event),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_AUTHOR_ICON.getString(), event),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_FOOTER_TEXT.getString(), event),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_FOOTER_ICON.getString(), event),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_THUMBNAIL.getString(), event),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_TITLE.getString(), event),
                replacePlaceholdersMatchStart(ConfigKeys.WEBHOOK_MATCH_START_EMBED_IMAGE.getString(), event)
        );
    }
}
