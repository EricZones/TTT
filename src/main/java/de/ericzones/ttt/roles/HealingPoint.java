// Created by Eric B. 04.06.2020 14:34
package de.ericzones.ttt.roles;

import de.ericzones.ttt.extra.MessageAPI;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class HealingPoint {

    private static final int HEALING_DELAY = 5, HEALING_RADIUS = 5;
    private static final double HEALING_POWER = 4;

    private Main plugin;
    private Location location;
    private int taskID, durability;
    private Entity dummyEntity;

    public HealingPoint(Main plugin, Location location) {
        durability = 5;
        this.plugin = plugin;
        this.location = location;
        createPoint();
    }

    private void createPoint() {
        Bukkit.broadcastMessage(Utils.prefix + "§7Ein §bHeilungspunkt §7wurde aufgestellt");
        dummyEntity = location.getWorld().spawnEntity(location, EntityType.ARROW);
        location.getWorld().spawnEntity(location, EntityType.FIREWORK);

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                durability--;
                dummyEntity.remove();
                dummyEntity = location.getWorld().spawnEntity(location, EntityType.ARROW);

                for(Entity current : dummyEntity.getNearbyEntities(HEALING_RADIUS, HEALING_RADIUS, HEALING_RADIUS)) {
                    if(current instanceof Player) {
                        Player player = (Player) current;
                        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
                        if(!ingameState.getSpectators().contains(player)) {
                            if(player.getHealth() <= (20 - HEALING_POWER)) {
                                player.setHealth(player.getHealth() + HEALING_POWER);
                                //player.sendMessage(Utils.prefix + "§7Du wurdest durch einen §bHeilungspunkt §7geheilt");
                                MessageAPI.sendTitle(player, 5, 25, 5, " ", "§8• §7Durch §bHeilungspunkt §7geheilt §8•");
                            } else {
                                player.setHealth(20);
                                //player.sendMessage(Utils.prefix + "§7Du wurdest durch einen §bHeilungspunkt §7geheilt");
                                MessageAPI.sendTitle(player, 5, 25, 5, " ", "§8• §7Durch §bHeilungspunkt §7geheilt §8•");
                            }
                        }
                    }
                }
                if(durability <= 0) {
                    destroyPoint();
                } else {
                    dummyEntity.remove();
                    for(Player all : Bukkit.getOnlinePlayers())
                        all.playSound(location, Sound.NOTE_PLING, 4, 1.5F);
                }


            }
        }, 0, 20 * HEALING_DELAY);
    }

    private void destroyPoint() {
        dummyEntity.remove();
        Bukkit.getScheduler().cancelTask(taskID);
        location.getBlock().setType(Material.AIR);
        for(Player all : Bukkit.getOnlinePlayers())
            all.playSound(location, Sound.ANVIL_LAND, 2, 1.1F);
    }

}
