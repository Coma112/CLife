package coma112.clife.database;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDatabase {
    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract void addDeath(@NotNull Player player);

    public abstract void addKill(@NotNull Player player);

    public abstract void addWin(@NotNull Player player);

    public abstract int getDeaths(@NotNull Player player);

    public abstract int getKills(@NotNull Player player);

    public abstract int getWins(@NotNull Player player);
}
