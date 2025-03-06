// Created by Eric B. 28.05.2020 13:16
package de.ericzones.ttt.game;

import de.ericzones.ttt.countdowns.RoleCountdown;
import de.ericzones.ttt.extra.MessageAPI;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import de.ericzones.ttt.roles.Role;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Collections;

public class IngameState extends GameState {

    private Main plugin;
    private Map map;
    private ArrayList<Player> players, spectators;
    private RoleCountdown roleCountdown;
    private boolean protection;
    private int actionbarID;

    private Role winnerRole;

    public IngameState(Main plugin) {
        this.plugin = plugin;
        roleCountdown = new RoleCountdown(plugin);
        spectators = new ArrayList<>();
    }

    @Override
    public void start() {
        protection = true;

        Collections.shuffle(plugin.getPlayers());
        players = plugin.getPlayers();

        map = plugin.getVoting().getFinalMap();
        map.load();
        for(int i = 0; i < players.size(); i++) {
            players.get(i).teleport(map.getSpawnLocations()[i]);
        }
        Bukkit.getWorld(map.getName()).setDifficulty(Difficulty.EASY);
        Bukkit.getWorld(map.getName()).setGameRuleValue("doFireTick", "false");
        for(Player current : players) {
            plugin.getScoreBoard().setNewScoreBoard(current);
            current.setHealth(20);
            current.setFoodLevel(20);
            current.getInventory().clear();
            current.getInventory().setChestplate(null);
            current.getInventory().setHelmet(null);
            current.setAllowFlight(false);
            current.setExp(0);
            current.setGameMode(GameMode.SURVIVAL);
            current.playSound(current.getLocation(), Sound.LEVEL_UP, 1, 1.0F);
        }
        plugin.getRoleManager().giveArmor();
        roleCountdown.start();

    }

    public void startActionbar() {
        actionbarID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(Player current : Bukkit.getOnlinePlayers()) {
                    Role playerRole = plugin.getRoleManager().getPlayerRole(current);
                    if(getSpectators().contains(current)) {
                        MessageAPI.ActionBar(current, "§8• §7Spectator §8•");
                    } else if(playerRole == Role.DETECTIVE) {
                        MessageAPI.ActionBar(current, "§8• §9Detektiv §8•");
                    } else if(playerRole == Role.TRAITOR) {
                        MessageAPI.ActionBar(current, "§8• §4Verräter §8•");
                    } else if(playerRole == Role.INNOCENT) {
                        MessageAPI.ActionBar(current, "§8• §aUnschuldiger §8•");
                    }
                }
            }
        }, 0, 20);
    }

    public void stopActionbar() {
        Bukkit.getScheduler().cancelTask(actionbarID);
    }

    public void checkGame() {
        if(plugin.getRoleManager().getTraitorPlayers().size() <= 0) {
            winnerRole = Role.INNOCENT;
            Utils.winnerRole = Role.INNOCENT;
            if(roleCountdown.isRunning())
                roleCountdown.stop();
            plugin.getGameStateManager().setGameState(ENDING_STATE);
        } else if(plugin.getPlayers().size() == plugin.getRoleManager().getTraitorPlayers().size()) {
            winnerRole = Role.TRAITOR;
            Utils.winnerRole = Role.TRAITOR;
            if(roleCountdown.isRunning())
                roleCountdown.stop();
            plugin.getGameStateManager().setGameState(ENDING_STATE);
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setExp(0);
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(getChestplate(Color.SILVER));
        player.teleport(map.getSpectatorLocation());
        for(Player current : Bukkit.getOnlinePlayers()) {
            if(!spectators.contains(current))
                current.hidePlayer(player);
            player.showPlayer(current);
        }
        player.getInventory().setItem(0, plugin.getSpectatorInventory().getSpectatorItem());
    }

    @Override
    public void stop() {
        switch (winnerRole) {
            case TRAITOR:
                for(Player current : Bukkit.getOnlinePlayers())
                     MessageAPI.sendTitle(current, 5, 40, 5, "§8• §7Die §4Verräter §8•", "§8• §7haben das Spiel gewonnen §8•");
                Bukkit.broadcastMessage(Utils.prefix + "§7Die §4Verräter §7haben das Spiel gewonnen");
                break;
            case INNOCENT:
                for(Player current : Bukkit.getOnlinePlayers())
                    MessageAPI.sendTitle(current, 5, 40, 5, "§8• §7Die §aUnschuldigen §8•", "§8• §7haben das Spiel gewonnen §8•");
                Bukkit.broadcastMessage(Utils.prefix + "§7Die §aUnschuldigen §7haben das Spiel gewonnen");
                break;
            default:
                break;
        }
        stopActionbar();
    }

    public void setProtection(boolean protection) {
        this.protection = protection;
    }

    public boolean isProtected() {
        return protection;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    private ItemStack getChestplate(Color color) {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return item;
    }

    public Map getMap() {
        return map;
    }
}
