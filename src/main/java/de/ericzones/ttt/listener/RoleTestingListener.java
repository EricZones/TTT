// Created by Eric B. 02.06.2020 22:20
package de.ericzones.ttt.listener;

import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.roles.RoleTesting;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RoleTestingListener implements Listener {

    private Main plugin;

    public RoleTestingListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if(block.getType() != Material.WOOD_BUTTON && block.getType() != Material.STONE_BUTTON) return;
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isProtected()) return;
        RoleTesting roleTesting = ingameState.getMap().getRoleTesting();
        if(roleTesting.getButton().getLocation().equals(block.getLocation()))
            roleTesting.testPlayer(e.getPlayer());
    }

}
