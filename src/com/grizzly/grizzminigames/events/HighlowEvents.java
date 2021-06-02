package com.grizzly.grizzminigames.events;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.commands.CmdHighLow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HighlowEvents implements Listener {

    Grizz plugin = Grizz.pluginMain;
    static ItemStack[] test;

    @EventHandler
    public void onClickItem(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        //player.sendMessage(String.valueOf(e.getRawSlot()));

        if (player.getOpenInventory().getTitle().equals("§c§lHighlow")) {
            if (e.getRawSlot() < 54) {
                e.setCancelled(true);
            }
        }


    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {

        Player player = (Player) e.getPlayer();

        if (e.getView().getTitle().equals("§c§lHighlow")) {
            player.sendMessage(plugin.pf() + "§aYou have opened the highlow game!");
            test = player.getInventory().getStorageContents();
            player.getInventory().setStorageContents(null);
        }

    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {

        Player player = (Player) e.getPlayer();

        if (e.getView().getTitle().equals("§c§lHighlow")) {
            player.sendMessage(plugin.pf() + "§cYou have closed the highlow game!");
            player.setCanPickupItems(true);
            player.getInventory().setStorageContents(test);
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();
        CmdHighLow highLow = new CmdHighLow();
        UUID uuid = player.getUniqueId();

        if (CmdHighLow.hlIsGameEnabled.get(uuid) != null && CmdHighLow.hlIsGameEnabled.get(uuid)) {
            if (e.getMessage().equalsIgnoreCase("lower")) {
                highLow.playerContinue(player, false);
                e.setCancelled(true);
            } else if (e.getMessage().equalsIgnoreCase("higher")) {
                highLow.playerContinue(player, true);
                e.setCancelled(true);
            } else if (e.getMessage().equalsIgnoreCase("cash out")) {
                highLow.playerStop(player, false);
                e.setCancelled(true);
            }
        }
    }

}
