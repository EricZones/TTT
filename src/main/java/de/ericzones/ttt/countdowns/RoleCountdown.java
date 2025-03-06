// Created by Eric B. 31.05.2020 15:00
package de.ericzones.ttt.countdowns;

import de.ericzones.ttt.extra.MessageAPI;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class RoleCountdown extends Countdown {

    private Main plugin;
    private int seconds = 30;
    private boolean isRunning;

    public RoleCountdown(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        isRunning = true;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                double currentScore = seconds, totalScore = 30;
                float percent = (float) ((currentScore*100)/totalScore);
                percent = (float) (percent*0.01);

                switch (seconds) {
                    case 15: case 10: case 5: case 4: case 3: case 2:
                        MessageAPI.sendActionBar("§8• §7Die Schutzzeit endet in §a"+seconds+" §7Sekunden §8•");
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            all.setExp(percent);
                        }
                        break;
                    case 1:
                        MessageAPI.sendActionBar("§8• §7Die Schutzzeit endet in §aeiner §7Sekunde §8•");
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            all.setExp(percent);
                        }
                        break;
                    case 0:
                        stop();
                        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
                        ingameState.setProtection(false);

                        plugin.getRoleManager().giveRoles(plugin.getPlayers().size());
                        ingameState.startActionbar();
                        for(Player current : plugin.getPlayers()) {
                            Role playerRole = plugin.getRoleManager().getPlayerRole(current);
                            current.sendMessage(Utils.prefix + "§7Rolle§8: "+playerRole.getName());
                            current.sendMessage(Utils.prefix + "§7Ziel§8: "+playerRole.getMission());
                            MessageAPI.sendTitle(current, 5, 40, 5, "§8• "+playerRole.getName()+" §8•", null);
                            current.playSound(current.getLocation(), Sound.LEVEL_UP, 1, 1.0F);
                            current.setExp(0);
                            plugin.getScoreBoard().setNewScoreBoard(current);
                            switch (playerRole) {
                                case TRAITOR:
                                    current.sendMessage(Utils.prefix + "§7Du kannst mit §4@v §7im §4Verräter-Chat §7schreiben");
                                    plugin.getRoleInventories().getPointManager().setPoints(current, 2);
                                    break;
                                case DETECTIVE:
                                    plugin.getRoleInventories().getPointManager().setPoints(current, 2);
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    default:
                        MessageAPI.sendActionBar("§8• §7Die Schutzzeit endet in §a"+seconds+" §7Sekunden §8•");
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
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
