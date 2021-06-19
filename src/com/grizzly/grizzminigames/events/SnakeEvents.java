package com.grizzly.grizzminigames.events;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.GrizzMinigames;
import com.grizzly.grizzminigames.commands.CmdMinigameMenu;
import com.grizzly.grizzminigames.minigames.Snake;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class SnakeEvents implements Listener {

    Grizz plugin = Grizz.pluginMain;
    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    Snake snake = new Snake();

    @EventHandler
    public void onClickItem(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (player.getOpenInventory().getTitle().equals("§c§lSnake")) {
            if (e.getRawSlot() == 58) snake.setUp(player);
            if (e.getRawSlot() == 66) snake.setLeft(player);
            if (e.getRawSlot() == 68) snake.setRight(player);
            if (e.getRawSlot() == 76) snake.setDown(player);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().equals("§c§lSnake")) {
            player.getInventory().setStorageContents(null);
            Bukkit.getScheduler().runTaskLater(pluginMinigames, () -> addArrows(player), 3);
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        boolean minigameRunning = false;
        if (MinigameMenuEvents.minigameRunning.get(player.getUniqueId()) != null) minigameRunning = MinigameMenuEvents.minigameRunning.get(player.getUniqueId());
        if (e.getView().getTitle().equals("§c§lSnake")) {
            String endType;
            switch (snake.getEndType(player)) {
                case "self": endType = "§cYou hit yourself!"; break;
                case "wall": endType = "§cYou hit the wall!"; break;
                default: endType = "§cYou quit the game!"; break;
            } player.sendMessage(endType + " Score: §6" + snake.getPlayerScore(player));
            snake.setGameOver(player);
            new MinigameMenuEvents().setGameEnded(player);
            Bukkit.getScheduler().runTaskLater(pluginMinigames, () -> new CmdMinigameMenu().openInventory(player), 1);
        } if (player.getOpenInventory().getTitle().equals("§a§lSnake Speed Selector") && !minigameRunning) {
            new MinigameMenuEvents().setGiveItems(player, true);
            Bukkit.getScheduler().runTaskLater(pluginMinigames, () -> new CmdMinigameMenu().openInventory(player), 1);
        }
    }

    void addArrows(Player player) {
        Integer[] slots = new Integer[]{13, 21, 22, 23, 31};
        String[] type = new String[]{"Up", "Left", "Blank", "Right", "Down"};
        for (int i = 0; i < 5; i++) player.getInventory().setItem(slots[i], plugin.getHeads().getItemStack("Heads." + type[i]));
    }
}
