// Created by Eric B. 28.05.2020 14:53
package de.ericzones.ttt.countdowns;

import de.ericzones.ttt.extra.MessageAPI;
import de.ericzones.ttt.extra.ScoreBoard;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.GameState;
import de.ericzones.ttt.game.GameStateManager;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import de.ericzones.ttt.mapvoting.Voting;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class LobbyCountdown extends Countdown {

    private static final int COUNTDOWN_TIME = 60, IDLE_TIME = 2;

    private GameStateManager gameStateManager;

    private Main plugin;
    private int seconds;
    private boolean isRunning;
    private int idleID;
    private boolean isIdling;

    private Map finalMap;

    public LobbyCountdown(Main plugin, GameStateManager gameStateManager) {
        this.plugin = plugin;
        this.gameStateManager = gameStateManager;
        seconds = COUNTDOWN_TIME;
    }

    @Override
    public void start() {
        isRunning = true;
        Voting voting = gameStateManager.getPlugin().getVoting();
        voting.setStarted(false);
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {
            @Override
            public void run() {

                double currentScore = seconds, totalScore = COUNTDOWN_TIME;
                float percent = (float) ((currentScore*100)/totalScore);
                percent = (float) (percent*0.01);

                switch(seconds) {
                    case 60: case 30: case 15: case 10: case 4: case 3: case 2:
                        MessageAPI.sendActionBar("§8• §7Das Spiel startet in §a" + seconds + " §7Sekunden §8•");
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            all.setExp(percent);
                        }
                        break;

                    case 5:

                        Voting voting = gameStateManager.getPlugin().getVoting();
                        voting.setStarted(true);
                        if(voting != null) {
                            finalMap = voting.getFinalMap();
                        } else {
                            ArrayList<Map> maps = gameStateManager.getPlugin().getMaps();
                            Collections.shuffle(maps);
                            finalMap = maps.get(0);
                        }

                        Bukkit.broadcastMessage(Utils.prefix + "§7Map§8: §a" + finalMap.getName());
                        Bukkit.broadcastMessage(Utils.prefix + "§7Erbauer§8: §a"+ finalMap.getBuilder());

                        MessageAPI.sendActionBar("§8• §7Das Spiel startet in §a" + seconds + " §7Sekunden §8•");
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            plugin.getScoreBoard().updateScoreBoard(all);
                            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            all.setExp(percent);
                            MessageAPI.sendTitle(all, 5, 40, 5, "§8• §c§lTTT §8•", "§8• §a"+finalMap.getName()+" §8•");
                        }
                        break;
                    case 1:
                        MessageAPI.sendActionBar("§8• §7Das Spiel startet in §aeiner §7Sekunde §8•");
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            all.setExp(percent);
                        }
                        break;

                    case 0:
                        Bukkit.broadcastMessage(Utils.prefix + "§7Nach der Schutzzeit wird dir automatisch eine Rolle zugewiesen");
                        Bukkit.broadcastMessage(Utils.prefix + "§cGrundloses Töten ist verboten und wird bestraft");
                        gameStateManager.setGameState(GameState.INGAME_STATE);
                        break;

                    default:
                        MessageAPI.sendActionBar("§8• §7Das Spiel startet in §a" + seconds + " §7Sekunden §8•");
                        for(Player all : Bukkit.getOnlinePlayers())
                            all.setExp(percent);
                        break;
                }
                seconds--;

            }
        }, 0, 20);

    }

    @Override
    public void stop() {
        if(isRunning) {
            Bukkit.getScheduler().cancelTask(taskID);
            isRunning = false;
            seconds = COUNTDOWN_TIME;
        }
    }

    public void startIdle() {
        isIdling = true;
        idleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {
            @Override
            public void run() {

                MessageAPI.sendActionBar("§8• §cWarte auf weitere Spieler §8•");
                //Bukkit.broadcastMessage(Utils.prefix + "§cNicht genügend Spieler zum Starten des Countdowns");

            }
        }, 0, 20 * IDLE_TIME);
    }

    public void stopIdle() {
        if(isIdling) {
            Bukkit.getScheduler().cancelTask(idleID);
            isIdling = false;
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Map getFinalMap() {
        return finalMap;
    }
}
