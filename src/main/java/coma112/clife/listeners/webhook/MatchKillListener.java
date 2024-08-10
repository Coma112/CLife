package coma112.clife.listeners.webhook;

import coma112.clife.events.MatchKillEvent;
import coma112.clife.hooks.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.net.URISyntaxException;

public class MatchKillListener implements Listener {
    @EventHandler
    public void onStart(final MatchKillEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.match-kill-embed", event);
    }
}
