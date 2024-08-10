package coma112.clife.listeners.webhook;

import coma112.clife.events.MatchStartEvent;
import coma112.clife.hooks.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.net.URISyntaxException;


public class MatchStartListener implements Listener {
    @EventHandler
    public void onStart(final MatchStartEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.match-start-embed", event);
    }
}
