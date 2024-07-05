package coma112.clife.managers;

import coma112.clife.enums.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ColoredPlayer(@NotNull Player player, @NotNull Color color, int time) {

    public ColoredPlayer(@NotNull Player player, @NotNull Color color, int time) {
        this.player = player;
        this.color = color;
        this.time = time;
    }

    public String formatTime(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getFormattedTime() {
        return formatTime(this.time);
    }
}
