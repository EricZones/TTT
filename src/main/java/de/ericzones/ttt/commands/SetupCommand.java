// Created by Eric B. 28.05.2020 17:27
package de.ericzones.ttt.commands;

import de.ericzones.ttt.extra.LocationConfig;
import de.ericzones.ttt.extra.TesterSetup;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SetupCommand implements CommandExecutor {

    private Main plugin;
    public SetupCommand(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("ttt.setup")) {

                if(args.length == 0) {
                    player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup <Lobby,create,set,tester>§8)");
                } else {
                    if(args[0].equalsIgnoreCase("Lobby")) {
                        new LocationConfig(plugin, player.getLocation(), "Lobby", "Spawn", 1).saveLocation();
                        player.sendMessage(Utils.prefix + "§7Spawn für §aLobby §7gesetzt");

                    } else if(args[0].equalsIgnoreCase("Endinglobby")) {

                        new LocationConfig(plugin, player.getLocation(), "Lobby", "Endinglobby", 1).saveLocation();
                        player.sendMessage(Utils.prefix + "§7Spawn für §aEndinglobby §7gesetzt");

                    } else if(args[0].equalsIgnoreCase("create")) {
                        if (args.length == 3) {

                            Map map = new Map(plugin, args[1]);
                            if (!map.exists()) {

                                map.create(args[2]);
                                player.sendMessage(Utils.prefix + "§7Map §a" + args[1] + " §7erfolgreich erstellt");

                            } else {
                                player.sendMessage(Utils.prefix + "§cDiese Map existiert bereits");
                            }
                        } else {
                            player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup create <Map> <Erbauer>§8)");
                        }
                    } else if(args[0].equalsIgnoreCase("set")) {
                        if (args.length == 3) {
                            Map map = new Map(plugin, args[1]);
                            if (map.exists()) {
                                if (args[2].equalsIgnoreCase("Spectator") || args[2].equalsIgnoreCase("Spec")) {

                                    map.setSpectatorLocation(player.getLocation());
                                    player.sendMessage(Utils.prefix + "§7Spawn §aSpectator §7für §a" + map.getName() + " §7gesetzt");

                                } else {
                                    try {
                                        int spawnNumber = Integer.parseInt(args[2]);
                                        if (spawnNumber > 0 && spawnNumber <= LobbyState.MAX_PLAYERS) {
                                            map.setSpawnLocation(spawnNumber, player.getLocation());
                                            player.sendMessage(Utils.prefix + "§7Spawn §a" + spawnNumber + " §7für §a" + map.getName() + " §7gesetzt");
                                        } else {
                                            player.sendMessage(Utils.prefix + "§cDie Zahl muss §e1§8-§e" + LobbyState.MAX_PLAYERS + " §cbetragen");
                                        }
                                    } catch (NumberFormatException e) {
                                        player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup set <Map> <1-" + LobbyState.MAX_PLAYERS + ",Spectator>§8)");
                                    }
                                }
                            } else {
                                player.sendMessage(Utils.prefix + "§cDiese Map wurde nicht gefunden");
                            }
                        } else {
                            player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup set <Map> <1-" + LobbyState.MAX_PLAYERS + ",Spectator>§8)");
                        }
                    } else if(args[0].equalsIgnoreCase("tester")) {
                        if (args.length == 2) {
                            Map map = new Map(plugin, args[1]);
                            if (map.exists())
                                new TesterSetup(plugin, player, map);
                            else
                                player.sendMessage(Utils.prefix + "§cDiese Map wurde nicht gefunden");

                        } else {
                            player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup tester <Map>§8)");
                        }
                    } else if(args[0].equalsIgnoreCase("import")) {
                        if (args.length == 2) {
                            if (FileUtils.getFile(args[1]).exists()) {
                                if (args[1].length() <= 16) {

                                    player.sendMessage(Utils.prefix + "§7Welt wird importiert...");

                                    WorldCreator creator = (WorldCreator) WorldCreator.name(args[1]).environment(org.bukkit.World.Environment.NORMAL).type(WorldType.NORMAL);
                                    Bukkit.createWorld(creator);
                                    player.sendMessage(Utils.prefix + "§7Welt §a" + args[1] + " §7importiert");

                                } else {
                                    player.sendMessage(Utils.prefix + "§cMaximal §e16 §cZeichen erlaubt");
                                }
                            } else {
                                player.sendMessage(Utils.prefix + "§cWeltordner §e" + args[1] + " §cnicht gefunden");
                            }
                        } else {
                            player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup import <Welt>§8)");
                        }
                    } else if(args[0].equalsIgnoreCase("delete")) {
                        if(args.length == 2)  {
                            Map map = new Map(plugin, args[1]);
                            if(map.exists()) {

                                map.delete();
                                player.sendMessage(Utils.prefix + "§7Map §c"+map.getName()+" §7gelöscht");

                            } else {
                                player.sendMessage(Utils.prefix + "§cDiese Map wurde nicht gefunden");
                            }
                        } else {
                            player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup delete <Map>§8)");
                        }
                    } else {
                        player.sendMessage(Utils.prefix + "§cFalscher Syntax. §8(§7/setup <Lobby,create,set,tester>§8)");
                    }
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
