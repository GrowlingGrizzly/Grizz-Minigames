package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzmain.util.ConfigMaker;
import com.grizzly.grizzminigames.GrizzMinigames;
import com.grizzly.grizzminigames.util.GameMessagesUtil;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CmdHighLow implements TabExecutor {

    Grizz plugin = Grizz.pluginMain;
    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    MinigamesUtil minigameUtil = new MinigamesUtil();
    GameMessagesUtil msgs = new GameMessagesUtil();
    String permNode = pluginMinigames.node("highlow.useadminflags");
    public static HashMap<UUID, ArrayList<String>> hlGetDeck = new HashMap<>();
    public static HashMap<UUID, Double> hlGetPlayerMultiplier = new HashMap<>();
    public static HashMap<UUID, Boolean> hlIsGameEnabled = new HashMap<>();
    public static HashMap<UUID, String> hlCurrentCard = new HashMap<>();
    public static HashMap<UUID, Integer> hlLastCardValue = new HashMap<>();
    public static HashMap<UUID, Double> hlPlayerBet = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (cmd.getName().equalsIgnoreCase("highlow")) {

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(getHelpMsg(false));
                    return true;
                }
            }

            boolean forceStart = minigameUtil.addFlag(player, "forcestart", permNode, args, 3);
            boolean showDeck = minigameUtil.addFlag(player, "showdeck", permNode, args, 3);
            boolean ignoreBet = minigameUtil.addFlag(player, "ignorebet", permNode, args, 3);

            if (forceStart) hlIsGameEnabled.remove(uuid);

            if (hlIsGameEnabled.get(uuid) == null || !hlIsGameEnabled.get(uuid)) {

                if (minigameUtil.getBet(player, hlPlayerBet, args, ignoreBet)) return true;
                if (minigameUtil.calculateCooldown(player, "Highlow", forceStart)) return true;

                hlIsGameEnabled.put(uuid, true);
                hlGetDeck.put(uuid, minigameUtil.createMixedDeck());
                if (showDeck) player.sendMessage("§c" + hlGetDeck.get(uuid));
                hlGetPlayerMultiplier.remove(uuid);
                minigameUtil.takeTokens(player, hlPlayerBet.get(uuid));

                if (minigameUtil.getFirstPlayHighLow(player)) {
                    player.sendMessage(getHelpMsg(true));
                    minigameUtil.setNotFirstTimeHighLow(player);
                }

                updateCard(uuid, player, true, null);

            } else sender.sendMessage(msgs.gameRunning());
        }
        return true;
    }

    public void updateCard(UUID uuid, Player player, Boolean startingGame, Boolean goingHigher) {

        String card = formatDeckNumOnly(hlGetDeck.get(uuid).get(0));
        hlCurrentCard.put(uuid, formatDeckNumSuit(hlGetDeck.get(uuid).get(0)));
        String visibleCard = formatDeckNumSuit(hlGetDeck.get(uuid).get(0));

        if (startingGame) hlGetPlayerMultiplier.put(uuid, 0.0);
        int cardValue = minigameUtil.getCardValueDifferentFaceValues(card);
        hlGetDeck.get(uuid).remove(0);
        if (!startingGame) if (cardValue != hlLastCardValue.get(uuid)) hlGetPlayerMultiplier.put(uuid, hlGetPlayerMultiplier.get(uuid) + 0.5);
        int multiplier = (int) Math.ceil((hlPlayerBet.get(player.getUniqueId()) * hlGetPlayerMultiplier.get(player.getUniqueId())));

        player.sendMessage(plugin.aDR(plugin.getMessage().getString("HighLow.Current-Multiplier")
                .replaceAll("%multiplier%", String.valueOf(hlGetPlayerMultiplier.get(uuid)))
                .replaceAll("%currentTotalWinnings%", String.valueOf(multiplier))));
        player.sendMessage(plugin.aDR(plugin.getMessage().getString("HighLow.Current-Card").replaceAll("%card%", visibleCard)));

        if (!startingGame) {
            if (goingHigher) if (cardValue < hlLastCardValue.get(uuid)) { player.sendMessage(msgs.gameLost(hlPlayerBet.get(uuid))); gameEnded(uuid, player, "LOST"); }
            if (!goingHigher) if (cardValue > hlLastCardValue.get(uuid)) { player.sendMessage(msgs.gameLost(hlPlayerBet.get(uuid))); gameEnded(uuid, player, "LOST"); }
        }
        if (hlIsGameEnabled.get(uuid) != null && hlIsGameEnabled.get(uuid) && hlGetPlayerMultiplier.get(uuid) == plugin.getMinigame().getDouble("Highlow.Max-Multiplier")) {
            playerStop(player, true);
        }

        if (hlIsGameEnabled.get(uuid) != null && hlIsGameEnabled.get(uuid)) player.sendMessage(plugin.aDR(plugin.getMessage().getString("HighLow.Higher-Lower-CashOut")));

        hlLastCardValue.put(uuid, cardValue);
    }

    public void playerContinue(Player player, Boolean goingHigher) {
        String type = null;
        if (goingHigher) type = "Higher";
        if (!goingHigher) type = "Lower";
        player.sendMessage(msgs.choose(type) + "\n\n§f ");
        updateCard(player.getUniqueId(), player, false, goingHigher);
    }

    public void playerStop(Player player, Boolean isNotPlayerInput) {
        if (isNotPlayerInput) {
            player.sendMessage(plugin.aDR(plugin.getMessage().getString("HighLow.Multiplier-Maxed"))); hlIsGameEnabled.remove(player.getUniqueId());
            player.sendMessage(msgs.gameWonHighLow(hlPlayerBet.get(player.getUniqueId()), Math.ceil((hlPlayerBet.get(player.getUniqueId()) * hlGetPlayerMultiplier.get(player.getUniqueId()))), hlGetPlayerMultiplier.get(player.getUniqueId())));
            minigameUtil.addTokens(player, (int) Math.ceil((hlPlayerBet.get(player.getUniqueId()) * hlGetPlayerMultiplier.get(player.getUniqueId()))));
            gameEnded(player.getUniqueId(), player, "WON");
        }
        else if (hlGetPlayerMultiplier.get(player.getUniqueId()) < 1.0) player.sendMessage(plugin.aDR(plugin.getMessage().getString("HighLow.Stopping-Before-1x")));
        else {
            if (!isNotPlayerInput) player.sendMessage(msgs.choose("Cash Out") + "\n\n§f ");
            if (hlGetPlayerMultiplier.get(player.getUniqueId()) == 1) { player.sendMessage(msgs.gameTied()); gameEnded(player.getUniqueId(), player, "TIED");}
            else player.sendMessage(msgs.gameWonHighLow(hlPlayerBet.get(player.getUniqueId()), Math.ceil((hlPlayerBet.get(player.getUniqueId()) * hlGetPlayerMultiplier.get(player.getUniqueId()))), hlGetPlayerMultiplier.get(player.getUniqueId())));
            minigameUtil.addTokens(player, (int) Math.ceil((hlPlayerBet.get(player.getUniqueId()) * hlGetPlayerMultiplier.get(player.getUniqueId()))));
            gameEnded(player.getUniqueId(), player, "WON");
        }
    }

    private void gameEnded(UUID uuid, Player player, String gameEnd) {
        if (minigameUtil.logGames()) setDb(player, "HIGHLOW", hlPlayerBet.get(uuid), gameEnd, hlCurrentCard.get(uuid), hlGetPlayerMultiplier.get(uuid));
        hlIsGameEnabled.remove(uuid);
    }

    public void setDb(Player player, String type, double bet, String end, String currentCard, double playerMultiplier) {
        ConfigMaker playerDb = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/log/");

        int num = minigameUtil.checkNextLogSpace(player);

        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(plugin.getMessage().getString("Date-Format"));

        playerDb.set(player.getName() + "." + num + ".Date", time.format(formatter));
        playerDb.set(player.getName() + "." + num + ".Type", type);
        playerDb.set(player.getName() + "." + num + ".Bet", bet);
        playerDb.set(player.getName() + "." + num + ".End", end);
        playerDb.set(player.getName() + "." + num + ".Multiplier", playerMultiplier);
        playerDb.set(player.getName() + "." + num + ".Last Card", currentCard);
        playerDb.save();
    }

    String getHelpMsg(Boolean firstTime) {
        return minigameUtil.getHelpMessage(firstTime, "HighLow").replaceAll("%maxMultiplier%", String.valueOf(plugin.getMinigame().getDouble("Highlow.Max-Multiplier")));
    }

    String formatDeckNumSuit(String deck) {
        deck = minigameUtil.formatDeckBase(deck, "NS");
        return deck;
    }

    String formatDeckNumOnly(String deck) {
        deck = minigameUtil.formatDeckBase(deck, "N");
        return deck;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Arrays.asList("help", "<bet>"));
        } else return new ArrayList<>();
    }
}
