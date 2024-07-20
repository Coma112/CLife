package coma112.clife.commands;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeLogger;
import coma112.clife.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static coma112.clife.enums.Color.getUpperLimit;

@Command({"clife", "life"})
public class CommandLife {
    @Subcommand("start")
    @CommandPermission("clife.start")
    public void start(@NotNull Player player) {
        Match match = CLife.getInstance().getMatch(player);

        if (Bukkit.getOnlinePlayers().size() < ConfigKeys.MINIMUM_PLAYERS.getInt()) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_PLAYERS.getMessage().replace("{minimum}", String.valueOf(ConfigKeys.MINIMUM_PLAYERS.getInt() - Bukkit.getOnlinePlayers().size())));
            return;
        }

        if (match != null && match.isInMatch(player)) {
            player.sendMessage(MessageKeys.ALREADY_IN_MATCH.getMessage());
            return;
        }

        new Match();
    }

    @Subcommand("stop")
    @CommandPermission("clife.stop")
    public void stop(@NotNull Player player) {
        Match match = CLife.getInstance().getMatch(player);

        if (match == null) return;

        if (!match.isInMatch(player)) {
            player.sendMessage(MessageKeys.NOT_IN_MATCH.getMessage());
            return;
        }

        match.endMatch();
    }

    @Subcommand("change")
    @CommandPermission("clife.changetime")
    public void changeTime(@NotNull Player player, @NotNull Player target, @NotNull String prefix, int time) {
        Match match = CLife.getInstance().getMatch(target);

        if (match == null) return;

        if (!match.isInMatch(player)) {
            player.sendMessage(MessageKeys.NOT_IN_MATCH.getMessage());
            return;
        }

        if (!match.isInMatch(target)) {
            player.sendMessage(MessageKeys.TARGET_NOT_IN_MATCH.getMessage());
            return;
        }

        switch (prefix) {
            case "+" -> {
                match.addTime(target, time);
                LifeLogger.info("Added " + time + " seconds to " + target.getName());
            }
            case "-" -> {
                match.removeTime(target, time);
                LifeLogger.info("Removed " + time + " seconds from " + target.getName());
            }
        }
    }

    @Subcommand("setcolor")
    @CommandPermission("clife.setcolor")
    public void setColor(@NotNull Player player, @NotNull Player target, @NotNull Color color) {
        Match match = CLife.getInstance().getMatch(target);

        if (match == null) return;

        if (!match.isInMatch(player)) {
            player.sendMessage(MessageKeys.NOT_IN_MATCH.getMessage());
            return;
        }

        if (!match.isInMatch(target)) {
            player.sendMessage(MessageKeys.TARGET_NOT_IN_MATCH.getMessage());
            return;
        }

        switch (color) {
            case DARK_GREEN -> match.setTime(target, getUpperLimit(Color.DARK_GREEN));
            case LIME -> match.setTime(target, getUpperLimit(Color.LIME));
            case YELLOW -> match.setTime(target, getUpperLimit(Color.YELLOW));
            case ORANGE -> match.setTime(target, getUpperLimit(Color.ORANGE));
            case RED -> match.setTime(target, getUpperLimit(Color.RED));
            case VIOLET -> match.setTime(target, getUpperLimit(Color.VIOLET));
        }
    }

    @Subcommand("setcenter")
    @CommandPermission("clife.setcenter")
    public void setCenter(@NotNull Player player) {
        CLife.getInstance().getConfiguration().set("match-center", PlayerUtils.convertLocationToString(player.getLocation()));
    }

    @Subcommand("setradius")
    @CommandPermission("clife.setradius")
    public void setRadius(@NotNull Player player, double radius) {
        if (radius <= 0) return;
        CLife.getInstance().getConfiguration().set("match-radius", radius);
    }
}
