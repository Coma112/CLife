package coma112.clife.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public final class MatchKillEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player victim;
    private final Player killer;

    public MatchKillEvent(@NotNull Player victim, @NotNull Player killer) {
        this.victim = victim;
        this.killer = killer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
