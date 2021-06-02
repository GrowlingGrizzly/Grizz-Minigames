package com.grizzly.grizzminigames.Minigames;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.GrizzMinigames;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Snake {

    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    Grizz plugin = Grizz.pluginMain;
    MinigamesUtil minigame = new MinigamesUtil();
    public static HashMap<UUID, Boolean> gameOver = new HashMap<>();
    public static HashMap<UUID, Integer> playerScore = new HashMap<>();
    public static HashMap<UUID, Integer> headX = new HashMap<>();
    public static HashMap<UUID, Integer> headY = new HashMap<>();
    public static HashMap<UUID, Integer> foodX = new HashMap<>();
    public static HashMap<UUID, Integer> foodY = new HashMap<>();
    public static HashMap<UUID, Boolean> up = new HashMap<>();
    public static HashMap<UUID, Boolean> down = new HashMap<>();
    public static HashMap<UUID, Boolean> left = new HashMap<>();
    public static HashMap<UUID, Boolean> right = new HashMap<>();
    public static HashMap<UUID, Integer[]> snakeHead = new HashMap<>();
    public static HashMap<UUID, ArrayList<Integer[]>> snakeList = new HashMap<>();
    public static HashMap<UUID, String> endType = new HashMap<>();

    public void runSnake(Player player) {

        UUID uuid = player.getUniqueId();
        up.put(player.getUniqueId(), false);
        down.put(player.getUniqueId(), false);
        left.put(player.getUniqueId(), false);
        right.put(player.getUniqueId(), false);
        gameOver.put(uuid, false);
        playerScore.put(uuid, 0);
        headX.put(uuid, 4);
        headY.put(uuid, 2);
        snakeList.put(uuid, new ArrayList<>());
        snakeHead.put(uuid, new Integer[]{});
        endType.put(uuid, "quit");
        player.openInventory(minigame.snakeGetInventory());

            addFood(player, true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if (gameOver.get(uuid)) cancel();
                        else {

                            int y1Change = 0;
                            int x1Change = 0;

                            if (up.get(uuid)) y1Change = 1;
                            if (down.get(uuid)) y1Change = -1;
                            if (left.get(uuid)) x1Change = -1;
                            if (right.get(uuid)) x1Change = 1;

                            headX.put(uuid, headX.get(uuid) + x1Change);
                            headY.put(uuid, headY.get(uuid) + y1Change);

                            if (0 > headX.get(uuid) || headX.get(uuid) > 8 || 0 > headY.get(uuid) || headY.get(uuid) > 5) {
                                endType.put(uuid, "wall");
                                gameEnd(player);
                                cancel();
                            }
                            setSnake(player);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        if (!gameOver.get(uuid)) {
                            player.sendMessage("§cAn error occured, so the game was force shut.");
                            gameEnd(player);
                        }
                        player.closeInventory();
                        cancel();
                    }
                }
            }.runTaskTimer(pluginMinigames, 0, plugin.getMinigame().getInt("Snake.Snake-Speed"));
    }

    public void gameEnd(Player player) {
        gameOver.put(player.getUniqueId(), true);
        player.getInventory().setStorageContents(null);
        player.closeInventory();
    }

    void addFood(Player player, boolean firstStart) {
        if (firstStart) do {
            if (!gameOver.get(player.getUniqueId())) {
                foodX.put(player.getUniqueId(), new Random().nextInt(8));
                foodY.put(player.getUniqueId(), new Random().nextInt(5));
            }
        } while (foodX.get(player.getUniqueId()) == 4 && foodY.get(player.getUniqueId()) == 2);
        else {
            if (!gameOver.get(player.getUniqueId())) {
                foodX.put(player.getUniqueId(), new Random().nextInt(8));
                foodY.put(player.getUniqueId(), new Random().nextInt(5));
            }
        }
    }

    void setSnake(Player player) {
        if (!gameOver.get(player.getUniqueId())) {
            UUID uuid = player.getUniqueId();
            player.getOpenInventory().getTopInventory().clear();
            if (!(headX.get(uuid) + setY(player, true) < 0)) player.getOpenInventory().getTopInventory().setItem((headX.get(uuid) + setY(player, true)), minigame.createItem("§2Snake Head", Material.GREEN_WOOL, Collections.singletonList("§7§oPart of your snake!")));

            if (headX.get(uuid).equals(foodX.get(uuid)) && headY.get(uuid).equals(foodY.get(uuid))) {
                addFood(player, false);
                playerScore.put(uuid, playerScore.get(uuid) + 1);
                player.sendMessage("§eYummy! §a+1");
            }

            snakeHead.put(uuid, new Integer[]{headX.get(uuid), headY.get(uuid)});

            ArrayList<Integer[]> list = new ArrayList<>(snakeList.get(uuid));
            list.add(snakeHead.get(uuid));

            snakeList.put(uuid, list);

            if (snakeList.get(uuid).toArray().length > playerScore.get(uuid)+1) snakeList.get(uuid).remove(0);

            for (int i = 0; i < snakeList.get(uuid).toArray().length-1; i++) {
                if (Arrays.toString(snakeList.get(uuid).get(i)).equals(Arrays.toString(snakeHead.get(uuid)))) {
                    endType.put(uuid, "self");
                    setGameOver(player);
                    gameEnd(player);
                }
            }
            for (int i = 0; i < snakeList.get(uuid).toArray().length-1; i++) player.getOpenInventory().getTopInventory().setItem((snakeList.get(uuid).get(i)[0] + setSnakeArrayY(snakeList.get(uuid).get(i)[1])), minigame.createItem("§aSnake Body", Material.LIME_WOOL, Collections.singletonList("§7§oPart of your snake!")));

            player.getOpenInventory().getTopInventory().setItem((headX.get(uuid) + setY(player, true)), minigame.createItem("§2Snake Head", Material.GREEN_WOOL, Collections.singletonList("§7§oPart of your snake!")));


            while (player.getOpenInventory().getTopInventory().getItem((foodX.get(uuid) + setY(player, false))) != null) {
                addFood(player, false);
            }
            if (!(foodX.get(uuid) + setY(player, false) < 0)) player.getOpenInventory().getTopInventory().setItem((foodX.get(uuid) + setY(player, false)), minigame.createItem("§eSnake Food", Material.SUNFLOWER, Collections.singletonList("§7§oEat this to grow your snake!")));
        }
    }

    int setY(Player player, boolean isSnake) {
        int num = headY.get(player.getUniqueId());
        if (!isSnake) num = foodY.get(player.getUniqueId());
        int formattedNum = 45;
        for (int i = 0; i < num; i++) formattedNum -= 9;
        return formattedNum;
    }

    int setSnakeArrayY(int num) {
        int formattedNum = 45;
        for (int i = 0; i < num; i++) formattedNum -= 9;
        return formattedNum;
    }

    public void setGameOver(Player player) { gameOver.put(player.getUniqueId(), true); }

    public int getPlayerScore(Player player) { return playerScore.get(player.getUniqueId()); }

    public String getEndType(Player player) { return endType.get(player.getUniqueId()); }

    void directionBase(Player player, boolean u, boolean d, boolean l, boolean r) {
        up.put(player.getUniqueId(), u);
        down.put(player.getUniqueId(), d);
        left.put(player.getUniqueId(), l);
        right.put(player.getUniqueId(), r);
    }

    public void setUp(Player player) { directionBase(player, true, false, false, false); }
    public void setDown(Player player) { directionBase(player, false, true, false, false); }
    public void setLeft(Player player) { directionBase(player, false, false, true, false); }
    public void setRight(Player player) { directionBase(player, false, false, false, true); }

}
