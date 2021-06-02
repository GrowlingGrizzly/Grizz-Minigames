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

public class CmdBlackJack implements TabExecutor {

    Grizz plugin = Grizz.pluginMain;
    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    MinigamesUtil minigameUtil = new MinigamesUtil();
    GameMessagesUtil msgs = new GameMessagesUtil();
    String permNode = pluginMinigames.node("blackjack.useadminflags");
    public static HashMap<UUID, ArrayList<String>> bjGetDeck = new HashMap<>();
    public static HashMap<UUID, Integer> bjGetPlayerScore = new HashMap<>();
    public static HashMap<UUID, Integer> bjGetBotScore = new HashMap<>();
    public static HashMap<UUID, Boolean> bjIsBotAce11 = new HashMap<>();
    public static HashMap<UUID, Boolean> bjIsPlayerAce11 = new HashMap<>();
    public static HashMap<UUID, ArrayList<String>> bjGetPlayerDeck = new HashMap<>();
    public static HashMap<UUID, ArrayList<String>> bjGetBotDeck = new HashMap<>();
    public static HashMap<UUID, Boolean> bjIsGameEnabled = new HashMap<>();
    public static HashMap<UUID, Double> bjPlayerBet = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (cmd.getName().equalsIgnoreCase("blackjack")) {

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(getHelpMsg(false));
                    return true;
                }
            }

            boolean forceStart = minigameUtil.addFlag(player, "forcestart", permNode, args, 3);
            boolean showDeck = minigameUtil.addFlag(player, "showdeck", permNode, args, 3);
            boolean ignoreBet = minigameUtil.addFlag(player, "ignorebet", permNode, args, 3);

            if (forceStart) bjIsGameEnabled.remove(uuid);

            if (bjIsGameEnabled.get(uuid) == null || !bjIsGameEnabled.get(uuid)) {

                if (minigameUtil.getBet(player, bjPlayerBet, args, ignoreBet)) return true;

                if (minigameUtil.calculateCooldown(player, "Blackjack", forceStart)) return true;

                bjIsGameEnabled.put(uuid, true);
                bjGetDeck.put(uuid, minigameUtil.createMixedDeck());
                if (showDeck) player.sendMessage("§c" + bjGetDeck.get(uuid));
                bjGetPlayerScore.remove(uuid);
                bjGetBotScore.remove(uuid);
                bjIsPlayerAce11.remove(uuid);
                bjIsBotAce11.remove(uuid);
                minigameUtil.takeTokens(player, bjPlayerBet.get(uuid));

                if (minigameUtil.getFirstPlayBlackJack(player)) {
                    player.sendMessage(getHelpMsg(true));
                    minigameUtil.setNotFirstTimeBlackJack(player);
                }

                ArrayList<String> playerDeck = new ArrayList<>();
                for (int i = 0; i < 2; i++) playerDeck.add(bjGetDeck.get(uuid).get(i));
                bjGetPlayerDeck.put(uuid, playerDeck);

                ArrayList<String> botDeck = new ArrayList<>();
                botDeck.add(bjGetDeck.get(uuid).get(2));
                bjGetBotDeck.put(uuid, botDeck);

                bjGetDeck.get(uuid).subList(0, 3).clear();

                updateBotDeck(uuid, bjGetBotDeck.get(uuid), player, true);
                updatePlayerDeck(uuid, bjGetPlayerDeck.get(uuid), player, true);

            } else sender.sendMessage(msgs.gameRunning());
        }
        return true;
    }

    public void updatePlayerDeck(UUID uuid, ArrayList<String> playerDeck, Player player, Boolean checkFor21) {

        ArrayList<String> correctedPlayerDeck = new ArrayList<>();

        ArrayList<String> deck = minigameUtil.formatMultipleDeckNumOnly(new ArrayList<>(playerDeck));
        ArrayList<String> visibleDeck = new ArrayList<>(playerDeck);

        bjGetPlayerScore.put(uuid, 0);

        for (int i = 0; i < deck.toArray().length; i++) {
            int cardValue = getCardValue(deck.get(i), uuid);
            bjGetPlayerScore.put(uuid, bjGetPlayerScore.get(uuid) + cardValue);
            correctedPlayerDeck.add(formatDeckNumSuit(visibleDeck.get(i)));
            if (bjGetPlayerScore.get(uuid) > 21) if (bjIsPlayerAce11.get(uuid) != null) if (bjIsPlayerAce11.get(uuid)) { bjGetPlayerScore.put(uuid, bjGetPlayerScore.get(uuid) - 10); bjIsPlayerAce11.put(uuid, false); }
        }
        player.sendMessage(plugin.aDR(plugin.getMessage().getString("BlackJack.Player-Cards").replaceAll("%cards%", String.valueOf(correctedPlayerDeck).replaceAll("\\[", "").replaceAll("]", "")).replaceAll("%points%", String.valueOf(bjGetPlayerScore.get(uuid)))));
        if (checkFor21) if (bjGetPlayerScore.get(uuid) == 21) playerStand(player, true);
        if (checkFor21) if (bjGetPlayerScore.get(uuid) < 21) player.sendMessage(plugin.aDR(plugin.getMessage().getString("BlackJack.Hit-Stand-DD")));
        if (bjGetPlayerScore.get(uuid) > 21) { player.sendMessage(msgs.gameLost(bjPlayerBet.get(uuid))); gameEnded(uuid, player, "LOST"); }
    }

    public void updateBotDeck(UUID uuid, ArrayList<String> botDeck, Player player, boolean sendDeck) {

        ArrayList<String> correctedBotDeck = new ArrayList<>();
        ArrayList<String> deck = minigameUtil.formatMultipleDeckNumOnly(new ArrayList<>(botDeck));
        ArrayList<String> visibleDeck = new ArrayList<>(botDeck);

        bjGetBotScore.put(uuid, 0);

        for (int i = 0; i < deck.toArray().length; i++) {
            int cardValue = getCardValue(deck.get(i), uuid);
            bjGetBotScore.put(uuid, bjGetBotScore.get(uuid) + cardValue);
            correctedBotDeck.add(formatDeckNumSuit(visibleDeck.get(i)));
            if (bjGetBotScore.get(uuid) > 21) if (bjIsBotAce11.get(uuid) != null && bjIsBotAce11.get(uuid)) { bjGetBotScore.put(uuid, bjGetBotScore.get(uuid) - 10); bjIsBotAce11.put(uuid, false); }
        }
        if (sendDeck) player.sendMessage(plugin.aDR(plugin.getMessage().getString("BlackJack.Bot-Cards").replaceAll("%cards%", String.valueOf(correctedBotDeck).replaceAll("\\[", "").replaceAll("]", "")).replaceAll("%points%", String.valueOf(bjGetBotScore.get(uuid)))));
    }

    String getHelpMsg(Boolean firstTime) {
        return minigameUtil.getHelpMessage(firstTime, "BlackJack").replaceAll("%botStandValue%", String.valueOf(plugin.getMinigame().getInt("Blackjack.Bot-Stands-At")));
    }


    String formatDeckNumSuit(String deck) {
        deck = minigameUtil.formatDeckBase(deck, "NS");
        return deck;
    }

    int getCardValue(String card, UUID uuid) {
        int cardValue = 0;
        switch (card) {
            case "A": if (bjGetBotScore.get(uuid) < 11) { cardValue = 11; bjIsBotAce11.put(uuid, true); } else { cardValue = 1; bjIsBotAce11.put(uuid, false); } break;
            case "J": case "Q": case "K":
                cardValue = 10; break;
        }
        for (int i = 2; i < 11; i++) if (card.equals(Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10").get(i - 2))) cardValue = i;
        return cardValue;
    }

    public void playerHit(Player player) {
        UUID uuid = player.getUniqueId();
        player.sendMessage(msgs.choose("Hit") + "\n\n§f ");
        bjGetPlayerDeck.get(uuid).add(bjGetDeck.get(uuid).get(0));
        bjGetDeck.get(uuid).remove(0);

        updateBotDeck(uuid, bjGetBotDeck.get(uuid), player, true);
        updatePlayerDeck(uuid, bjGetPlayerDeck.get(uuid), player, true);

    }

    public void playerStand(Player player, Boolean isNotPlayerInput) {
        if (!isNotPlayerInput) player.sendMessage(msgs.choose("Stand") + "\n\n§f ");
        UUID uuid = player.getUniqueId();

        while (bjIsGameEnabled.get(uuid) != null && bjIsGameEnabled.get(uuid)) {

            if (bjGetBotScore.get(uuid) < plugin.getMinigame().getInt("Blackjack.Bot-Stands-At")) {
                bjGetBotDeck.get(uuid).add(bjGetDeck.get(uuid).get(0));
                bjGetDeck.get(uuid).remove(0);
                updateBotDeck(uuid, bjGetBotDeck.get(uuid), player, false);
            } else {
                String wLT = null;
                String EndType = null;
                updateBotDeck(uuid, bjGetBotDeck.get(uuid), player, true);
                updatePlayerDeck(uuid, bjGetPlayerDeck.get(uuid), player, false);
                if (bjGetPlayerScore.get(uuid) < bjGetBotScore.get(uuid) && bjGetBotScore.get(uuid) < 22) { wLT = msgs.gameLost(bjPlayerBet.get(uuid)); EndType = "LOST"; }
                if (bjGetPlayerScore.get(uuid) > bjGetBotScore.get(uuid) && bjGetPlayerScore.get(uuid) < 22 || bjGetBotScore.get(uuid) > 21 && bjGetPlayerScore.get(uuid) < 22 )
                { wLT = msgs.gameWon(bjPlayerBet.get(uuid)); minigameUtil.addTokens(player, bjPlayerBet.get(uuid) * 2); EndType = "WON"; }
                if (bjGetPlayerScore.get(uuid).equals(bjGetBotScore.get(uuid))) { wLT = msgs.gameTied(); EndType = "TIED"; }
                player.sendMessage(wLT);
                gameEnded(uuid, player, EndType);
            }
        }
    }

    private void gameEnded(UUID uuid, Player player, String gameEnd) {
        if (minigameUtil.logGames()) setDb(player, "BLACKJACK", bjPlayerBet.get(uuid), gameEnd, bjGetBotDeck.get(uuid).toString(), bjGetBotScore.get(uuid), bjGetPlayerDeck.get(uuid).toString(), bjGetPlayerScore.get(uuid));
        bjIsGameEnabled.remove(uuid);
    }

    public void setDb(Player player, String type, double bet, String end, String botCards, int botScore, String playerCards, int playerScore) {
        ConfigMaker playerDb = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/log/");

        int num = minigameUtil.checkNextLogSpace(player);

        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(plugin.getMessage().getString("Date-Format"));

        playerDb.set(player.getName() + "." + num + ".Date", time.format(formatter));
        playerDb.set(player.getName() + "." + num + ".Type", type);
        playerDb.set(player.getName() + "." + num + ".Bet", bet);
        playerDb.set(player.getName() + "." + num + ".End", end);
        playerDb.set(player.getName() + "." + num + ".Bot Cards", minigameUtil.formatCardsLog(botCards));
        playerDb.set(player.getName() + "." + num + ".Bot Score", botScore);
        playerDb.set(player.getName() + "." + num + ".Player Cards", minigameUtil.formatCardsLog(playerCards));
        playerDb.set(player.getName() + "." + num + ".Player Score", playerScore);
        playerDb.save();
    }


    public void playerDoubleDown(Player player) {
        UUID uuid = player.getUniqueId();

        if (bjPlayerBet.get(uuid) > minigameUtil.getTokens(player)) {
            player.sendMessage(msgs.tooHighDD());
        } else {
            player.sendMessage(msgs.choose("Double Down") + "\n\n§f ");

            bjGetPlayerDeck.get(uuid).add(bjGetDeck.get(uuid).get(0));
            bjGetDeck.get(uuid).remove(0);
            minigameUtil.takeTokens(player, bjPlayerBet.get(uuid));
            bjPlayerBet.put(uuid, bjPlayerBet.get(uuid) * 2);
            playerStand(player, true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Arrays.asList("help", "<bet>"));
        } else return new ArrayList<>();
    }
}
