package coma112.clife.managers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import coma112.clife.enums.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ColorManager {
    private final Map<Player, Color> playerColors = new ConcurrentHashMap<>();

    public void setColor(@NotNull Player player, @NotNull Color color) {
        playerColors.put(player, color);
    }

    public Color getColor(@NotNull Player player) {
        return playerColors.get(player);
    }

    public void removeColor(@NotNull Player player) {
        playerColors.remove(player);
    }
}
