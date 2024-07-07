package coma112.clife.utils;

import coma112.clife.processor.MessageProcessor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class PlayerUtils {
    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageProcessor.process(message)));
    }

    public static void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle) {
        player.sendTitle(MessageProcessor.process(title), MessageProcessor.process(subTitle));
    }

    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
