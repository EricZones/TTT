// Created by Eric B. 28.05.2020 13:36
package de.ericzones.ttt.listener;

import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.cloudperms.CloudPermissionsManagement;
import de.ericzones.ttt.countdowns.LobbyCountdown;
import de.ericzones.ttt.extra.ItemBuilder;
import de.ericzones.ttt.extra.LocationConfig;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import de.ericzones.ttt.mapvoting.Voting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerLobbyJoinListener implements Listener {

    private Main plugin;
    private ItemStack voteItem;

    public PlayerLobbyJoinListener(Main plugin) {
        this.plugin = plugin;
        voteItem = new ItemBuilder(Material.PAPER).setDisplayName(Utils.voting_item).build();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        Player player = e.getPlayer();
        plugin.addPlayer(player);

        IPermissionUser user = CloudPermissionsManagement.getInstance().getUser(player.getUniqueId());
        String color = CloudPermissionsManagement.getInstance().getHighestPermissionGroup(user).getColor();
        String name = color + player.getName();

        e.setJoinMessage("§a» " + name + " §7hat das Spiel betreten §8[§7" + plugin.getPlayers().size() + "/" + LobbyState.MAX_PLAYERS + "§8]");

        player.getInventory().clear();
        player.getInventory().setChestplate(null);
        player.getInventory().setHelmet(null);
        player.setAllowFlight(false);
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setItem(0, voteItem);

        LocationConfig locationConfig = new LocationConfig(plugin, "Lobby", "Spawn", 1);
        if(locationConfig.hasLocation()) {
            player.teleport(locationConfig.loadLocation());
        } else {
            Bukkit.getConsoleSender().sendMessage(Utils.prefix + "§cSpawn für Lobby wurde nicht gesetzt");
        }

        LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
        LobbyCountdown countdown = lobbyState.getCountdown();
        if(plugin.getPlayers().size() >= LobbyState.MIN_PLAYERS) {
            if(!countdown.isRunning()) {
                countdown.stopIdle();
                countdown.start();
            }
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
        e.setMaxPlayers(LobbyState.MAX_PLAYERS);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        Player player = e.getPlayer();
        plugin.removePlayer(player);

        IPermissionUser user = CloudPermissionsManagement.getInstance().getUser(player.getUniqueId());
        String color = CloudPermissionsManagement.getInstance().getHighestPermissionGroup(user).getColor();
        String name = color + player.getName();

        e.setQuitMessage("§c« " + name + " §7hat das Spiel verlassen §8[§7" + plugin.getPlayers().size() + "/" + LobbyState.MAX_PLAYERS + "§8]");

        LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
        LobbyCountdown countdown = lobbyState.getCountdown();
        if(plugin.getPlayers().size() < LobbyState.MIN_PLAYERS) {
            if(countdown.isRunning()) {
                countdown.stop();
                countdown.startIdle();
            }
        }

        Voting voting = plugin.getVoting();
        if(voting.hasVoted(player)) {
            Map votedMap = voting.getVotedMap(player);
            votedMap.removeVote();
            voting.removeVote(player);
        }

    }

}
