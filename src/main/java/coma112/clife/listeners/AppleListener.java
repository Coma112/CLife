package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class AppleListener implements Listener {
    @EventHandler
    public void onConsume(final PlayerItemConsumeEvent event) {
        Material item = event.getItem().getType();
        Player player = event.getPlayer();
        Match match = CLife.getInstance().getMatch(player);

        int golden_apple = ConfigKeys.GOLDEN_APPLE_TIME.getInt();
        int enchanted_golden_apple = ConfigKeys.ENCHANTED_GOLDEN_APPLE_TIME.getInt();

        switch (item) {
            case GOLDEN_APPLE -> {
                if (match != null) match.addTime(player, golden_apple);
                LifeUtils.sendTitle(player, "&a+ " + LifeUtils.formatTime(golden_apple), "");
            }

            case ENCHANTED_GOLDEN_APPLE -> {
                if (match != null) match.addTime(player, golden_apple);
                LifeUtils.sendTitle(player, "&a+ " + LifeUtils.formatTime(enchanted_golden_apple), "");
            }
        }
    }
}
