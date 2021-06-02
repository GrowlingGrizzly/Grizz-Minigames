package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdFreeTokens implements TabExecutor {

    MinigamesUtil minigameUtil = new MinigamesUtil();
    Grizz plugin = Grizz.pluginMain;
    Boolean forceGive = false;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        forceGive = false;

        if (cmd.getName().equalsIgnoreCase("freetokens")) {

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("-forcegive")) {
                    forceGive = true;
                }
            }
            if (minigameUtil.calculateCooldown(player, "FreeTokens", forceGive)) return true;

            double freeAmount = plugin.getMinigame().getDouble("FreeTokens.Amount-Given");

            minigameUtil.addTokens(player, freeAmount);
            player.sendMessage(plugin.aDR(plugin.getMessage().getString("FreeTokens.Given-Tokens")
                    .replaceAll("%freeAmount%", minigameUtil.formatBalance.format(freeAmount))
                    .replaceAll("%tokens%", minigameUtil.formatBalance.format(minigameUtil.getTokens(player)))));

        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<>();
    }
}
