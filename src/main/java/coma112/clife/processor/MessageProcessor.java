package coma112.clife.processor;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class MessageProcessor {
    public static @NotNull String process(@Nullable String message) {
        if (message == null) return "";

        Pattern pattern = Pattern.compile("#[a-fA-F\\d]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());

            String result = hexCode
                    .substring(1)
                    .chars()
                    .mapToObj(c -> "&" + (char) c)
                    .collect(Collectors.joining());

            message = message.replace(hexCode, result);
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static @NotNull List<String> processList(@Nullable List<String> messages) {
        if (messages == null) return new ArrayList<>();

        List<String> processedMessages = new ArrayList<>();

        messages.forEach(message -> processedMessages.add(process(message)));
        return processedMessages;
    }
}
