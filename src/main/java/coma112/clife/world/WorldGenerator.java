package coma112.clife.world;

import coma112.clife.CLife;
import coma112.clife.utils.LifeUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

@Getter
public class WorldGenerator {
    private static boolean generated = false;
    private static World generatedWorld;

    public static World generateWorld() {
        if (generated) {
            return generatedWorld;
        }

        String uniqueID = LifeUtils.generateUniqueID();
        CLife.getDatabase().saveWorldID(uniqueID);

        WorldCreator creator = new WorldCreator(uniqueID);
        generatedWorld = Bukkit.createWorld(creator);

        if (generatedWorld != null) {
            generated = true;
        }

        return generatedWorld;
    }

    public static boolean isWorldGenerated() {
        return generated;
    }

    public static void setfalse() {
        generated = false;
        generatedWorld = null; // Also reset the world reference
    }
}