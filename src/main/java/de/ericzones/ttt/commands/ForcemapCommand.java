// Created by Eric B. 06.06.2020 13:36
package de.ericzones.ttt.commands;

import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import de.ericzones.ttt.mapvoting.Voting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForcemapCommand implements CommandExecutor {

    private Main plugin;
    private Voting voting;

    public ForcemapCommand(Main plugin) {
        this.plugin = plugin;
        voting = plugin.getVoting();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("games.forcemap")) {
                if(args.length == 1) {
                    if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                        LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
                        Map map = new Map(plugin, args[0]);
                        if(map.exists()) {

                            for(int i = 0; i < voting.getVotingMaps().length; i++) {
                                String name = voting.getVotingMaps()[i].getName();
                                if(name.equalsIgnoreCase(args[0])) {
                                    voting.forceMap(player, i);
                                    return true;
                                }
                            }
                            player.sendMessage(Utils.prefix + "§cDiese Map wurde nicht gefunden");

                        } else {
                            player.sendMessage(Utils.prefix + "§cDiese Map wurde nicht gefunden");
                        }
                    } else {
                        player.sendMessage(Utils.prefix + "§cDas Spiel ist bereits gestartet");
                    }
                } else {
                    player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/forcemap <Map>§8)");
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
