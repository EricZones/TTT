// Created by Eric B. 01.06.2020 21:12
package de.ericzones.ttt.listener;

import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.cloudperms.CloudPermissionsManagement;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;

import java.util.HashMap;

public class ChatListener implements Listener {

    private Main plugin;
    private HashMap<Player, Long> spam;
    private HashMap<Player, String> again;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
        spam = new HashMap<>();
        again = new HashMap<>();
    }

    @EventHandler
    public void onLobbyChat(AsyncPlayerChatEvent e) {
        if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        Player player = e.getPlayer();
        String msg2 = checkMessage(player, e.getMessage().trim());
        if(msg2 == null) {
            e.setCancelled(true);
            return;
        }
        String msg = checkColorCodes(player, msg2);
        if(msg == null) {
            e.setCancelled(true);
            return;
        }
        e.setFormat(getLobbyFormat(player) + msg);
    }

    @EventHandler
    public void onIngameChat(AsyncPlayerChatEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = e.getPlayer();
        String msg = checkMessage(player, e.getMessage().trim());
        if(msg == null) {
            e.setCancelled(true);
            return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isProtected()) {
            e.setFormat(getLobbyFormat(player) + msg);
            return;
        }
        if(ingameState.getSpectators().contains(player)) {
            e.setCancelled(true);
            for(Player current : ingameState.getSpectators())
                current.sendMessage(getSpectatorFormat(player)+msg);
            return;
        }
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);
        if(playerRole == Role.DETECTIVE || playerRole == Role.INNOCENT) {
            if(msg.startsWith("@v ") || msg.startsWith("@V ") || msg.startsWith("@t ") || msg.startsWith("@T ")) {
                e.setCancelled(true);
                player.sendMessage(Utils.prefix + "§cDu kannst nicht im §4Verräter-Chat §cschreiben");
                return;
            }
            e.setFormat(getIngameFormat(player)+msg);
            return;
        }

        if(playerRole == Role.TRAITOR) {
            e.setCancelled(true);
            if(msg.startsWith("@v ") || msg.startsWith("@V ") || msg.startsWith("@t ") || msg.startsWith("@T ")) {
                msg = msg.replace("@v ", "");
                msg = msg.replace("@V ", "");
                msg = msg.replace("@t ", "");
                msg = msg.replace("@T ", "");
                if(msg.length() == 0) {
                    player.sendMessage(Utils.prefix_chat + "§cDeine Nachricht ist leer");
                    return;
                }

                for(Player current : Bukkit.getOnlinePlayers()) {
                    Role currentRole = plugin.getRoleManager().getPlayerRole(current);
                    if(currentRole == Role.TRAITOR)
                        current.sendMessage(getTraitorChatFormat(player)+msg);
                }
                return;
            }

            for(Player current : Bukkit.getOnlinePlayers()) {
                Role currentRole = plugin.getRoleManager().getPlayerRole(current);
                if(currentRole == Role.TRAITOR)
                    current.sendMessage(getIngameFormat(player)+msg);
                else
                    current.sendMessage(getFakeTraitorFormat(player)+msg);
            }
        }

    }

    private String getTraitorChatFormat(Player player) {
        return "§4V§8-§4Chat §8┃§7 "+player.getName()+" §8» §7";
    }

    private String getFakeTraitorFormat(Player player) {
        return Role.INNOCENT.getName()+" §8┃§7 "+player.getName()+" §8» §7";
    }

    private String getIngameFormat(Player player) {
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);
        return playerRole.getName()+" §8┃§7 "+player.getName()+" §8» §7";
    }

    private String getSpectatorFormat(Player player) {
        return "§7Spectator §8┃ §7"+player.getName()+" §8» §7";
    }

    private String getLobbyFormat(Player player) {
        IPermissionUser user = CloudPermissionsManagement.getInstance().getUser(player.getUniqueId());
        String rank = CloudPermissionsManagement.getInstance().getHighestPermissionGroup(user).getPrefix();
        return rank+" §8┃§7 "+player.getName()+" §8» §7";
    }

    private String checkColorCodes(Player player, String message) {
        String msg = message;
        msg = msg.replace("%", "$");
        if(player.hasPermission("essentials.chat.color.all")) {
            msg = msg.replace("&0", "§0");
            msg = msg.replace("&1", "§1");
            msg = msg.replace("&2", "§2");
            msg = msg.replace("&3", "§3");
            msg = msg.replace("&4", "§4");
            msg = msg.replace("&5", "§5");
            msg = msg.replace("&6", "§6");
            msg = msg.replace("&7", "§7");
            msg = msg.replace("&8", "§8");
            msg = msg.replace("&9", "§9");
            msg = msg.replace("&a", "§a");
            msg = msg.replace("&b", "§b");
            msg = msg.replace("&c", "§c");
            msg = msg.replace("&d", "§d");
            msg = msg.replace("&e", "§e");
            msg = msg.replace("&f", "§f");
            msg = msg.replace("&r", "§r");
            msg = msg.replace("&k", "§k");
            msg = msg.replace("&m", "§m");
            msg = msg.replace("&n", "§n");
            msg = msg.replace("&l", "§l");
            msg = msg.replace("&o", "§o");
        } else if(player.hasPermission("essentials.chat.color")) {
            msg = msg.replace("&0", "§0");
            msg = msg.replace("&1", "§1");
            msg = msg.replace("&2", "§2");
            msg = msg.replace("&3", "§3");
            msg = msg.replace("&4", "§4");
            msg = msg.replace("&5", "§5");
            msg = msg.replace("&6", "§6");
            msg = msg.replace("&7", "§7");
            msg = msg.replace("&8", "§8");
            msg = msg.replace("&9", "§9");
            msg = msg.replace("&a", "§a");
            msg = msg.replace("&b", "§b");
            msg = msg.replace("&c", "§c");
            msg = msg.replace("&d", "§d");
            msg = msg.replace("&e", "§e");
            msg = msg.replace("&f", "§f");
            msg = msg.replace("&r", "§r");
        }
        if(msg.length() == 2) {
            if(msg.startsWith("§") && (msg.endsWith("0") || msg.endsWith("1") || msg.endsWith("2") || msg.endsWith("3") || msg.endsWith("4") || msg.endsWith("5") || msg.endsWith("6") || msg.endsWith("7") || msg.endsWith("8") || msg.endsWith("9")
                    || msg.endsWith("a") || msg.endsWith("b") || msg.endsWith("c") || msg.endsWith("d") || msg.endsWith("e") || msg.endsWith("f") || msg.endsWith("r"))) {
                msg = null;
                player.sendMessage(Utils.prefix_chat + "§cDeine Nachricht enthält nur Farbcodes");
            }
        }
        return msg;
    }

    private String checkMessage(Player player, String message) {
        String msg = message;
        if(!player.hasPermission("essentials.chat.admin")) {
            float uppercaseLetter = 0;
            for (int i = 0; i < msg.length(); i++) {
                if (Character.isUpperCase(msg.charAt(i)) && Character.isLetter(msg.charAt(i))) {
                    uppercaseLetter++;
                }
            }
            if (uppercaseLetter / (float) msg.length() > 0.3F) {
                msg = msg.toLowerCase();
            }
            if (spam.containsKey(player) || again.containsKey(player)) {
                if (spam.get(player) > System.currentTimeMillis()) {
                    msg = null;
                    player.sendMessage(Utils.prefix_chat + "§cDu schreibst zu schnell...");
                } else if (again.get(player).equalsIgnoreCase(msg)) {
                    msg = null;
                    player.sendMessage(Utils.prefix_chat + "§cDu wiederholst dich...");
                } else {
                    again.put(player, msg);
                    spam.put(player, System.currentTimeMillis() + 2 * 1000);
                }
            } else {
                spam.put(player, System.currentTimeMillis() + 2 * 1000);
                again.put(player, msg);
            }
        }
        return msg;
    }


}
