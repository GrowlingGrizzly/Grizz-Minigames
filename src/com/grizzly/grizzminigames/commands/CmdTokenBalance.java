package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdTokenBalance implements TabExecutor {

    MinigamesUtil minigamesUtil = new MinigamesUtil();


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tokens")) {
                    player.sendMessage("§6Your Tokens: §a" + minigamesUtil.formatBalance.format(minigamesUtil.getTokens(player)));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return null;
        return new ArrayList<>();
    }

}
