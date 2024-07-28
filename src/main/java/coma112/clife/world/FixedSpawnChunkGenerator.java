package coma112.clife.world;

import org.bukkit.Location;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FixedSpawnChunkGenerator extends ChunkGenerator {
    private final Location fixedSpawnLocation;

    public FixedSpawnChunkGenerator(Location fixedSpawnLocation) {
        this.fixedSpawnLocation = fixedSpawnLocation;
    }

    @Override
    public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return fixedSpawnLocation;
    }
}
