// Created by Eric B. 04.06.2020 23:52
package de.ericzones.ttt.listener;

import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.EndingState;
import de.ericzones.ttt.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEndingJoinListener implements Listener {

    private Main plugin;

    public PlayerEndingJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof EndingState)) return;
        Player player = e.getPlayer();
        e.setJoinMessage(null);
        EndingState endingState = (EndingState) plugin.getGameStateManager().getCurrentGameState();
        if(endingState.getLocationConfig().hasLocation()) {
            player.teleport(endingState.getLocationConfig().loadLocation());
        } else {
            Bukkit.getConsoleSender().sendMessage(Utils.prefix + "§cSpawn für Endinglobby wurde nicht gesetzt");
        }
        for(Player all : Bukkit.getOnlinePlayers()) {
            all.showPlayer(player);
            player.showPlayer(all);
        }

        player.getInventory().clear();
        player.getInventory().setChestplate(null);
        player.getInventory().setHelmet(null);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof EndingState)) return;
        e.setQuitMessage(null);
    }



}
