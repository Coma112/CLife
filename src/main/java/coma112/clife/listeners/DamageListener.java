package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.events.MatchKillEvent;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageListener implements Listener {
    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim) {
            Player damager = null;

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            } else if (event.getDamager() instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player) damager = (Player) projectile.getShooter();
            }

            if (damager != null) {
                Match match = CLife.getInstance().getMatch(victim);

                if (match != null) {
                    if (!ConfigKeys.EVERYONE_CAN_ATTACK.getBoolean() && !match.getColor(damager).canAttack(match.getColor(victim))) {
                        event.setCancelled(true);
                        return;
                    }

                    match.addTime(damager, (ConfigKeys.KILLER_DAMAGE.getInt() * (int) event.getDamage()));
                    LifeUtils.sendTitle(damager, "&a+ " + LifeUtils.formatTime(ConfigKeys.KILLER_DAMAGE.getInt() * (int) event.getDamage()), "");
                    match.removeTime(victim, (ConfigKeys.DAMAGE.getInt() * (int) event.getDamage()));
                    LifeUtils.sendTitle(victim, "&4- " + LifeUtils.formatTime(ConfigKeys.DAMAGE.getInt() * (int) event.getDamage()), "");
                    match.recordAttack(damager, victim, event.getFinalDamage());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) return;

        if (event.getEntity() instanceof Player victim) {
            Match match = CLife.getInstance().getMatch(victim);
            DamageCause cause = event.getCause();

            if (match != null) {
                switch (cause) {
                    case FIRE_TICK,
                         FIRE,
                         BLOCK_EXPLOSION,
                         ENTITY_EXPLOSION,
                         CAMPFIRE,
                         FALL -> {
                        match.removeTime(victim, (ConfigKeys.DAMAGE.getInt() * (int) event.getDamage()));
                        LifeUtils.sendTitle(victim, "&4- " + LifeUtils.formatTime(ConfigKeys.DAMAGE.getInt() * (int) event.getDamage()), "");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        Match match = null;
        for (Block block : event.blockList()) {
            for (Player player : block.getWorld().getPlayers()) {
                if (player.getLocation().distance(block.getLocation()) < 5) {
                    if (match == null) match = CLife.getInstance().getMatch(player);

                    if (match != null) {
                        match.removeTime(player, (ConfigKeys.DAMAGE.getInt() * 10));
                        LifeUtils.sendTitle(player, "&4- " + LifeUtils.formatTime(ConfigKeys.DAMAGE.getInt() * 10), "");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityCombust(final EntityCombustEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player victim) {
            Match match = CLife.getInstance().getMatch(victim);

            if (match != null) {
                match.removeTime(victim, (ConfigKeys.DAMAGE.getInt() * (int) event.getDuration()));
                LifeUtils.sendTitle(victim, "&4- " + LifeUtils.formatTime(ConfigKeys.DAMAGE.getInt() * (int) event.getDuration()), "");
            }
        }
    }
}
