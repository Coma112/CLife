package coma112.clife.listeners.webhook;

import coma112.clife.events.MatchEndEvent;
import coma112.clife.hooks.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.net.URISyntaxException;

public class MatchEndListener implements Listener {
    @EventHandler
    public void onStart(final MatchEndEvent event) throws IOException, URISyntaxException {
        if (event.getMatch().getWinner() != null) {
            Webhook.sendWebhookFromString("webhook.match-end-embed", event);
        }
    }
}
