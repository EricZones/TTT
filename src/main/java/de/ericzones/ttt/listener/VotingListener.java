// Created by Eric B. 30.05.2020 18:57
package de.ericzones.ttt.listener;

import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Voting;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VotingListener implements Listener {

    private Voting voting;
    private Main plugin;

    public VotingListener(Main plugin) {
        this.plugin = plugin;
        voting = plugin.getVoting();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        Player player = e.getPlayer();
        if(e.hasItem() && e.getItem().hasItemMeta()) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = player.getItemInHand();
                if(item.getItemMeta().getDisplayName().equals(Utils.voting_item)) {
                    voting.createVotingInv(player);
                    player.openInventory(voting.getVotingInv());
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(e.getClickedInventory() == null) return;
        Player player = (Player) e.getWhoClicked();
        if(e.getInventory().getTitle().equals(Utils.voting_inv)) {
            e.setCancelled(true);
            for(int i = 0; i < voting.getVotingInvOrder().length; i++) {
                if(voting.getVotingInvOrder()[i] == e.getSlot()) {
                    voting.voteMap(player, i);
                    return;
                }
            }
        }
    }

}
