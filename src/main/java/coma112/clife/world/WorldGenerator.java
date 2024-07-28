package coma112.clife.world;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.utils.LifeUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.Objects;


@Getter
public class WorldGenerator {
    private static boolean generated = false;
    private static World generatedWorld;

    public static void setFalse() {
        generated = false;
        generatedWorld = null;
    }

    public static World generateWorld() {
        if (generated) return generatedWorld;

        String uniqueID = LifeUtils.generateUniqueID();
        WorldCreator creator = new WorldCreator(uniqueID);
        generatedWorld = Bukkit.createWorld(creator);

        LifeUtils.setWorldBorder(Objects.requireNonNull(generatedWorld).getSpawnLocation(), ConfigKeys.RADIUS.getInt());

        CLife.getDatabase().saveWorldID(uniqueID);

        if (generatedWorld != null) generated = true;
        return generatedWorld;
    }
}
