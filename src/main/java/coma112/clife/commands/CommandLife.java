package coma112.clife.commands;

import coma112.clife.CLife;
import coma112.clife.enums.keys.MessageKeys;
import org.bukkit.command.CommandSender;
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
}
