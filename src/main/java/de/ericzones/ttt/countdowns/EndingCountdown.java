// Created by Eric B. 04.06.2020 22:48
package de.ericzones.ttt.countdowns;

import de.ericzones.ttt.extra.MessageAPI;
import de.ericzones.ttt.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EndingCountdown extends Countdown {

    private static final int ENDING_SECONDS = 20;

    private Main plugin;
    private int seconds;

    public EndingCountdown(Main plugin) {
        this.plugin = plugin;
        seconds = ENDING_SECONDS;
    }

    @Override
    public void start() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                double currentScore = seconds, totalScore = ENDING_SECONDS;
                float percent = (float) ((currentScore*100)/totalScore);
                percent = (float) (percent*0.01);

                switch (seconds) {
                    case 15: case 10: case 5: case 4: case 3: case 2:
                        MessageAPI.sendActionBar("§8• §cServer startet in §e" + seconds + " §cSekunden neu §8•");
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            all.setExp(percent);
                        }
                        break;
                    case 1:
                        MessageAPI.sendActionBar("§8• §cServer startet in §eeiner §cSekunde neu §8•");
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            all.setExp(percent);
                        }
                        break;
                    case 0:
                        plugin.getGameStateManager().getCurrentGameState().stop();
                        stop();
                        break;
                    default:
                        MessageAPI.sendActionBar("§8• §cServer startet in §e" + seconds + " §cSekunden neu §8•");
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
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
