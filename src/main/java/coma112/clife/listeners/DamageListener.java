package coma112.clife.listeners;
import coma112.clife.CLife;
import coma112.clife.config.Config;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.managers.Match;
import coma112.clife.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class DamageListener implements Listener{
    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player damager) {
            Match match = CLife.getInstance().getMatch(victim);

            if (match != null) {
                if (!match.getColor(damager).canAttack(match.getColor(victim))) {
                    event.setCancelled(true);
                    return;
                }

                match.addTime(damager, (ConfigKeys.KILLER_DAMAGE.getInt() * (int) event.getDamage()));
                PlayerUtils.sendTitle(damager, "&a+ " + PlayerUtils.formatTime(ConfigKeys.KILLER_DAMAGE.getInt() * (int) event.getDamage()), "");
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {

        if (event.getEntity() instanceof Player victim) {
            Match match = CLife.getInstance().getMatch(victim);

            if (match != null) {
                match.removeTime(victim, (ConfigKeys.DAMAGE.getInt() * (int) event.getDamage()));
                PlayerUtils.sendTitle(victim, "&4- " + PlayerUtils.formatTime(ConfigKeys.DAMAGE.getInt() * (int) event.getDamage()), "");
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        Match match = CLife.getInstance().getMatch(victim);

        if (match != null && killer != null && match.isInMatch(killer)) {
            CLife.getDatabase().addDeath(victim);
            CLife.getDatabase().addKill(killer);
        }
    }
}
