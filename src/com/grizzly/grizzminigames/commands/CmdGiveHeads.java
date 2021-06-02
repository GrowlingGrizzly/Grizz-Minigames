package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdGiveHeads implements CommandExecutor {

    Grizz plugin = Grizz.pluginMain;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("headtest")) {
            for (int i = 2; i < 11; i++) player.getInventory().addItem(plugin.getHeads().getItemStack("BlackJackHeads." + i));
            for (String head : new String[]{"A1", "A11", "J", "Q", "K", "Blank", "Up", "Down", "Left", "Right"})
                player.getInventory().addItem(plugin.getHeads().getItemStack("Heads." + head));
            player.sendMessage("Head given!");
        }
        return true;
    }

}
