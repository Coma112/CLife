package coma112.clife.hooks;

import coma112.clife.CLife;
import coma112.clife.managers.Match;
import coma112.clife.utils.PlayerUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class PlaceholderAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "cl";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Coma112 & B3rcyy (Vulcan Studio)";
    }

    @Override
    public @NotNull String getVersion() {
        return CLife.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
        Match match = CLife.getInstance().getMatch(player);

        return switch (params) {
            case "color_string" -> {
                if (match == null) yield "";
                else yield match.getColor(player).getName();
            }

            case "color_code" -> {
                if (match == null) yield "";
                else yield match.getColor(player).getColorCode();
            }

            case "time" -> {
                if (match == null) yield "";
                else yield PlayerUtils.formatTime(match.getTime(player));
            }

            case "wins" -> String.valueOf(CLife.getDatabase().getWins(player));
            case "deaths" -> String.valueOf(CLife.getDatabase().getDeaths(player));
            case "kills" -> String.valueOf(CLife.getDatabase().getKills(player));
            default -> throw new IllegalStateException("Unexpected value: " + params);
        };
    }


    public static void registerHook() {
        new PlaceholderAPI().register();
    }
}
