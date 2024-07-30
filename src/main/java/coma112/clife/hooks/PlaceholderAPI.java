package coma112.clife.hooks;

import coma112.clife.CLife;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeUtils;
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
        Match match = Match.getMatch(player);

        if (params.startsWith("topkillsplayer_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CLife.getDatabase().getTopKillsPlayer(position) != null) return CLife.getDatabase().getTopKillsPlayer(position);
                return "---";
            } catch (Exception exception) {
                return "";
            }
        } else if (params.startsWith("topkills_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CLife.getDatabase().getKillStatistics(position) != 0) return String.valueOf(CLife.getDatabase().getKillStatistics(position));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        } else if (params.startsWith("topdeathsplayer_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CLife.getDatabase().getTopDeathsPlayer(position) != null) return CLife.getDatabase().getTopDeathsPlayer(position);
                return "---";
            } catch (Exception exception) {
                return "";
            }
        } else if (params.startsWith("topdeaths_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CLife.getDatabase().getDeathStatistics(position) != 0) return String.valueOf(CLife.getDatabase().getDeathStatistics(position));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        } else if (params.startsWith("topwinsplayer_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CLife.getDatabase().getTopWinsPlayer(position) != null) return CLife.getDatabase().getTopWinsPlayer(position);
                return "---";
            } catch (Exception exception) {
                return "";
            }
        } else if (params.startsWith("topwins_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (CLife.getDatabase().getWinStatistics(position) != 0) return String.valueOf(CLife.getDatabase().getWinStatistics(position));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

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
                else yield LifeUtils.formatTime(match.getTime(player));
            }

            case "match_id" -> {
                if (match == null) yield "";
                else yield match.getId();
            }

            case "alive_players" -> {
                if (match == null) yield "";
                else yield String.valueOf(match.getPlayers().size());
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
