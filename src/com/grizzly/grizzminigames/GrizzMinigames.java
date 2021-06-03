package com.grizzly.grizzminigames;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.commands.*;
import com.grizzly.grizzminigames.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("ConstantConditions")
public class GrizzMinigames extends JavaPlugin {

    public static GrizzMinigames pluginMinigames;
    Grizz pluginMain = Grizz.pluginMain;


    @Override
    public void onEnable() {

        pluginMinigames = this;

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlackjackEvents(), this);
        pm.registerEvents(new HighlowEvents(), this);
        pm.registerEvents(new AceydouceyEvents(), this);
        pm.registerEvents(new SnakeEvents(), this);
        pm.registerEvents(new MinigameMenuEvents(), this);
        pm.registerEvents(new EnergyFactoryEvents(), this);
        getCommand("minigamehistory").setExecutor(new CmdMinigameHistory());
        getCommand("blackjack").setExecutor(new CmdBlackJack());
        getCommand("highlow").setExecutor(new CmdHighLow());
        getCommand("headtest").setExecutor(new CmdGiveHeads());
        getCommand("tokens").setExecutor(new CmdTokenBalance());
        getCommand("freetokens").setExecutor(new CmdFreeTokens());
        getCommand("aceydoucey").setExecutor(new CmdAceyDoucey());
        getCommand("minigames").setExecutor(new CmdMinigameMenu());
        getCommand("customnumbersystem").setExecutor(new CmdCustomNumberSystem());

        Bukkit.getScheduler().runTask(pluginMinigames, () -> getServer().getOnlinePlayers().forEach(player -> new MinigameMenuEvents().checkInventory(player)));

        Grizz.connectedAddons.add("Grizz-Minigames");
    }

    @Override
    public void onDisable() {

        getServer().getOnlinePlayers().forEach(player -> {
            if (player != null && player.getOpenInventory() != null)
                if (player.getOpenInventory().getTitle().equalsIgnoreCase("§c§lSnake") ||
                        player.getOpenInventory().getTitle().equalsIgnoreCase("§2§lMinigames") ||
                        player.getOpenInventory().getTitle().contains("§a§lEnergy Factory"))
                player.closeInventory();
        });
        pluginMain = null;
    }

    public String node(String permNode) { return "grizzminigames." + permNode; }

}

