// Created by Eric B. 04.06.2020 11:57
package de.ericzones.ttt.roles;

import javafx.print.PageLayout;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PointManager {

    private HashMap<String, Integer> playerPoints;

    public PointManager() {
        playerPoints = new HashMap<>();
    }

    public void addPoints(Player player, int points) {
        if(playerPoints.containsKey(player.getName())) {
            int currentPoints = playerPoints.get(player.getName());
            playerPoints.put(player.getName(), currentPoints + points);
        } else {
            playerPoints.put(player.getName(), points);
        }
    }

    public void setPoints(Player player, int points) {
        playerPoints.put(player.getName(), points);
    }

    public boolean removePoints(Player player, int points) {
        if(!playerPoints.containsKey(player.getName())) return false;
        int currentPoints = playerPoints.get(player.getName());
        if(currentPoints >= points) {
            playerPoints.put(player.getName(), currentPoints - points);
            if(playerPoints.get(player.getName()) < 0) setPoints(player, 0);
            return true;
        }
        return false;
    }

    public int getPlayerPoints(Player player) {
        if(!playerPoints.containsKey(player.getName())) return 0;
        return playerPoints.get(player.getName());
    }
}
