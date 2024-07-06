package coma112.clife.commands;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.enums.keys.MessageKeys;
import coma112.clife.managers.Match;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"clife", "life"})
public class CommandLife {
    @Subcommand("reload")
    @CommandPermission("clife.reload")
    public void reload(@NotNull CommandSender sender) {
        CLife.getInstance().getLanguage().reload();
        CLife.getInstance().getConfiguration().reload();
        //CLife.getDatabaseManager().reconnect();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("start")
    @CommandPermission("clife.start")
    public void start(@NotNull Player player) {
        Match match = CLife.getInstance().getMatch(player);

        if (Bukkit.getOnlinePlayers().size() < ConfigKeys.MINIMUM_PLAYERS.getInt()) {
            player.sendMessage("Not enough players to start match!");
            return;
        }

        if (match != null && match.isInMatch(player)) {
            player.sendMessage("Match is already in progress!");
            return;
        }

        new Match();
    }
}
