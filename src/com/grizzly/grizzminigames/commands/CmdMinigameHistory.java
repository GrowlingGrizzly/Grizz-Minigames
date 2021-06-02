package com.grizzly.grizzminigames.commands;

import com.grizzly.grizzmain.Grizz;
import com.grizzly.grizzmain.util.ConfigMaker;
import com.grizzly.grizzminigames.util.GameMessagesUtil;
import com.grizzly.grizzminigames.util.MinigamesUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdMinigameHistory implements TabExecutor {

    MinigamesUtil minigamesUtil = new MinigamesUtil();
    GameMessagesUtil messages = new GameMessagesUtil();

    Grizz plugin = Grizz.pluginMain;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) sender.sendMessage(plugin.playerOnly());
        else if (!minigamesUtil.logGames()) sender.sendMessage(messages.logDisabled());
        else if (args.length > 0 && args.length < 3)

            try {
                Player player = (Player) sender;
                Player target = Bukkit.getPlayer(args[0]);

                if (cmd.getName().equalsIgnoreCase("minigameHistory")) {

                    int page;
                    if (args.length == 1) page = 0; else
                        try {
                            page = Integer.parseInt(args[1]) - 1;
                        } catch (Exception e) {
                            player.sendMessage(plugin.aDR(plugin.getMessage().getString("History-Invalid-Page")));
                            return true;
                        } if (page < 0) {
                        player.sendMessage(plugin.aDR(plugin.getMessage().getString("History-Invalid-Page")));
                        return true;
                    }

                    int num = minigamesUtil.checkNextLogSpace(player) - 1 - (page * 3);
                    String historyMsg = getHistoryMessage().replaceAll("%page%", String.valueOf(page+1));
                    String historyList = "";

                    boolean historyFound = false;
                    if (addHistory(player, target, num, historyFound).equals("History-Empty")) return true;
                    historyFound = true;

                    int numberPerPage = plugin.getMinigame().getInt("Minigame-Log.Logs-Per-Page");

                    for (int i = 0; i < numberPerPage; i++) {
                        historyList = historyList.concat(addHistory(player, target, num, historyFound) + "\n");
                        num--;
                    }

                    historyMsg = historyMsg.replaceAll("%history%", historyList);
                    player.sendMessage(plugin.aDR(historyMsg)
                            .replaceAll("%player%", target.getName())
                            .replaceAll("History-Empty", "")
                            .replaceAll("History-Finished", ""));
                }
                return true;
            } catch (NullPointerException e) {
                sender.sendMessage(plugin.playerOffline());
                e.printStackTrace();
                return true;
            }
        else sender.sendMessage(plugin.commandSyntax("/minigamehistory <name> <page>"));
        return true;
    }

    String getHistoryMessage() {

        List<String> historyMsg = plugin.getMessage().getStringList("History-Format");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plugin.getMessage().getStringList("History-Format").toArray().length; i++) {
            sb.append((historyMsg.get(i)));
            sb.append('\n');
        }

        return sb.toString();
    }

    String addHistory(Player player, Player target, int num, boolean foundHistoryBefore) {

        try {

            StringBuilder sb = new StringBuilder();

            ConfigMaker playerDb = new ConfigMaker(Grizz.pluginMain, String.valueOf(player.getUniqueId()), plugin.getDataFolder() + "/minigames/log/");
            String gameDate = playerDb.getString(target.getName() + "." + num + ".Date");
            String gameType = playerDb.getString(target.getName() + "." + num + ".Type").toLowerCase();
            String gameBet = minigamesUtil.formatBalance.format(playerDb.getDouble(player.getName() + "." + num + ".Bet"));
            String gameEnd = playerDb.getString(target.getName() + "." + num + ".End").toLowerCase();

            List<String> historyMsg = plugin.getMessage().getStringList("Game-History");
            List<String> bjList = plugin.getMessage().getStringList("Game-History-Blackjack");
            List<String> hlList = plugin.getMessage().getStringList("Game-History-Highlow");

            for (int i = 1; i < historyMsg.toArray().length; i++) {
                sb.append((historyMsg.get(i - 1)));
                sb.append('\n');
            }

            if (gameType.equalsIgnoreCase("blackjack")) {
                String botCards = playerDb.getString(target.getName() + "." + num + ".Bot Cards");
                String botScore = playerDb.getString(target.getName() + "." + num + ".Bot Score");
                String playerCards = playerDb.getString(target.getName() + "." + num + ".Player Cards");
                String playerScore = playerDb.getString(target.getName() + "." + num + ".Player Score");
                for (int i = 0; i < bjList.toArray().length; i++) {
                    sb.append(bjList.get(i)
                            .replaceAll("%botCards%", botCards)
                            .replaceAll("%botScore%", botScore)
                            .replaceAll("%playerCards%", playerCards)
                            .replaceAll("%playerScore%", playerScore));
                    sb.append('\n');
                }
            }

            if (gameType.equalsIgnoreCase("highlow")) {
                double multiplier = playerDb.getDouble(target.getName() + "." + num + ".Multiplier");
                String lastCard = playerDb.getString(target.getName() + "." + num + ".Last Card");
                for (int i = 0; i < hlList.toArray().length; i++) {
                    sb.append(hlList.get(i)
                            .replaceAll("%lastCard%", lastCard)
                            .replaceAll("%multiplier%", String.valueOf(multiplier)));
                    sb.append('\n');
                }
            }

            sb.append(historyMsg.get(historyMsg.toArray().length - 1));

            return sb.toString()
                    .replaceAll("%date%", gameDate)
                    .replaceAll("%game%", firstUpperCase(gameType))
                    .replaceAll("%bet%", gameBet)
                    .replaceAll("%end%", firstUpperCase(gameEnd));
        } catch (NullPointerException e) {
            if (foundHistoryBefore) return "History-Finished"; else {
                player.sendMessage(plugin.aDR(plugin.getMessage().getString("No-History").replaceAll("%player%", target.getName())));
                return "History-Empty";
            }
        }
    }

    String firstUpperCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return null;
        return new ArrayList<>();
    }

}
