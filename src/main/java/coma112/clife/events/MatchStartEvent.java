package coma112.clife.events;

import coma112.clife.interfaces.PlaceholderProvider;
import coma112.clife.managers.Match;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class MatchStartEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final Match match;

    public MatchStartEvent(@NotNull Match match) {
        this.match = match;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{count}", String.valueOf(match.getPlayers().size()));

        return placeholders;
    }
}
