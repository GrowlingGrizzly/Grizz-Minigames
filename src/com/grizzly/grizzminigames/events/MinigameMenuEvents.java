package com.grizzly.grizzminigames.events;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzmain.util.ConfigMaker;
import com.grizzly.grizzminigames.GrizzMinigames;
import com.grizzly.grizzminigames.minigames.EnergyFactory;
import com.grizzly.grizzminigames.minigames.Snake;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"unused", "ConstantConditions"})
public class MinigameMenuEvents implements Listener {

    Grizz plugin = Grizz.pluginMain;
    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    Snake snake = new Snake();
    EnergyFactory factory = new EnergyFactory();
    static HashMap<UUID, Boolean> minigameRunning = new HashMap<>();

    public void setGameRunning(Player player) { minigameRunning.put(player.getUniqueId(), true); }
    public void setGameEnded(Player player) { minigameRunning.remove(player.getUniqueId()); }

    @EventHandler
    public void onClickItem(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (player.getOpenInventory().getTitle().equals("§2§lMinigames")) {
            if (e.getRawSlot() == 12) {
                setGameRunning(player);
                player.closeInventory();
                snake.runSnake(player);
            }
            if (e.getRawSlot() == 14) {
                setGameRunning(player);
                player.closeInventory();
                factory.runFactory(player);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {

        Player player = (Player) e.getPlayer();
        ConfigMaker inventoryBackup = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/inventory-backup/");

        if (e.getView().getTitle().equals("§2§lMinigames")) {
            if (!inventoryBackup.contains("Inventory")) saveInventory(player);
            player.getInventory().setStorageContents(null);
        }

    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        ConfigMaker inventoryBackup = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/inventory-backup/");
        if (e.getView().getTitle().equals("§2§lMinigames") && inventoryBackup.contains("Inventory"))
            Bukkit.getScheduler().runTaskLater(pluginMinigames, () -> {
                if (minigameRunning.get(player.getUniqueId()) == null || !minigameRunning.get(player.getUniqueId())) loadInventory(player); }, 2 );
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ConfigMaker inventoryBackup = new ConfigMaker(Grizz.pluginMain, String.valueOf(e.getPlayer().getUniqueId()), plugin.getDataFolder() + "/minigames/inventory-backup/");
        if (inventoryBackup.contains("Inventory")) {
            loadInventory(e.getPlayer());
        }
    }

    void saveInventory(Player player) {
        ConfigMaker inventoryBackup = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/inventory-backup/");
        ItemStack[] items = player.getInventory().getStorageContents();
        inventoryBackup.set("Inventory", items);
        inventoryBackup.save();
    }

    public void loadInventory(Player player) {
        ConfigMaker inventoryBackup = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/inventory-backup/");
        player.getInventory().setStorageContents(null);
        @SuppressWarnings("unchecked")
        List<ItemStack> itemList = (List<ItemStack>) inventoryBackup.get("Inventory");
        ItemStack[] items = itemList.toArray(new ItemStack[0]);
        player.getInventory().setStorageContents(items);
        inventoryBackup.set("Inventory", null);
        inventoryBackup.save();
        player.setCanPickupItems(true);
    }

    public void checkInventory(Player player) {
        ConfigMaker inventoryBackup = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/inventory-backup/");
        if (inventoryBackup.contains("Inventory")) loadInventory(player);
    }
}
