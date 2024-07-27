package coma112.clife.processor;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class MessageProcessor {
    public static @NotNull String process(@Nullable String message) {
        if (message == null) return "";

        Pattern pattern = Pattern.compile("#[a-fA-F\\d]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            StringBuilder builder = new StringBuilder();
            for (char c : replaceSharp.toCharArray()) builder.append("&").append(c);

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&' ,message);
    }

    public static @NotNull List<String> processList(@Nullable List<String> messages) {
        if (messages == null) return new ArrayList<>();

        List<String> processedMessages = new ArrayList<>();

        messages.forEach(message -> processedMessages.add(process(message)));
        return processedMessages;
    }
}
