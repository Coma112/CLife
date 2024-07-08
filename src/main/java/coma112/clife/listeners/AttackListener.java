package coma112.clife.listeners;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.managers.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class AttackListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player damager) {
            Match match = CLife.getInstance().getMatch(victim);

            if (match != null) {
                if (!canAttack(match.getColor(damager), match.getColor(victim))) event.setCancelled(true);
            }
        }
    }

    private boolean canAttack(@NotNull Color damageColor, @NotNull Color victimColor) {
        switch (damageColor) {
            case YELLOW, ORANGE -> {
                return victimColor == Color.DARK_GREEN || victimColor == Color.LIME;
            }

            case RED -> {
                return victimColor != Color.VIOLET;
            }

            case VIOLET -> {
                return true;
            }

            default -> {
                return false;
            }
        }
    }
}
