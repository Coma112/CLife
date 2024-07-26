package coma112.clife.world;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.utils.LifeLogger;
import coma112.clife.utils.LifeUtils;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.scheduler.BukkitRunnable;

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
        MVWorldManager worldManager = CLife.getCore().getMVWorldManager();
        String uniqueID = LifeUtils.generateUniqueID();
        CLife.getDatabase().saveWorldID(uniqueID);

        worldManager.addWorld(
                uniqueID,
                World.Environment.NORMAL,
                null,
                WorldType.NORMAL,
                true,
                null
        );

        worldManager.loadWorld(uniqueID);

        World world = Bukkit.getWorld(uniqueID);
        generatedWorld = world;

        LifeUtils.setWorldBorder(Objects.requireNonNull(world).getSpawnLocation(), ConfigKeys.RADIUS.getInt());
        return world;
    }
}
