package coma112.clife.utils;

import coma112.clife.CLife;
import org.bukkit.Bukkit;
import org.bukkit.World;
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

    public static void deleteWorlds() {
        List<String> worldsOnServer = Bukkit.getWorlds().stream()
                .map(World::getName)
                .toList();

        CLife.getDatabase().getWorlds().forEach(worldID -> {
            if (!worldsOnServer.contains(worldID)) {
                World world = Bukkit.getWorld(worldID);
                if (world != null) LifeUtils.deleteWorld(world);
                else LifeLogger.error("World " + worldID + " does not exist on the server.");
            }
        });
    }
}
