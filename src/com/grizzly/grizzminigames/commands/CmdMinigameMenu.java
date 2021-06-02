package com.grizzly.grizzminigames.commands;

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
    Boolean forceGive = false;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        forceGive = false;

        if (cmd.getName().equalsIgnoreCase("minigames")) openInventory(player);

        return true;
    }

    public void openInventory(Player player) {
        player.openInventory(minigameUtil.menuGetInventory());
        player.getOpenInventory().getTopInventory().setItem(12, minigameUtil.createItem("§b§lSnake", Material.SUNFLOWER, Collections.singletonList("§7§oClick here to play snake!")));
        player.getOpenInventory().getTopInventory().setItem(14, minigameUtil.createItem("§b§lEnergy Factory", Material.REDSTONE_TORCH, Arrays.asList("§7§oClick here to open", "§7§oyour energy factory!")));
        addSameItemRange(player, 0, 11, "§r", Material.GRAY_STAINED_GLASS_PANE, null);
        player.getOpenInventory().getTopInventory().setItem(13,  minigameUtil.createItem( "§r", Material.GRAY_STAINED_GLASS_PANE, null));
        addSameItemRange(player, 15, 26, "§r", Material.GRAY_STAINED_GLASS_PANE, null);
    }

    @SuppressWarnings("SameParameterValue")
    void addSameItemRange(Player player, int min, int max, String name, Material mat, List<String> lore) {
        Integer[] list = new Integer[]{min};

        for (int i = min; i < max; i++) list = append(list, i);
        list = append(list, max);

        for (Integer slot : list) player.getOpenInventory().getTopInventory().setItem(slot, minigameUtil.createItem(name, mat, lore));
    }

    Integer[] append(Integer[] arr, int addition) {
        List<Integer> list = new ArrayList<>(Arrays.asList(arr));
        list.add(addition);

        return list.toArray(new Integer[0]);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<>();
    }
}
