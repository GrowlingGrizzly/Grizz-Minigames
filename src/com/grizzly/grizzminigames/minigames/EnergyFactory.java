package com.grizzly.grizzminigames.minigames;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzmain.util.ConfigMaker;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EnergyFactory {

    MinigamesUtil minigame = new MinigamesUtil();

    static ConfigMaker getUserData(Player player) { return new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), Grizz.pluginMain.getDataFolder() + "/userdata/"); }

    public void runFactory(Player player) {
        if (!getUserData(player).contains("Minigames-Addon.Energy-Factory.Energy")) minigame.setEnergy(player, "0");
        player.openInventory(minigame.energyGetInventory());
        setEnergy(player);
    }

    public void setEnergy(Player player) {
        player.getOpenInventory().getTopInventory().setItem(22,
                minigame.createItem("§e⚡ §cManual Energy §e⚡", Material.REDSTONE_TORCH,
                        Arrays.asList("§7§oClick to get energy!", "§bCurrent: §6" + minigame.formatStringNum(getUserData(player).getString("Minigames-Addon.Energy-Factory.Energy")) + " ⚡")));
    }
}
