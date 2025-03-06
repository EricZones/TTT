// Created by Eric B. 28.05.2020 13:16
package de.ericzones.ttt.game;

import de.ericzones.ttt.countdowns.EndingCountdown;
import de.ericzones.ttt.extra.LocationConfig;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.main.Main;
import net.minecraft.server.v1_8_R3.BlockCactus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EndingState extends GameState {

    private Main plugin;
    private EndingCountdown endingCountdown;
    private LocationConfig locationConfig;

    public EndingState(Main plugin) {
        this.plugin = plugin;
        endingCountdown = new EndingCountdown(plugin);
        locationConfig = new LocationConfig(plugin, "Lobby", "Endinglobby", 1);
    }

    @Override
    public void start() {
        for(Player current : Bukkit.getOnlinePlayers()) {
            for(Player all : Bukkit.getOnlinePlayers()) {
                all.showPlayer(current);
                current.showPlayer(all);
            }

            if(locationConfig.hasLocation()) {
                current.teleport(locationConfig.loadLocation());
            } else {
                Bukkit.getConsoleSender().sendMessage(Utils.prefix + "§cSpawn für Endinglobby wurde nicht gesetzt");
            }

            plugin.getScoreBoard().setNewScoreBoard(current);
            current.setHealth(20);
            current.setFoodLevel(20);
            current.getInventory().clear();
            current.getInventory().setChestplate(null);
            current.getInventory().setHelmet(null);
            current.setAllowFlight(false);
            current.setFlying(false);
            current.setExp(0);
            current.setGameMode(GameMode.ADVENTURE);
            current.playSound(current.getLocation(), Sound.LEVEL_UP, 1, 1.0F);
            current.playSound(current.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1.2F);
        }
        endingCountdown.start();
    }

    @Override
    public void stop() {
        for(Player all : Bukkit.getOnlinePlayers())
            all.kickPlayer(null);
        Bukkit.getServer().shutdown();
    }

    public LocationConfig getLocationConfig() {
        return locationConfig;
    }
}
