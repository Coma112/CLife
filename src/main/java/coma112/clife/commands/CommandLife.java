package coma112.clife.commands;

import coma112.clife.CLife;
import coma112.clife.enums.Color;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
import coma112.clife.queue.Queue;
import coma112.clife.utils.LifeUtils;
import coma112.clife.utils.MatchUtils;
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

import java.util.ArrayList;


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

    @Subcommand("stop")
    @CommandPermission("clife.stop")
    public void stop(@NotNull Player player, @NotNull @Default("none") String id) {
        Match match;

        if (id.equals("none")) match = Match.getMatch(player);
        else match = Match.getMatchById(id);

        if (match == null) {
            player.sendMessage(MessageKeys.NO_MATCH_FOUND.getMessage());
            return;
        }

        match.endMatch();
    }

    @Subcommand("stopall")
    @CommandPermission("clife.stopall")
    public void stopAll(@NotNull Player player) {
        new ArrayList<>(Match.getActiveMatches()).forEach(Match::endMatch);
        player.sendMessage(MessageKeys.SUCCESSFUL_STOPALL.getMessage());
    }

    @Subcommand("change")
    @CommandPermission("clife.changetime")
    public void changeTime(@NotNull Player player, @NotNull @Default("me") Player target, @NotNull String prefix, int time) {
        Match match = Match.getMatch(target);

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
                player.sendMessage(MessageKeys.SUCCESSFUL_ADD_PLAYER
                        .getMessage()
                        .replace("{target}", target.getName())
                        .replace("{time}", LifeUtils.formatTime(time)));

                target.sendMessage(MessageKeys.SUCCESSFUL_ADD_TARGET
                        .getMessage()
                        .replace("{player}", player.getName())
                        .replace("{time}", LifeUtils.formatTime(time)));
            }
            case "-" -> {
                match.recordAttack(player, target, time);
                target.sendMessage(MessageKeys.SUCCESSFUL_REMOVE_TARGET
                        .getMessage()
                        .replace("{player}", player.getName())
                        .replace("{time}", LifeUtils.formatTime(time)));

                player.sendMessage(MessageKeys.SUCCESSFUL_REMOVE_PLAYER
                        .getMessage()
                        .replace("{target}", target.getName())
                        .replace("{time}", LifeUtils.formatTime(time)));
            }
        }
    }

    @Subcommand("setcolor")
    @CommandPermission("clife.setcolor")
    public void setColor(@NotNull Player player, @NotNull @Default("me") Player target, @NotNull Color color) {
        Match match = Match.getMatch(target);

        if (match == null) return;

        if (!match.isInMatch(player)) {
            player.sendMessage(MessageKeys.NOT_IN_MATCH.getMessage());
            return;
        }

        if (!match.isInMatch(target)) {
            player.sendMessage(MessageKeys.TARGET_NOT_IN_MATCH.getMessage());
            return;
        }

        MatchUtils.updateMatchColor(match, player, target, color);
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
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
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
