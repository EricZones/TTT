// Created by Eric B. 01.06.2020 18:17
package de.ericzones.ttt.commands;

import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.listener.ProtectionListener;
import de.ericzones.ttt.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {

    private Main plugin;

    public BuildCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("ttt.build")) {
                ProtectionListener protectionListener = plugin.getProtectionListener();
                if(!protectionListener.getBuildPlayers().contains(player.getName())) {
                    protectionListener.getBuildPlayers().add(player.getName());
                    player.sendMessage(Utils.prefix + "§7Baumodus §aaktiviert");
                } else {
                    protectionListener.getBuildPlayers().remove(player.getName());
                    player.sendMessage(Utils.prefix + "§7Baumodus §cdeaktiviert");
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
