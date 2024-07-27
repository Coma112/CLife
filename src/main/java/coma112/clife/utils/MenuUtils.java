package coma112.clife.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Getter
public class MenuUtils {
    private final Player owner;
    @Getter private static final HashMap<Player, MenuUtils> menuMap = new HashMap<>();

    public MenuUtils(@NotNull Player player) {
        this.owner = player;
    }

    public static MenuUtils getMenuUtils(@NotNull Player player) {
        MenuUtils menuUtils;

        if (!(getMenuMap().containsKey(player))) {
            menuUtils = new MenuUtils(player);
            getMenuMap().put(player, menuUtils);

            return menuUtils;
        }

        return getMenuMap().get(player);
    }

}

