package com.grizzly.grizzminigames.util;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzmain.util.ConfigMaker;
import com.grizzly.grizzminigames.GrizzMinigames;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

public class MinigamesUtil implements InventoryHolder {

    Grizz plugin = Grizz.pluginMain;
    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    GameMessagesUtil msgs = new GameMessagesUtil();
    public DecimalFormat formatBalance = new DecimalFormat("#,##0");


    public void takeTokens(Player player, double amount){
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        getUserData.set("Minigames-Addon.Tokens", getUserData.getDouble("Minigames-Addon.Tokens") - amount); getUserData.save();
    }

    public void addTokens(Player player, double amount){
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        getUserData.set("Minigames-Addon.Tokens", getUserData.getDouble("Minigames-Addon.Tokens") + amount); getUserData.save();
    }

    public Double getTokens(Player player) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        return getUserData.getDouble("Minigames-Addon.Tokens");
    }

    public Boolean getFirstPlayBlackJack(Player player) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        return getUserData.getBoolean("Minigames-Addon.FirstTimePlayingBlackJack");
    }

    public void setNotFirstTimeBlackJack(Player player) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        getUserData.set("Minigames-Addon.FirstTimePlayingBlackJack", false); getUserData.save();
    }

    public Boolean getFirstPlayHighLow(Player player) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        return getUserData.getBoolean("Minigames-Addon.FirstTimePlayingHighLow");
    }

    public void setNotFirstTimeHighLow(Player player) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        getUserData.set("Minigames-Addon.FirstTimePlayingHighLow", false); getUserData.save();
    }

    public double getLastTimeCmdUsed(Player player, String game) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        return getUserData.getDouble("Minigames-Addon.LastTimeUsed." + game);
    }

    public void setLastTimeCmdUsed(Player player, String game, double time) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        getUserData.set("Minigames-Addon.LastTimeUsed." + game, time); getUserData.save();
    }

    public String formatCardsLog(String cards) {
        return cards.replaceAll("\\.", "").replaceAll("\\[", "").replaceAll("]", "");
    }

    public Double formatDateToInt() {
        double i = 0.0;
        LocalDateTime time = LocalDateTime.now();
        i += ((double) time.getYear() * 60.0 * 60.0 * 24.0 * 365.0);
        i += ((double) time.getDayOfYear() * 60.0 * 60.0 * 24.0);
        i += ((double) time.getDayOfMonth() * 60.0 * 60.0 * 24.0 * 30.0);
        i += ((double) time.getDayOfWeek().getValue() * 60.0 * 60.0 * 24.0 * 7.0);
        i += ((double) time.getHour() * 60.0 * 60.0);
        i += ((double) time.getMinute() * 60.0);
        i += time.getSecond();
        return i;
    }

    public ArrayList<String> createMixedDeck() {
        List<String> deckCreation = Arrays.asList("A.♠", "2.♠", "3.♠", "4.♠", "5.♠", "6.♠", "7.♠", "8.♠", "9.♠", "10.♠",
                "J.♠", "Q.♠", "K.♠", "A.♣", "2.♣", "3.♣", "4.♣", "5.♣", "6.♣", "7.♣", "8.♣", "9.♣", "10.♣", "J.♣", "Q.♣",
                "K.♣", "A.♥", "2.♥", "3.♥", "4.♥", "5.♥", "6.♥", "7.♥", "8.♥", "9.♥", "10.♥", "J.♥", "Q.♥", "K.♥", "A.♦",
                "2.♦", "3.♦", "4.♦", "5.♦", "6.♦", "7.♦", "8.♦", "9.♦", "10.♦", "J.♦", "Q.♦", "K.♦");
        ArrayList<String> deckList = new ArrayList<>(deckCreation);
        Collections.shuffle(deckList);

        return deckList;
    }

    public String formatDeckBase(String deck, String format) {
        String suit = null;
        for (int i = 0; i < 4; i++) {

            switch (i) {
                case 0: suit = "♠"; break;
                case 1: suit = "♣"; break;
                case 2: suit = "♥"; break;
                case 3: suit = "♦"; break;
            } for (int i2 = 2; i2 < 11; i2++) deck = deck.replaceAll(i2 + "\\." + suit, formatCard(format, String.valueOf(i2), suit));
            for (String card : new String[]{"A", "J", "Q", "K"}) deck = deck.replaceAll(card + "\\." + suit, formatCard(format, card, suit));
        } return deck;
    }

    String formatCard(String format, String num, String suit) { return format.replaceAll("N", num).replaceAll("S", suit); }

    public Boolean calculateCooldown(Player player, String game, Boolean forceStart) {

        int coolDownAmount = plugin.getMinigame().getInt(game + ".Cooldown");

        if (getLastTimeCmdUsed(player, game) != 0) {
            double date = formatDateToInt();
            if (date - getLastTimeCmdUsed(player, game) > coolDownAmount) setLastTimeCmdUsed(player, game, date);
            else if (forceStart) setLastTimeCmdUsed(player, game, date); else {
                double timeRemaining = getLastTimeCmdUsed(player, game) - date + coolDownAmount;
                int hrAmount = (int) (timeRemaining / 3600);
                int minAmount = (int) ((timeRemaining - (hrAmount * 3600)) / 60);
                int secAmount = (int) (timeRemaining - (hrAmount * 3600) - (minAmount * 60));
                player.sendMessage(plugin.aDR(plugin.getMessage().getString("Game-Cooldown")
                        .replaceAll("%hr%", String.valueOf(hrAmount))
                        .replaceAll("%min%", String.valueOf(minAmount))
                        .replaceAll("%sec%", String.valueOf(secAmount))));
                return true;
            }

        }

        else if (getLastTimeCmdUsed(player, game) == 0) {
            setLastTimeCmdUsed(player, game, formatDateToInt());
        } return false;
    }

    public Boolean getBet(Player player, HashMap<UUID, Double> playerBet, String[] args, Boolean ignoreBet) {

        UUID uuid = player.getUniqueId();
        if (args.length == 0)  {
            player.sendMessage(msgs.noBet());
            return true;
        }

        try {
            playerBet.remove(uuid);
            args[0] = args[0].toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000").replaceAll("t", "000000000000");
            playerBet.put(uuid, Double.parseDouble(args[0]));
        } catch (Exception e) {
            player.sendMessage(msgs.invalidBet());
            return true;
        } if (playerBet.get(uuid) < 1 && !ignoreBet) {
            player.sendMessage(msgs.minBet());
            return true;
        } if (playerBet.get(uuid) > getTokens(player) && !ignoreBet) {
            player.sendMessage(msgs.tooHighBet(player));
            return true;
        } return false;
    }

    public int getCardValueDifferentFaceValues(String card) {
        int cardValue;
        switch (card) {
            case "A": cardValue = 1; break;
            case "2": cardValue = 2; break;
            case "3": cardValue = 3; break;
            case "4": cardValue = 4; break;
            case "5": cardValue = 5; break;
            case "6": cardValue = 6; break;
            case "7": cardValue = 7; break;
            case "8": cardValue = 8; break;
            case "9": cardValue = 9; break;
            case "10": cardValue = 10; break;
            case "J": cardValue = 11; break;
            case "Q": cardValue = 12; break;
            case "K": cardValue = 13; break;
            default: cardValue = 0; break;
        } return cardValue;
    }

    public boolean logGames() { return plugin.getMinigame().getBoolean("Minigame-Log.Log-Games"); }

    public boolean addFlag(Player player, String flag, String permNode, String[] args, int flagAmount) {

        String overwriteFlag = "-" + flag;
        for (int i = 1; i < flagAmount+1; i++) {
            if (args.length > i) {
                if (overwriteFlag.equals(args[i].toLowerCase())) {
                    if (player.hasPermission(permNode)) return true;
                }
            }
        } return false;
    }

    public String getHelpMessage(Boolean firstTime, String game) {

        List<String> helpMsg = plugin.getMessage().getStringList(game + ".Help");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plugin.getMessage().getStringList(game + ".Help").toArray().length; i++) {
            sb.append((helpMsg.get(i)));
            sb.append('\n');
        } if (firstTime) sb.append("\n&f ");

        return plugin.aDR(sb.toString());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ArrayList<String> formatMultipleDeckNumOnly(ArrayList<String> deck) {
        String suit = null;
        for (int i = 0; i < 4; i++) {

            switch (i) {
                case 0: suit = "♠"; break;
                case 1: suit = "♣"; break;
                case 2: suit = "♥"; break;
                case 3: suit = "♦"; break;
            } for (int i2 = 2; i2 < 11; i2++) Collections.replaceAll(deck, i2 + "." + suit, String.valueOf(i2));
            for (String card : new String[]{"A", "J", "Q", "K"}) Collections.replaceAll(deck, card + "." + suit, card);
        } return deck;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ArrayList<String> formatMultipleDeckNumSuit(ArrayList<String> deck) {
        String suit = null;
        for (int i = 0; i < 4; i++) {

            switch (i) {
                case 0: suit = "♠"; break;
                case 1: suit = "♣"; break;
                case 2: suit = "♥"; break;
                case 3: suit = "♦"; break;
            } for (int i2 = 2; i2 < 11; i2++) Collections.replaceAll(deck, i2 + "." + suit, i2 + suit);
            for (String card : new String[]{"A", "J", "Q", "K"}) Collections.replaceAll(deck, card + "." + suit, card + suit);
        } return deck;
    }

    public Integer checkNextLogSpace(Player player) {
        ConfigMaker playerDb = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/log/");
        int num = 0;
        while (true) {
            try {
                @SuppressWarnings("unused")
                String dataCheck = playerDb.get(player.getName() + "." + num).toString();
                num++;
            } catch (NullPointerException e) { break; }
        } return num;
    }

    public String formatStringNum(String energy) {
        StringBuilder formattedNum = new StringBuilder();

        char[] energyAmount = energy.toCharArray();
        int addComma = 0;

        if (energyAmount.length > 3) {
            for (int i = energyAmount.length - 1; i > -1; i--) {

                if (addComma == 3) {
                    formattedNum.insert(0, ",");
                    addComma = 0;
                }

                formattedNum.insert(0, energyAmount[i]);
                addComma++;
            }
        } else formattedNum.insert(0, energy);

        return formattedNum.toString();
    }

    public String addNumbers(Player player, String baseAmount, String addAmount, boolean debugMode) {
        try {
            char[] storedNumber = baseAmount.toCharArray();
            char[] newAmount = addAmount.toCharArray();

            StringBuilder finishedAmount = new StringBuilder();

            while (storedNumber.length < newAmount.length)
                storedNumber = ("0" + String.valueOf(storedNumber)).toCharArray();
            while (newAmount.length < storedNumber.length) newAmount = ("0" + String.valueOf(newAmount)).toCharArray();

            boolean plusOne = false;

            if (debugMode) player.sendMessage(String.valueOf(storedNumber) + "\n" + String.valueOf(newAmount));

            for (int i = newAmount.length - 1; i > -1; i--) {
                int base = Integer.parseInt(String.valueOf(storedNumber[i]));
                int add = Integer.parseInt(String.valueOf(newAmount[i]));
                int newNum;

                if (plusOne) add++;

                if (debugMode) {
                    player.sendMessage("§3Previous > 10: §a" + plusOne);
                    player.sendMessage("§3Current Formula: §a" + base + " + " + add + " = " + (base + add));
                }
                if (base + add > 9) {
                    plusOne = true;
                    newNum = base + add - 10;
                } else {
                    plusOne = false;
                    newNum = base + add;
                }
                finishedAmount.insert(0, newNum);
            }
            if (plusOne) finishedAmount.insert(0, "1");
            if (debugMode) player.sendMessage("\n\n§3Finished Number: §a" + formatStringNum(finishedAmount.toString()));
            return finishedAmount.toString();
        } catch (Exception e) {
            player.sendMessage("§cPlease enter valid numbers.");
            return null;
        }
    }

    public String subtractNumbers(Player player, String baseAmount, String removeAmount, boolean debugMode) {
        try {
            char[] storedNumber = baseAmount.toCharArray();
            char[] newAmount = removeAmount.toCharArray();

            while (storedNumber.length < newAmount.length)
                storedNumber = ("0" + String.valueOf(storedNumber)).toCharArray();
            while (newAmount.length < storedNumber.length) newAmount = ("0" + String.valueOf(newAmount)).toCharArray();

            if (debugMode)
                player.sendMessage("§3Base: §a" + String.valueOf(storedNumber) + "\n§3Subtract: §a" + String.valueOf(newAmount));

            StringBuilder finishedAmount = subtractLoop(player, debugMode, newAmount, storedNumber);

            if (finishedAmount == null) return null;

            if (String.valueOf(finishedAmount.toString().toCharArray()[0]).equalsIgnoreCase("Y")) {
                if (debugMode) player.sendMessage("§3Swapping final number to negative...");
                finishedAmount = subtractLoop(player, debugMode, storedNumber, newAmount);
                finishedAmount.deleteCharAt(0);
                finishedAmount.insert(0, "-");
                while (String.valueOf(finishedAmount.toString().toCharArray()[1]).equals("0"))
                    finishedAmount.deleteCharAt(1);
            } else {
                while (String.valueOf(finishedAmount.toString().toCharArray()[1]).equals("0"))
                    finishedAmount.deleteCharAt(1);
                finishedAmount.deleteCharAt(0);
            }

            if (debugMode) player.sendMessage("\n\n§3Finished Number: §a" + formatStringNum(finishedAmount.toString()));
            return finishedAmount.toString();
        } catch (Exception e) {
            player.sendMessage("§cPlease enter valid numbers.");
            return null;
        }
    }

    public String multiplyNumbers(Player player, String baseAmount, String multiplyAmount, boolean debugMode) {
        try {
            char[] storedNumber = baseAmount.toCharArray();
            char[] newAmount = multiplyAmount.toCharArray();
            while (storedNumber.length < newAmount.length) storedNumber = ("0" + String.valueOf(storedNumber)).toCharArray();
            while (newAmount.length < storedNumber.length) newAmount = ("0" + String.valueOf(newAmount)).toCharArray();
            if (debugMode) player.sendMessage("§3Base: §a" + String.valueOf(storedNumber) + "\n§3Multiply: §a" + String.valueOf(newAmount));
            StringBuilder finishedAmount = new StringBuilder();

            int extraAmount = 0;
            int addExtraToEnd = 0;
            int currentMultiDigit = 0;
            HashMap<Integer, String> loopNum = new HashMap<>();

            for (int i = newAmount.length - 1; i > -1; i--) {
                int multi = Integer.parseInt(String.valueOf(storedNumber[i]));

                boolean addExtra = false;
                String finalNum = "0";
                int currentBaseDigit = 0;
                player.sendMessage("§3Current Multiplier: §a" + multi);

                for (int i2 = newAmount.length - 1; i2 > -1; i2--) {

                    int base = Integer.parseInt(String.valueOf(newAmount[i2]));
                    player.sendMessage("§3Current Base: §a" + base);

                    int currentNum = base * multi + extraAmount;

                    if (currentNum > 9) {
                        extraAmount = Integer.parseInt(String.valueOf(String.valueOf(base * multi + extraAmount).toCharArray()[0]));
                        if (debugMode) player.sendMessage("§3Extra Amount: §a" + extraAmount);
                        finalNum = addNumbers(player, finalNum, addZeros(String.valueOf(currentNum - (extraAmount * 10)), currentBaseDigit + currentMultiDigit), false);
                    } else {
                        extraAmount = 0;
                        finalNum = addNumbers(player, finalNum, addZeros(String.valueOf(currentNum), currentBaseDigit + currentMultiDigit), false);
                    } if (debugMode) player.sendMessage("§3Current Final Num for Digit: §a" + finalNum);
                    currentBaseDigit++;
                } finalNum = addNumbers(player, finalNum, addZeros(String.valueOf(extraAmount), finalNum.length()), false);
                if (debugMode) player.sendMessage("§3Total digit multiplier: §a" + finalNum);
                loopNum.put(currentMultiDigit, String.valueOf(finalNum));
                extraAmount = 0;
                currentMultiDigit++;
            } String finalNumberNum = "0";

            for (int i = 0; i < currentMultiDigit; i++) finalNumberNum = addNumbers(player, finalNumberNum, loopNum.get(i), false);

            player.sendMessage("\n\n§3Final Number Num: §a" + finalNumberNum);

            finishedAmount.append(finalNumberNum);
            while (String.valueOf(finishedAmount.toString().toCharArray()[0]).equals("0")) finishedAmount.deleteCharAt(0);
            if (debugMode) player.sendMessage("\n\n§3Finished Number: §a" + formatStringNum(finishedAmount.toString()));

            return finishedAmount.toString();
        } catch (Exception e) {
            player.sendMessage("§cPlease enter valid numbers.");
            return null;
        }
    }

    String addZeros(String msg, int amount) {
        StringBuilder finalNum = new StringBuilder(msg);
        for (int i = 0; i < amount; i++) finalNum.append("0");
        return finalNum.toString();
    }

    public void setEnergy(Player player, String amount) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        getUserData.set("Minigames-Addon.Energy-Factory.Energy", amount);
        getUserData.save();
    }

    public void addEnergy(Player player, String addAmount) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        setEnergy(player, addNumbers(player, getUserData.getString("Minigames-Addon.Energy-Factory.Energy"), addAmount, false));
    }

    public void removeEnergy(Player player, String addAmount) {
        ConfigMaker getUserData = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/userdata/");
        setEnergy(player, subtractNumbers(player, getUserData.getString("Minigames-Addon.Energy-Factory.Energy"), addAmount, false));
    }


    StringBuilder subtractLoop(Player player, Boolean debugMode, char[] newAmount, char[] storedNumber) {
        try {
            StringBuilder finishedAmount = new StringBuilder();
            boolean minusOne = false;

            for (int i = newAmount.length - 1; i > -1; i--) {
                int base = Integer.parseInt(String.valueOf(storedNumber[i]));
                int subtract = Integer.parseInt(String.valueOf(newAmount[i]));
                int newNum;

                if (minusOne) subtract++;

                if (debugMode) {
                    player.sendMessage("§3Previous negative: §a" + minusOne);
                    player.sendMessage("§3Current Formula: §a" + base + " - " + subtract + " = " + (base - subtract));
                }
                if (base - subtract < 0) {
                    minusOne = true;
                    newNum = (10 + base) - subtract;
                } else {
                    minusOne = false;
                    newNum = base - subtract;
                }
                finishedAmount.insert(0, newNum);
            }
            if (!minusOne) finishedAmount.insert(0, "N");
            else finishedAmount.insert(0, "Y");
            return finishedAmount;
        } catch (Exception e) {
            player.sendMessage("§Please enter valid numbers.");
            return null;
        }
    }


    public ItemStack createItem(String name, Material mat, List<String> lore) {
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void setFullInventoryItem(Player player, int size, String name, Material mat, List<String> lore) {
        for (int slot = 0; slot < size; slot++) player.getOpenInventory().getTopInventory().setItem(slot, createItem(name, mat, lore));
    }

    private Inventory snakeInventoryCreation() { return Bukkit.createInventory(this, 54, "§c§lSnake"); }
    public Inventory snakeGetInventory() { return snakeInventoryCreation(); }

    private Inventory snakeSpeedInventoryCreation() { return Bukkit.createInventory(this, 27, "§a§lSnake Speed Selector"); }
    public Inventory snakeSpeedGetInventory() { return snakeSpeedInventoryCreation(); }

    private Inventory menuInventoryCreation() { return Bukkit.createInventory(this, 27, "§2§lMinigames"); }
    public Inventory menuGetInventory() { return menuInventoryCreation(); }

    private Inventory energyInventoryCreation() { return Bukkit.createInventory(this, 45, "  §e⚡⚡⚡ §a§lEnergy Factory §e⚡⚡⚡"); }
    public Inventory energyGetInventory() { return energyInventoryCreation(); }

    @Override
    public Inventory getInventory() { return null; }

}
