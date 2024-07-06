package coma112.clife.hooks;

import coma112.clife.CLife;
import coma112.clife.managers.Match;
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

        if (match == null) return "N/A";

        return switch (params) {
            case "color_string" -> match.getColor(player).getName();
            case "color_code" -> match.getColor(player).getColorCode();
            case "time" -> match.getTime(player);
            default -> null;
        };
    }


    public static void registerHook() {
        new PlaceholderAPI().register();
    }
}
