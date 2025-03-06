// Created by Eric B. 28.05.2020 18:15
package de.ericzones.ttt.commands;

import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    private static final int START_SECONDS = 15;

    private Main plugin;
    public StartCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("games.start")) {
                if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                    LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
                    if(lobbyState.getCountdown().isRunning()) {
                        if(lobbyState.getCountdown().getSeconds() > START_SECONDS) {

                            lobbyState.getCountdown().setSeconds(START_SECONDS);
                            player.sendMessage(Utils.prefix + "§aDer Countdown wurde verkürzt");

                        } else {
                            player.sendMessage(Utils.prefix + "§cDas Spiel wurde bereits gestartet");
                        }
                    } else {
                        player.sendMessage(Utils.prefix + "§cDafür sind nicht genug Spieler in der Runde");
                    }
                } else {
                 player.sendMessage(Utils.prefix + "§cDas Spiel ist bereits gestartet");
                }
            } else {
                player.sendMessage(Utils.error_rechte);
            }
        } else {
            sender.sendMessage(Utils.error_console);
        }
        return false;
    }
}
