package coma112.clife.database;

import coma112.clife.managers.stats.DeathStatistics;
import coma112.clife.managers.stats.KillStatistics;
import coma112.clife.managers.stats.WinsStatistics;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractDatabase {
    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract void createTable();

    public abstract void createPlayer(@NotNull Player player);

    public abstract boolean exists(@NotNull Player player);

    public abstract void addDeath(@NotNull Player player);

    public abstract void addKill(@NotNull Player player);

    public abstract void addWin(@NotNull Player player);

    public abstract int getDeaths(@NotNull Player player);

    public abstract int getKills(@NotNull Player player);

    public abstract int getWins(@NotNull Player player);

    public abstract void reconnect();

    public abstract String getID(@NotNull World world);

    public abstract void saveWorldID(@NotNull String worldID);

    public abstract void removeWorldID(@NotNull String worldID);

    public abstract boolean isIDExists(@NotNull String worldID);

    public abstract List<String> getWorlds();

    public abstract int getKillStatistics(int number);

    public abstract int getDeathStatistics(int number);

    public abstract String getTopKillsPlayer(int top);

    public abstract int getWinStatistics(int number);

    public abstract String getTopDeathsPlayer(int top);

    public abstract String getTopWinsPlayer(int top);
}
