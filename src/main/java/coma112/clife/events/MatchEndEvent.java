package coma112.clife.events;

import coma112.clife.managers.Match;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public final class MatchEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Match match;

    public MatchEndEvent(@NotNull Match match) {
        this.match = match;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
