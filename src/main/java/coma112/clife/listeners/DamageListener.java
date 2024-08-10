package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Objects;

public class DamageListener implements Listener {
    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim) {
            Match match = Match.getMatchById(victim.getLocation().getWorld().getName());

            if (match == null) return;

            Player damager = null;

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            } else if (event.getDamager() instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player) damager = (Player) projectile.getShooter();
            }

            if (match.getSpectators().contains(damager)) {
                event.setCancelled(true);
                return;
            }

            if (damager != null) {
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

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) return;

        if (event.getEntity() instanceof Player victim) {
            Match match = Match.getMatchById(victim.getLocation().getWorld().getName());
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
                        match.recordAttack(null, victim, event.getFinalDamage());
                        LifeUtils.sendTitle(victim, "&4- " + LifeUtils.formatTime(ConfigKeys.DAMAGE.getInt() * (int) event.getDamage()), "");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getMatch(player);

        if (match == null) return;

        player.setRespawnLocation(Objects.requireNonNull(Bukkit.getWorld(Match.getMatchById(player.getLocation().getWorld().getName()).getId())).getSpawnLocation(), true);
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getMatch(player);

        if (match == null) return;

        player.teleport(Objects.requireNonNull(Bukkit.getWorld(Match.getMatchById(player.getLocation().getWorld().getName()).getId())).getSpawnLocation());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getMatchById(player.getLocation().getWorld().getName());

        if (match != null) {
            match.getPlayers().remove(player);
            CLife.getInstance().getColorManager().removeColor(player);

            if (match.getPlayers().size() <= 1) {
                CLife.getDatabase().addDeath(player);
                match.getPlayers().forEach(winner -> CLife.getDatabase().addWin(winner));
                match.endMatch();
            }
        }
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        event.blockList().forEach(block -> {
            block.getWorld().getPlayers().forEach(player -> {
                if (player.getLocation().distance(block.getLocation()) < 5) {
                    Match match = Match.getMatchById(player.getLocation().getWorld().getName());

                    if (match == null) return;

                    match.removeTime(player, (ConfigKeys.DAMAGE.getInt()));
                    LifeUtils.sendTitle(player, "&4- " + LifeUtils.formatTime(ConfigKeys.DAMAGE.getInt()), "");
                }
            });
        });
    }

    @EventHandler
    public void onEntityCombust(final EntityCombustEvent event) {
        if (event.getEntity() instanceof Player victim) {
            Match match = Match.getMatchById(victim.getLocation().getWorld().getName());

            if (match != null) {
                match.removeTime(victim, (ConfigKeys.DAMAGE.getInt()));
                LifeUtils.sendTitle(victim, "&4- " + LifeUtils.formatTime(ConfigKeys.DAMAGE.getInt()), "");
            }
        }
    }
}
