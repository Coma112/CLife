package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("all")
public class PotionListener implements Listener {
    @EventHandler
    public void onConsume(final PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Match match = CLife.getInstance().getMatch(player);

        int healing_potion = ConfigKeys.HEALING_POTION.getInt();
        int regeneration_potion = ConfigKeys.REGENERATION_POTION.getInt();

        if (!item.hasItemMeta()) return;

        if (item.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            PotionData data = meta.getBasePotionData();

            if (data.getType().getEffectType() == PotionEffectType.REGENERATION) {
                if (match != null) match.addTime(player, regeneration_potion);
                LifeUtils.sendTitle(player, "&a+ " + LifeUtils.formatTime(regeneration_potion), "");
            }

            if (data.getType().getEffectType() == PotionEffectType.INSTANT_HEALTH) {
                if (match != null) match.addTime(player, healing_potion);
                LifeUtils.sendTitle(player, "&a+ " + LifeUtils.formatTime(healing_potion), "");
            }
        }

    }
}
