package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdMinigameMenu implements TabExecutor {

    MinigamesUtil minigameUtil = new MinigamesUtil();
    Grizz plugin = Grizz.pluginMain;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) sender.sendMessage(plugin.playerOnly());
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("minigames")) openInventory(player);
        return true;
    }

    public void openInventory(Player player) {
        player.openInventory(minigameUtil.menuGetInventory());
        minigameUtil.setFullInventoryItem(player, 27, "§r", Material.GRAY_STAINED_GLASS_PANE, null);
        player.getOpenInventory().getTopInventory().setItem(12, minigameUtil.createItem("§b§lSnake", Material.SUNFLOWER, Collections.singletonList("§7§oClick here to play snake!")));
        player.getOpenInventory().getTopInventory().setItem(14, minigameUtil.createItem("§b§lEnergy Factory", Material.REDSTONE_TORCH, Arrays.asList("§7§oClick here to open", "§7§oyour energy factory!")));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<>();
    }
}
