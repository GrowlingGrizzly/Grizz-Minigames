package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzminigames.GrizzMinigames;
import com.grizzly.grizzminigames.util.GameMessagesUtil;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdAceyDoucey implements TabExecutor {

    Grizz plugin = Grizz.pluginMain;
    GrizzMinigames pluginMinigames = GrizzMinigames.pluginMinigames;
    MinigamesUtil minigameUtil = new MinigamesUtil();
    GameMessagesUtil msgs = new GameMessagesUtil();
    String permNode = pluginMinigames.node("aceydoucey.useadminflags");
    public static HashMap<UUID, ArrayList<String>> adGetDeck = new HashMap<>();
    public static HashMap<UUID, Boolean> adIsGameEnabled = new HashMap<>();
    public static HashMap<UUID, ArrayList<Integer>> adPlayerDeck = new HashMap<>();
    public static HashMap<UUID, Double> adPlayerBet = new HashMap<>();
    public static HashMap<UUID, String> adThirdCard = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (cmd.getName().equalsIgnoreCase("aceydoucey")) {

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(getHelpMsg(false));
                    return true;
                }
            }

            boolean forceStart = minigameUtil.addFlag(player, "forcestart", permNode, args, 3);
            boolean showDeck = minigameUtil.addFlag(player, "showdeck", permNode, args, 3);
            boolean ignoreBet = minigameUtil.addFlag(player, "ignorebet", permNode, args, 3);

            if (forceStart) adIsGameEnabled.remove(uuid);

            if (adIsGameEnabled.get(uuid) == null || !adIsGameEnabled.get(uuid)) {

                if (minigameUtil.getBet(player, adPlayerBet, args, ignoreBet)) return true;

                if (minigameUtil.calculateCooldown(player, "Aceydoucey", forceStart)) return true;

                adIsGameEnabled.put(uuid, true);
                adPlayerDeck.remove(uuid);
                adThirdCard.remove(uuid);
                adGetDeck.put(uuid, minigameUtil.createMixedDeck());
                if (showDeck) player.sendMessage("§c" + adGetDeck.get(uuid));
                minigameUtil.takeTokens(player, adPlayerBet.get(uuid));

                if (minigameUtil.getFirstPlayHighLow(player)) {
                    player.sendMessage(getHelpMsg(true));
                    minigameUtil.setNotFirstTimeHighLow(player);
                }

                updateCard(uuid, player, true, null);

            } else sender.sendMessage(msgs.gameRunning());
        }
        return true;
    }

    public void updateCard(UUID uuid, Player player, Boolean startingGame, Boolean inRange) {

        if (!startingGame) {
            player.sendMessage(plugin.aDR(plugin.getMessage().getString("Aceydoucey.Third-Card").replaceAll("%card%", adThirdCard.get(uuid))));
            if (inRange) {
                if (!checkRange(player)) player.sendMessage(msgs.gameLost(adPlayerBet.get(uuid)));
                else player.sendMessage(msgs.gameWon(adPlayerBet.get(uuid)));
            } else {
                if (checkRange(player)) player.sendMessage(msgs.gameLost(adPlayerBet.get(uuid)));
                else player.sendMessage(msgs.gameWon(adPlayerBet.get(uuid)));
            }
            adIsGameEnabled.remove(uuid);
        }

        if (adIsGameEnabled.get(uuid) != null && adIsGameEnabled.get(uuid)) {

        ArrayList<String> currentDeck = new ArrayList<>(Arrays.asList(adGetDeck.get(uuid).get(0), adGetDeck.get(uuid).get(1), adGetDeck.get(uuid).get(2)));

        ArrayList<String> deck = minigameUtil.formatMultipleDeckNumOnly(new ArrayList<>(currentDeck));
        ArrayList<String> visibleDeck = minigameUtil.formatMultipleDeckNumSuit(new ArrayList<>(currentDeck));
        adThirdCard.put(uuid, visibleDeck.get(2));
        adPlayerDeck.putIfAbsent(uuid, new ArrayList<>());

        for (int i = 0; i < deck.toArray().length; i++) {
            int cardValue = minigameUtil.getCardValueDifferentFaceValues(deck.get(i));
            adGetDeck.get(uuid).remove(0);
            ArrayList<Integer> playerDeckValue = adPlayerDeck.get(uuid);
            playerDeckValue.add(cardValue);
            adPlayerDeck.put(uuid, playerDeckValue);
        }

        player.sendMessage(plugin.aDR(plugin.getMessage().getString("Aceydoucey.Outside-In-Between")));
        player.sendMessage(plugin.aDR((plugin.getMessage().getString("Aceydoucey.Current-Cards").replaceAll("%cards%", (visibleDeck.get(0) + ", " + visibleDeck.get(1)).replaceAll("\\[", "").replaceAll("]", "")))));

        if (adIsGameEnabled.get(uuid) != null && adIsGameEnabled.get(uuid)) player.sendMessage(plugin.aDR(plugin.getMessage().getString("Aceydoucey.In-Out")));

        }
    }

    boolean checkRange(Player player) {
        ArrayList<Integer> deck = adPlayerDeck.get(player.getUniqueId());
        int card1 = deck.get(0);
        int card2 = deck.get(1);
        int card3 = deck.get(2);
        if (card1 > card2) {
            card1 = deck.get(1);
            card2 = deck.get(0);
        }
        return (card3 > card1 && card3 < card2);
    }

    public void playerContinue(Player player, Boolean inRange) {
        String type;
        if (inRange) type = "In range"; else type = "Out of range";
        player.sendMessage(msgs.choose(type) + "\n\n§f ");
        updateCard(player.getUniqueId(), player, false, inRange);
    }

    String getHelpMsg(Boolean firstTime) {
        return minigameUtil.getHelpMessage(firstTime, "AceyDoucey").replaceAll("%maxMultiplier%", String.valueOf(plugin.getMinigame().getDouble("Highlow.Max-Multiplier")));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return new ArrayList<>(Arrays.asList("help", "<bet>"));
        else return new ArrayList<>();
    }
}
