// Created by Eric B. 30.05.2020 00:29
package de.ericzones.ttt.mapvoting;

import de.ericzones.ttt.extra.ItemBuilder;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Voting {

    public static final int MIN_MAPS = 2;
    private Main plugin;
    private ArrayList<Map> maps;
    private Map[] votingMaps;
    private int[] votingInvOrder = new int[]{12, 14};
    private HashMap<String, Integer> playerVotes;
    private Inventory votingInv;
    private boolean started;
    private Map winnerMap;

    public Voting(Main plugin, ArrayList<Map> maps) {
        this.plugin = plugin;
        this.maps = maps;
        votingMaps = new Map[MIN_MAPS];
        playerVotes = new HashMap<>();
        started = false;

        chooseRandomMaps();
    }

    private void chooseRandomMaps() {
        for(int i = 0; i < votingMaps.length; i++) {
            Collections.shuffle(maps);
            votingMaps[i] = maps.remove(0);
        }
    }

    public void createVotingInv(Player player) {
        votingInv = Bukkit.createInventory(null, 9*3, Utils.voting_inv);
        for(int i = 0; i < votingMaps.length; i++) {
            Map currentMap = votingMaps[i];
            if(hasVoted(player)) {
                Map votedMap = getVotedMap(player);
                if(votedMap == currentMap) {
                    votingInv.setItem(votingInvOrder[i], new ItemBuilder(Material.PAPER).setDisplayName("§8» §a" + currentMap.getName())
                            .setAmount(currentMap.getVotes()).setEnchanted(true).build());
                } else {
                    votingInv.setItem(votingInvOrder[i], new ItemBuilder(Material.PAPER).setDisplayName("§8» §a" + currentMap.getName())
                            .setAmount(currentMap.getVotes()).build());
                }
            } else {
                votingInv.setItem(votingInvOrder[i], new ItemBuilder(Material.PAPER).setDisplayName("§8» §a" + currentMap.getName())
                        .setAmount(currentMap.getVotes()).build());
            }
        }
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta backgroundmeta = background.getItemMeta();
        backgroundmeta.setDisplayName(" ");
        background.setItemMeta(backgroundmeta);

        votingInv.setItem(0, background);
        votingInv.setItem(1, background);
        votingInv.setItem(2, background);
        votingInv.setItem(3, background);
        votingInv.setItem(4, background);
        votingInv.setItem(5, background);
        votingInv.setItem(6, background);
        votingInv.setItem(7, background);
        votingInv.setItem(8, background);

        votingInv.setItem(18, background);
        votingInv.setItem(19, background);
        votingInv.setItem(20, background);
        votingInv.setItem(21, background);
        votingInv.setItem(22, background);
        votingInv.setItem(23, background);
        votingInv.setItem(24, background);
        votingInv.setItem(25, background);
        votingInv.setItem(26, background);
    }

    public Map getFinalMap() {
        Map finalMap = votingMaps[0];
        if(winnerMap == null) {
            for (int i = 1; i < votingMaps.length; i++) {
                if (votingMaps[i].getVotes() >= finalMap.getVotes())
                    finalMap = votingMaps[i];
            }
        } else {
            finalMap = winnerMap;
        }
        return finalMap;
    }

    public void forceMap(Player player, int votedMap) {
        if(!started) {
            winnerMap = votingMaps[votedMap];
            player.sendMessage(Utils.prefix + "§7Die Map wird §a"+votingMaps[votedMap].getName()+ " §7sein");
        } else {
            player.sendMessage(Utils.prefix + "§cDie Mapvoting-Phase ist vorbei");
        }
    }

    public void voteMap(Player player, int votedMap) {
        if(!started) {
            if (!hasVoted(player)) {
                votingMaps[votedMap].addVote();
                playerVotes.put(player.getUniqueId().toString(), votedMap);
                player.closeInventory();
                player.sendMessage(Utils.prefix + "§7Du hast für die Map §a" + votingMaps[votedMap].getName() + " §7abgestimmt");
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0.7F);
            } else {
                player.closeInventory();
                player.sendMessage(Utils.prefix + "§cDu hast bereits für eine Map abgestimmt");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            }
        } else {
            player.closeInventory();
            player.sendMessage(Utils.prefix + "§cDie Mapvoting-Phase ist vorbei");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
        }
    }

    public Map[] getVotingMaps() {
        return votingMaps;
    }

    public boolean hasVoted(Player player) {
        if(playerVotes.containsKey(player.getUniqueId().toString())) {
            return true;
        }
        return false;
    }

    public Map getVotedMap(Player player) {
        int mapNumber = playerVotes.get(player.getUniqueId().toString());
        Map votedMap = votingMaps[mapNumber];
        return votedMap;
    }

    public HashMap<String, Integer> getPlayerVotes() {
        return playerVotes;
    }

    public void removeVote(Player player) {
        playerVotes.remove(player.getUniqueId().toString());
    }

    public Inventory getVotingInv() {
        return votingInv;
    }

    public int[] getVotingInvOrder() {
        return votingInvOrder;
    }

    public void setStarted(boolean start) {
        started = start;
    }
}
