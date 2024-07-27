package coma112.clife.services;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardService {
    @Getter public static final Map<Player, FastBoard> boards = new HashMap<>();

    private static void updateMatchBoard(@NotNull FastBoard board, @NotNull Player player) {
        board.updateLines(ConfigKeys.MATCH_SCOREBOARD_LINES.getScoreboardList(player));
        board.updateTitle(ConfigKeys.MATCH_SCOREBOARD_TITLE.getString());
    }

    public static void giveMatchBoard(@NotNull Player player) {
        CLife.getInstance().getScheduler().runTaskTimer(() -> ScoreboardService.getBoards().values().forEach(boards -> updateMatchBoard(boards, player)), 0, 20);
        getBoards().put(player, new FastBoard(player));
    }

    public static void removeMatchBoard(@NotNull Player player) {
        FastBoard board = getBoards().remove(player);

        if (board != null) board.delete();
    }
}
