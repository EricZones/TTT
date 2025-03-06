// Created by Eric B. 02.06.2020 15:25
package de.ericzones.ttt.listener;

import de.ericzones.ttt.extra.ItemBuilder;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ChestListener implements Listener {

    private Main plugin;
    private ItemStack woodenSword, stoneSword, ironSword, bow, arrows;

    public ChestListener(Main plugin) {
        this.plugin = plugin;
        woodenSword = new ItemBuilder(Material.WOOD_SWORD).setDisplayName("§6Holzschwert").setUnbreakable(true).build();
        stoneSword = new ItemBuilder(Material.STONE_SWORD).setDisplayName("§6Steinschwert").setUnbreakable(true).build();
        ironSword = new ItemBuilder(Material.IRON_SWORD).setDisplayName("§eEisenschwert").setUnbreakable(true).build();
        bow = new ItemBuilder(Material.BOW).setDisplayName("§6Bogen").setUnbreakable(true).build();
        arrows = new ItemBuilder(Material.ARROW).setDisplayName("§6Pfeil").setAmount(20).build();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock().getType() != Material.CHEST) return;
        e.setCancelled(true);
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = e.getPlayer();
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.getSpectators().contains(player)) return;

        Random random = new Random();
        int number = random.nextInt(10)+1;

        switch (number) {
            case 1: case 2: case 3: case 4: case 5: case 6:
                if(!player.getInventory().contains(woodenSword)) {
                    useChest(woodenSword, e.getClickedBlock(), player);
                } else {
                    int number2 = random.nextInt(10)+1;
                    switch (number2) {
                        case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                            if(!player.getInventory().contains(bow))
                                useChest(bow, e.getClickedBlock(), player);
                            else if(!player.getInventory().contains(stoneSword))
                                useChest(stoneSword, e.getClickedBlock(), player);
                            break;
                        case 8: case 9: case 10:
                            if(!player.getInventory().contains(stoneSword))
                                useChest(stoneSword, e.getClickedBlock(), player);
                            else if(!player.getInventory().contains(bow))
                                useChest(bow, e.getClickedBlock(), player);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case 7: case 8: case 9:
                if(!player.getInventory().contains(bow)) {
                    useChest(bow, e.getClickedBlock(), player);
                } else {
                    int number2 = random.nextInt(10)+1;
                    switch (number2) {
                        case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8:
                            if(!player.getInventory().contains(woodenSword))
                                useChest(woodenSword, e.getClickedBlock(), player);
                            else if(!player.getInventory().contains(stoneSword))
                                useChest(stoneSword, e.getClickedBlock(), player);
                            break;
                        case 9: case 10:
                            if(!player.getInventory().contains(stoneSword))
                                useChest(stoneSword, e.getClickedBlock(), player);
                            else if(!player.getInventory().contains(woodenSword))
                                useChest(woodenSword, e.getClickedBlock(), player);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case 10:
                if(!player.getInventory().contains(stoneSword)) {
                    useChest(stoneSword, e.getClickedBlock(), player);
                } else {
                    int number2 = random.nextInt(10)+1;
                    switch (number2) {
                        case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                            if(!player.getInventory().contains(woodenSword))
                                useChest(woodenSword, e.getClickedBlock(), player);
                            else if(!player.getInventory().contains(bow))
                                useChest(bow, e.getClickedBlock(), player);
                            break;
                        case 8: case 9: case 10:
                            if(!player.getInventory().contains(bow))
                                useChest(bow, e.getClickedBlock(), player);
                            else if(!player.getInventory().contains(woodenSword))
                                useChest(woodenSword, e.getClickedBlock(), player);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
        player.updateInventory();
    }

    @EventHandler
    public void onInteract2(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock().getType() != Material.ENDER_CHEST) return;
        e.setCancelled(true);
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = e.getPlayer();
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.getSpectators().contains(player)) return;
        if(ingameState.isProtected()) return;

        if(!player.getInventory().contains(ironSword))
            useChest(ironSword, e.getClickedBlock(), player);
    }

    private void useChest(ItemStack itemStack, Block block, Player player) {
        if(itemStack == bow) {
            player.getInventory().addItem(itemStack);
            player.getInventory().addItem(arrows);
        } else {
            player.getInventory().addItem(itemStack);
        }
        block.setType(Material.AIR);
        if(itemStack == ironSword) {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
            }
        } else {
            for (Player current : Bukkit.getOnlinePlayers()) {
                current.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
            }
        }
    }

}
