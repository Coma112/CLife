package coma112.clife.world;

import coma112.clife.CLife;
import coma112.clife.utils.MatchUtils;
import lombok.Getter;
import net.kyori.adventure.util.TriState;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldType;
import org.bukkit.GameRule;

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

        String uniqueID = MatchUtils.generateUniqueID();
        WorldCreator creator = new WorldCreator(uniqueID);
        generatedWorld = Bukkit.createWorld(creator);
        Location spawnLocation = new Location(generatedWorld, 0, 70, 0);
        ChunkGenerator generator = new FixedSpawnChunkGenerator(spawnLocation);

        creator.keepSpawnLoaded(TriState.byBoolean(false));
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.NORMAL);
        creator.generator(generator);

        if (generatedWorld != null) {
            generatedWorld.setKeepSpawnInMemory(false);
            generatedWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

            generatedWorld.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
            generatedWorld.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

            generatedWorld.getChunkAt(spawnLocation).load();
            CLife.getDatabase().saveWorldID(uniqueID);
            generated = true;
        }

        return generatedWorld;
    }
}
