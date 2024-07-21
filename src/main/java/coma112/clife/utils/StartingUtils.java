package coma112.clife.utils;

import coma112.clife.CLife;
import coma112.clife.managers.Match;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class StartingUtils {
    public static void registerListenersAndCommands() {
        RegisterUtils.registerEvents();
        RegisterUtils.registerCommands();
    }

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(CLife.getInstance().getDataFolder(), resourcePath).exists())
            CLife.getInstance().saveResource(resourcePath, false);
    }
}
