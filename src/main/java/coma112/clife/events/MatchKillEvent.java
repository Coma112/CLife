package coma112.clife.events;

import coma112.clife.interfaces.PlaceholderProvider;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class MatchKillEvent extends Event implements PlaceholderProvider {
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

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{killer}", killer.getName());
        placeholders.put("{victim}", victim.getName());

        return placeholders;
    }
}
