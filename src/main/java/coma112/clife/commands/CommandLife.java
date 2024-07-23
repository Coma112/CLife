package coma112.clife.commands;

import coma112.clife.CLife;
import coma112.clife.config.Config;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeLogger;
import coma112.clife.utils.LifeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;


@Command({"clife", "life"})
public class CommandLife {
    @DefaultFor({"clife", "life"})
    public void defaultCommand(@NotNull CommandSender sender) {
        help(sender);
    }

    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        MessageKeys.HELP
                .getMessages()
                .forEach(sender::sendMessage);
    }

    @Subcommand("reload")
    @CommandPermission("clife.reload")
    public void reload(@NotNull CommandSender sender) {
        CLife.getInstance().getLanguage().reload();
        CLife.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("start")
    @CommandPermission("clife.start")
    public void start(@NotNull Player player) {
        Match match = CLife.getInstance().getMatch(player);
        Config config = CLife.getInstance().getConfiguration();

        if (Bukkit.getOnlinePlayers().size() < ConfigKeys.MINIMUM_PLAYERS.getInt()) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_PLAYERS.getMessage().replace("{minimum}", String.valueOf(ConfigKeys.MINIMUM_PLAYERS.getInt() - Bukkit.getOnlinePlayers().size())));
            return;
        }

        if (match != null && match.isInMatch(player)) {
            player.sendMessage(MessageKeys.ALREADY_IN_MATCH.getMessage());
            return;
        }

        if (config.getYml().get("match-center") == null || config.getYml().get("match-radius") == null) {
            player.sendMessage(MessageKeys.INCORRECT_LOCATION.getMessage());
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
    public void changeTime(@NotNull Player player, @NotNull @Default("me") Player target, @NotNull String prefix, int time) {
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
    public void setColor(@NotNull Player player, @NotNull @Default("me") Player target, @NotNull Color color) {
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

        LifeUtils.updateMatchColor(match, player, target, color);
    }

    @Subcommand("setcenter")
    @CommandPermission("clife.setcenter")
    public void setCenter(@NotNull Player player) {
        if (!ConfigKeys.RTP_ENABLED.getBoolean()) {
            player.sendMessage(MessageKeys.RTP_DISABLED.getMessage());
            return;
        }

        CLife.getInstance().getConfiguration().set("match-center", LifeUtils.convertLocationToString(player.getLocation()));
        player.sendMessage(MessageKeys.SUCCESSFUL_CENTER.getMessage());
    }

    @Subcommand("setradius")
    @CommandPermission("clife.setradius")
    public void setRadius(@NotNull Player player, double radius) {
        if (!ConfigKeys.RTP_ENABLED.getBoolean()) {
            player.sendMessage(MessageKeys.RTP_DISABLED.getMessage());
            return;
        }

        if (radius <= 0) {
            player.sendMessage(MessageKeys.CANT_BE_NULL.getMessage());
            return;
        }

        CLife.getInstance().getConfiguration().set("match-radius", radius);
        player.sendMessage(MessageKeys.SUCCESSFUL_RADIUS.getMessage());
    }
}
