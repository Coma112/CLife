package coma112.clife.managers;

import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.utils.LifeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import coma112.clife.enums.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class ColorManager {
    private final Map<Player, Color> playerColors = new ConcurrentHashMap<>();

    public void setColor(@NotNull Player player, @NotNull Color color) {
        playerColors.put(player, color);

            Bukkit.broadcastMessage(ConfigKeys.COLOR_BROADCAST
                    .getString()
                    .replace("{player}", player.getName())
                    .replace("{color}", color.getName()));

            LifeUtils.sendTitle(player, ConfigKeys.COLOR_PLAYER_TITLE
                    .getString()
                    .replace("{color}", color.getName()), ConfigKeys.COLOR_PLAYER_SUBTITLE

                    .getString()
                    .replace("{color}", color.getName()));

    }

    public Color getColor(@NotNull Player player) {
        return playerColors.get(player);
    }

    public void removeColor(@NotNull Player player) {
        playerColors.remove(player);
    }
}
