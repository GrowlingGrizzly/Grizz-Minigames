package com.grizzly.grizzminigames.util;

import com.grizzly.grizzmain.Grizz;
import org.bukkit.entity.Player;

public class GameMessagesUtil {

    Grizz plugin = Grizz.pluginMain;

    public String gameWon(Double bet) {
        return plugin.aDR(plugin.getMessage().getString("Win").replaceAll("%bet%", new MinigamesUtil().formatBalance.format(bet)));
    }

    public String gameWonHighLow(Double baseBet, Double bet, Double multiplier) {
        return plugin.aDR(plugin.getMessage().getString("Win-With-Multiplier")
                .replaceAll("%bet%", new MinigamesUtil().formatBalance.format(bet - baseBet))
                .replaceAll("%multiplier%", String.valueOf(multiplier)));
    }

    public String gameLost(Double bet) {
        return plugin.aDR(plugin.getMessage().getString("Lose").replaceAll("%bet%", new MinigamesUtil().formatBalance.format(bet)));
    }

    public String logDisabled() { return plugin.aDR(plugin.getMessage().getString("History-Disabled")); }

    public String gameTied() { return plugin.aDR(plugin.getMessage().getString("Tie")); }

    public String gameRunning() { return plugin.aDR(plugin.getMessage().getString("Game-Running")); }

    public String noBet() { return plugin.aDR(plugin.getMessage().getString("No-Bet")); }

    public String minBet() { return plugin.aDR(plugin.getMessage().getString("Min-Bet")); }

    public String tooHighBet(Player player) {
        return plugin.aDR(plugin.getMessage().getString("Too-High-Bet").replaceAll("%tokens%", new MinigamesUtil().formatBalance.format(new MinigamesUtil().getTokens(player))));
    }

    public String tooHighDD() { return plugin.aDR(plugin.getMessage().getString("BlackJack.Too-High-DD")); }

    public String invalidBet() { return plugin.aDR(plugin.getMessage().getString("Invalid-Bet")); }

    public String choose(String type) {
        return plugin.aDR(plugin.getMessage().getString("Choose").replaceAll("%type%", type));
    }

}
