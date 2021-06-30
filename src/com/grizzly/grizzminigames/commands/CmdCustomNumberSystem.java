package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.minigames.EnergyFactory;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdCustomNumberSystem implements TabExecutor {

    MinigamesUtil minigameUtil = new MinigamesUtil();
    Grizz plugin = Grizz.pluginMain;
    Boolean forceGive = false;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) sender.sendMessage(plugin.playerOnly());


        Player player = (Player) sender;
        forceGive = false;

        if (cmd.getName().equalsIgnoreCase("customnumbersystem")) {

            if (args.length < 3) {
                if (args[0].equalsIgnoreCase("format")) {
                    if (args.length < 2) player.sendMessage(plugin.commandSyntax("/customnumbersystem <add/subtract/multiply/format> <base num/formatting num> <math second num>"));
                } else player.sendMessage(plugin.commandSyntax("/customnumbersystem <add/subtract/multiply/format> <base num/formatting num> <math second num>"));
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) minigameUtil.addNumbers(player, args[1], args[2], true);
            else if (args[0].equalsIgnoreCase("subtract")) minigameUtil.subtractNumbers(player, args[1], args[2], true);
            else if (args[0].equalsIgnoreCase("multiply")) minigameUtil.multiplyNumbers(player, args[1], args[2], true);
            else if (args[0].equalsIgnoreCase("format")) player.sendMessage(minigameUtil.formatStringNum(args[1]));
            else player.sendMessage(plugin.commandSyntax("/customnumbersystem <add/subtract/multiply/format> <base num/formatting num> <math second num>"));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return new ArrayList<>(Arrays.asList("add", "subtract", "multiply", "format"));
        else return new ArrayList<>();
    }
}
