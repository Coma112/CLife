package coma112.clife.world;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.utils.LifeUtils;
import lombok.Getter;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@SuppressWarnings("deprecation")
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

        Location spawnLocation = new Location(generatedWorld, 0, 70, 0);
        ChunkGenerator generator = new FixedSpawnChunkGenerator(spawnLocation);

        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.NORMAL);
        creator.generator(generator);
        creator.keepSpawnLoaded(TriState.byBoolean(false));


        if (generatedWorld != null) {
            generatedWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            generatedWorld.setKeepSpawnInMemory(false);
            generatedWorld.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
            generatedWorld.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

                generatedWorld.getChunkAt(spawnLocation).load();
                CLife.getDatabase().saveWorldID(uniqueID);
                generated = true;
        }

        return generatedWorld;
    }
}
