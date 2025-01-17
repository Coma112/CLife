package coma112.clife.utils;

import coma112.clife.CLife;
import coma112.clife.commands.CommandLife;
import coma112.clife.listeners.*;
import coma112.clife.listeners.webhook.MatchEndListener;
import coma112.clife.listeners.webhook.MatchKillListener;
import coma112.clife.listeners.webhook.MatchStartListener;
import coma112.clife.menu.MenuListener;
import org.bukkit.event.Listener;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class RegisterUtils {
    public static void registerEvents() {
        getListenerClasses().forEach(clazz -> {
            try {
                CLife.getInstance().getServer().getPluginManager().registerEvents(clazz.newInstance(), CLife.getInstance());
            } catch (InstantiationException | IllegalAccessException exception) {
                LifeLogger.error(exception.getMessage());
            }
        });
    }

    public static void registerCommands() {
        BukkitCommandHandler handler = BukkitCommandHandler.create(CLife.getInstance());

        handler.register(new CommandLife());
    }

    private static Set<Class<? extends Listener>> getListenerClasses() {
        Set<Class<? extends Listener>> listenerClasses = new HashSet<>();

        listenerClasses.add(AppleListener.class);
        listenerClasses.add(DamageListener.class);
        listenerClasses.add(PotionListener.class);
        listenerClasses.add(MatchStartListener.class);
        listenerClasses.add(MatchEndListener.class);
        listenerClasses.add(MatchKillListener.class);
        listenerClasses.add(DatabaseListener.class);
        listenerClasses.add(SpectatorListener.class);
        listenerClasses.add(WorldListener.class);
        listenerClasses.add(MovementListener.class);
        listenerClasses.add(MenuListener.class);

        return listenerClasses;
    }
}
