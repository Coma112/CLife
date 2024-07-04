package coma112.clife.utils;

import coma112.clife.CLife;
import coma112.clife.update.UpdateChecker;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@SuppressWarnings("deprecation")
public class StartingUtils {
    public static void registerListenersAndCommands() {
        RegisterUtils.registerEvents();
        RegisterUtils.registerCommands();
    }

    public static void checkUpdates() {
        new UpdateChecker(00000).getVersion(version -> {
            LifeLogger.info(CLife.getInstance().getDescription().getVersion().equals(version) ? "Everything is up to date" : "You are using an outdated version! Please download the new version so that your server is always fresh! The newest version: " + version);
        });

        //Instead of 00000 we need the resource id from Spigot
    }

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(CLife.getInstance().getDataFolder(), resourcePath).exists())
            CLife.getInstance().saveResource(resourcePath, false);
    }
}
