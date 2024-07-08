package coma112.clife.utils;

import coma112.clife.CLife;
import coma112.clife.commands.CommandLife;
import coma112.clife.listeners.AppleListener;
import coma112.clife.listeners.AttackListener;
import coma112.clife.listeners.DamageListener;
import coma112.clife.listeners.PotionListener;
import org.bukkit.event.Listener;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class RegisterUtils {
    public static void registerEvents() {
        Set<Class<? extends Listener>> listenerClasses = getListenerClasses();

        for (Class<? extends Listener> clazz : listenerClasses) {
            try {
                CLife.getInstance().getServer().getPluginManager().registerEvents(clazz.newInstance(), CLife.getInstance());
            } catch (InstantiationException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
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
        //listenerClasses.add(AttackListener.class);
        return listenerClasses;
    }

}
