package com.grizzly.grizzminigames.events;

import com.grizzly.grizzminigames.GrizzMinigames;
import com.grizzly.grizzminigames.commands.CmdMinigameMenu;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public class EnergyFactoryEvents implements Listener {
    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    MinigamesUtil minigame = new MinigamesUtil();

    static HashMap<UUID, Double> oldTick = new HashMap<>();

    @EventHandler
    public void onClickItem(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (player.getOpenInventory().getTitle().contains("§a§lEnergy Factory")) {
            if (e.getRawSlot() == 22) {
                LocalDateTime time = LocalDateTime.now();

                double currentTick = time.getNano()/1000000.0/50.0;

                if (!String.valueOf(e.getClick()).equals("DOUBLE_CLICK") && !String.valueOf(e.getClick()).equals("NUMBER_KEY"))
                    if (!oldTick.containsKey(player.getUniqueId()) || currentTick - oldTick.get(player.getUniqueId()) > 1.53 || currentTick - oldTick.get(player.getUniqueId()) < -1.53) {
                        oldTick.put(player.getUniqueId(), currentTick);
                        minigame.addEnergy(player, 1);
                    }
            } e.setCancelled(true);
        }
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().contains("§a§lEnergy Factory")) player.getInventory().setStorageContents(null);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().contains("§a§lEnergy Factory")) {
            new MinigameMenuEvents().setGameEnded(player);
            Bukkit.getScheduler().runTask(pluginMinigames, () -> new CmdMinigameMenu().openInventory(player));
        }
    }
}
