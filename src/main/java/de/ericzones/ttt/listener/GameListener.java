// Created by Eric B. 31.05.2020 19:09
package de.ericzones.ttt.listener;

import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.cloudperms.CloudPermissionsManagement;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.roles.Role;
import de.ericzones.ttt.roles.RoleManager;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {

    private Main plugin;
    private RoleManager roleManager;

    public GameListener(Main plugin) {
        this.plugin = plugin;
        this.roleManager = plugin.getRoleManager();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        if (e.getDamager() instanceof Player) {
            if (!(e.getEntity() instanceof Player)) return;
            if (!(plugin.getPlayers().contains((Player) e.getDamager()))) return;
            Player damager = (Player) e.getDamager(), victim = (Player) e.getEntity();
            Role damagerRole = roleManager.getPlayerRole(damager), victimRole = roleManager.getPlayerRole(victim);

            if ((damagerRole == Role.INNOCENT || damagerRole == Role.DETECTIVE) && victimRole == Role.DETECTIVE)
                damager.sendMessage(Utils.prefix + "§cDu hast einen Detektiv angegriffen");

            if (damagerRole == Role.TRAITOR && victimRole == Role.TRAITOR)
                e.setDamage(0);

        } else if(e.getDamager() instanceof Arrow) {
            if (!(e.getEntity() instanceof Player)) return;
            Arrow arrow = (Arrow) e.getDamager();
            if(!(arrow.getShooter() instanceof Player)) return;
            Player damager = (Player) arrow.getShooter();
            Player victim = (Player) e.getEntity();
            Role damagerRole = roleManager.getPlayerRole(damager), victimRole = roleManager.getPlayerRole(victim);

            if ((damagerRole == Role.INNOCENT || damagerRole == Role.DETECTIVE) && victimRole == Role.DETECTIVE)
                damager.sendMessage(Utils.prefix + "§cDu hast einen Detektiv angegriffen");

            if (damagerRole == Role.TRAITOR && victimRole == Role.TRAITOR)
                e.setDamage(0);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        Player victim = e.getEntity();
        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();
            Role killerRole = roleManager.getPlayerRole(killer), victimRole = roleManager.getPlayerRole(victim);

            switch (killerRole) {
                case TRAITOR:
                    if (victimRole == Role.TRAITOR) {
                        // !! Karma Abzug in Message
                        killer.sendMessage(Utils.prefix + "§cDu hast einen " + Role.TRAITOR.getName() + " §cgetötet");
                    } else {
                        // !! Karma Addition in Message
                        plugin.getRoleInventories().getPointManager().addPoints(killer, 2);
                        killer.sendMessage(Utils.prefix + "§7Du hast " + victimRole.getChatColor() + victim.getName() + " §7getötet §8[§a§l+2 §4V-Punkte§8]");
                        killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                    }
                    break;
                case DETECTIVE:
                    if (victimRole == Role.TRAITOR) {
                        // !! Karma Addition in Message
                        plugin.getRoleInventories().getPointManager().addPoints(killer, 2);
                        killer.sendMessage(Utils.prefix + "§7Du hast " + victimRole.getChatColor() + victim.getName() + " §7getötet §8[§a§l+2 §9D-Punkte§8]");
                        killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                    } else if (victimRole == Role.DETECTIVE) {
                        // !! Karma Abzug in Message
                        killer.sendMessage(Utils.prefix + "§cDu hast einen " + Role.DETECTIVE.getName() + " §cgetötet");
                    } else if (victimRole == Role.INNOCENT) {
                        // !! Karma Abzug in Message
                        killer.sendMessage(Utils.prefix + "§cDu hast einen §aUnschuldigen §cgetötet");
                    }
                    break;
                case INNOCENT:
                    if (victimRole == Role.TRAITOR) {
                        // !! Karma Addition in Message
                        killer.sendMessage(Utils.prefix + "§7Du hast " + victimRole.getChatColor() + victim.getName() + " §7getötet");
                        killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                    } else if (victimRole == Role.DETECTIVE) {
                        // !! Karma Abzug in Message
                        killer.sendMessage(Utils.prefix + "§cDu hast einen " + Role.DETECTIVE.getName() + " §cgetötet");
                    } else if (victimRole == Role.INNOCENT) {
                        // !! Karma Abzug in Message
                        killer.sendMessage(Utils.prefix + "§cDu hast einen §aUnschuldigen §cgetötet");
                    }
                    break;
                default:
                    break;
            }

            victim.sendMessage(Utils.prefix + "§cDu wurdest von " + killerRole.getChatColor() + killer.getName() + " §cgetötet");
            if (victimRole == Role.TRAITOR)
                plugin.getRoleManager().getTraitorPlayers().remove(victim.getName());
            plugin.removePlayer(victim);
            ingameState.checkGame();
        } else {
            victim.sendMessage(Utils.prefix + "§cDu bist gestorben");
            if (plugin.getRoleManager().getPlayerRole(victim) == Role.TRAITOR)
                plugin.getRoleManager().getTraitorPlayers().remove(victim.getName());
            plugin.removePlayer(victim);
            ingameState.checkGame();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = e.getPlayer();
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(plugin.getPlayers().contains(player)) {
            Role playerRole = roleManager.getPlayerRole(player);
            if(playerRole != null) {
                if (plugin.getRoleManager().getPlayerRole(player) == Role.TRAITOR)
                    plugin.getRoleManager().getTraitorPlayers().remove(player.getName());
                e.setQuitMessage("§c« " + playerRole.getChatColor() + player.getName() + " §7hat das Spiel verlassen");
            } else {
                IPermissionUser user = CloudPermissionsManagement.getInstance().getUser(player.getUniqueId());
                String color = CloudPermissionsManagement.getInstance().getHighestPermissionGroup(user).getColor();
                String name = color + player.getName();
                e.setQuitMessage("§c« " + name + " §7hat das Spiel verlassen");
            }

            plugin.removePlayer(player);
            ingameState.checkGame();
        } else if(ingameState.getSpectators().contains(player)) {
            e.setQuitMessage(null);
            ingameState.getSpectators().remove(player);
        } else {
            e.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = e.getPlayer();
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        e.setJoinMessage(null);
        ingameState.addSpectator(player);
    }

}
