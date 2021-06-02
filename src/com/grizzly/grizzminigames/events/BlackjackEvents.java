package com.grizzly.grizzminigames.events;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.commands.CmdBlackJack;
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

public class BlackjackEvents implements Listener {

    Grizz plugin = Grizz.pluginMain;
    static ItemStack[] test;

    @EventHandler
    public void onClickItem(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        //player.sendMessage(String.valueOf(e.getRawSlot()));

        if (player.getOpenInventory().getTitle().equals("§c§lBlackjack")) {
            if (e.getRawSlot() < 54) {
                e.setCancelled(true);
            }
        }


    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {

        Player player = (Player) e.getPlayer();

        if (e.getView().getTitle().equals("§c§lBlackjack")) {
            player.sendMessage(plugin.pf() + "§aYou have opened the blackjack game!");
            test = player.getInventory().getStorageContents();
            player.getInventory().setStorageContents(null);
        }

    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {

        Player player = (Player) e.getPlayer();

        if (e.getView().getTitle().equals("§c§lBlackjack")) {
            player.sendMessage(plugin.pf() + "§cYou have closed the blackjack game!");
            player.setCanPickupItems(true);
            player.getInventory().setStorageContents(test);
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();
        CmdBlackJack deckTest = new CmdBlackJack();
        UUID uuid = player.getUniqueId();

        if (CmdBlackJack.bjIsGameEnabled.get(uuid) != null && CmdBlackJack.bjIsGameEnabled.get(uuid)) {
            if (e.getMessage().equalsIgnoreCase("stand")) {
                deckTest.playerStand(player, false);
                e.setCancelled(true);
            } else if (e.getMessage().equalsIgnoreCase("hit")) {
                deckTest.playerHit(player);
                e.setCancelled(true);
            } else if (e.getMessage().equalsIgnoreCase("double down")) {
                deckTest.playerDoubleDown(player);
                e.setCancelled(true);
            }
        }
    }

}
