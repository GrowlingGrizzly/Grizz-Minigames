package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdTokenBalance implements TabExecutor {

    MinigamesUtil minigamesUtil = new MinigamesUtil();
    Grizz plugin = Grizz.pluginMain;

//minigamesUtil.formatBalance.format(minigamesUtil.getTokens(player)
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) player.sendMessage(plugin.aDR(plugin.getMessage().getString("Token-Balance"))
                    .replaceAll("%tokens%", minigamesUtil.formatBalance.format(minigamesUtil.getTokens(player))));
            else if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    player.sendMessage(plugin.aDR(plugin.getMessage().getString("Token-Balance-Other"))
                            .replaceAll("%tokens%", minigamesUtil.formatBalance.format(minigamesUtil.getTokens(target)))
                            .replaceAll("%player%", target.getName()));
                } else player.sendMessage(plugin.playerOffline());
            } else player.sendMessage(plugin.commandSyntax("/tokens <player>"));
        } return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return null;
        return new ArrayList<>();
    }

}
