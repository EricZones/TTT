// Created by Eric B. 04.06.2020 13:38
package de.ericzones.ttt.listener;

import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.roles.HealingPoint;
import de.ericzones.ttt.roles.Role;
import de.ericzones.ttt.roles.RoleInventories;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ShopItemListener implements Listener {

    private Main plugin;

    public ShopItemListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if(e.getEntity().getType() != EntityType.ARROW) return;
        if(!(e.getEntity().getShooter() instanceof Player)) return;
        Arrow arrow = (Arrow) e.getEntity();
        Player player = (Player) e.getEntity().getShooter();
        if(plugin.getRoleManager().getPlayerRole(player) != Role.TRAITOR) return;
        if(RoleInventories.removeItem(player, Material.MONSTER_EGG)) {
            player.getWorld().spawnEntity(arrow.getLocation(), EntityType.CREEPER);
            arrow.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = e.getPlayer();
        if(plugin.getRoleManager().getPlayerRole(player) != Role.DETECTIVE) return;
        if(e.getBlock().getType() == Material.BEACON) {
            e.setCancelled(false);
            e.setBuild(true);
            new HealingPoint(plugin, e.getBlock().getLocation());
        }
    }

}
