package coma112.clife.managers.stats;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public record DeathStatistics(@NotNull OfflinePlayer player, int deaths) {}
