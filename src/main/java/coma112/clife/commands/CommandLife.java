package coma112.clife.commands;

import coma112.clife.CLife;
import coma112.clife.config.Config;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
import coma112.clife.queue.Queue;
import coma112.clife.utils.LifeLogger;
import coma112.clife.utils.LifeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

        if (config.getYml().get("lobby") == null) {
            player.sendMessage(MessageKeys.NO_LOBBY.getMessage());
            return;
        }

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

    @Subcommand("setlobby")
    @CommandPermission("clife.setlobby")
    public void setLobby(@NotNull Player player) {
        CLife.getInstance().getConfiguration().set("lobby", LifeUtils.convertLocationToString(player.getLocation()));

        World world = LifeUtils.convertStringToLocation(CLife.getInstance().getConfiguration().getString("lobby")).getWorld();

        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setTime(6000);
        world.setStorm(false);
        world.setThundering(false);
        world.setPVP(false);
        world.setDifficulty(Difficulty.PEACEFUL);

        world.getEntities()
                .stream()
                .filter(entity -> entity.getType() != EntityType.PLAYER)
                .filter(entity -> entity.getType() != EntityType.PAINTING)
                .filter(entity -> entity.getType() != EntityType.ITEM_FRAME)
                .filter(entity -> entity.getType() != EntityType.ARMOR_STAND)
                .forEach(Entity::remove);

        player.sendMessage(MessageKeys.SUCCESSFUL_SETLOBBY.getMessage());
    }

    @Subcommand("joinqueue")
    @CommandPermission("clife.joinqueue")
    public void joinQueue(@NotNull Player player) {
        Queue.addPlayerToQueue(player);

    }

    @Subcommand("quitqueue")
    @CommandPermission("clife.quitqueue")
    public void quitQueue(@NotNull Player player) {
        Queue.removePlayerFromQueue(player);
    }
}
