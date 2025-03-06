// Created by Eric B. 06.06.2020 11:40
package de.ericzones.ttt.extra;

import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SpectatorInventory implements Listener {

    public static final String
            SPECTATOR_INV = Utils.spectator_inv, SPECTATOR_ITEM = Utils.spectator_item;

    private Main plugin;
    private ItemStack spectator_item;
    private Inventory spectator_inv;

    public SpectatorInventory(Main plugin) {
        this.plugin = plugin;
        spectator_item = new ItemBuilder(Material.COMPASS).setDisplayName(SPECTATOR_ITEM).build();
        spectator_inv = Bukkit.createInventory(null, 9*4, SPECTATOR_INV);
    }

    public void createInventory(Player player) {
        for(int i = 0; i < plugin.getPlayers().size(); i++) {
            if(plugin.getPlayers().get(i) != null) {
                Player target = plugin.getPlayers().get(i);
                Role playerRole = plugin.getRoleManager().getPlayerRole(player);
                Role targetRole = plugin.getRoleManager().getPlayerRole(target);
                if(playerRole == Role.TRAITOR) {
                    spectator_inv.setItem(i, new ItemBuilder(Material.SKULL_ITEM, 3, target.getName()).setDisplayName(targetRole.getChatColor() + target.getName()).build());
                } else {
                    if(targetRole == Role.TRAITOR) {
                        spectator_inv.setItem(i, new ItemBuilder(Material.SKULL_ITEM, 3, target.getName()).setDisplayName(Role.INNOCENT.getChatColor() + target.getName()).build());
                    } else {
                        spectator_inv.setItem(i, new ItemBuilder(Material.SKULL_ITEM, 3, target.getName()).setDisplayName(targetRole.getChatColor() + target.getName()).build());
                    }
                }
            }
        }
        player.openInventory(spectator_inv);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player player = e.getPlayer();
        if(!(e.hasItem() && e.getItem().hasItemMeta())) return;
        if(plugin.getPlayers().contains(player)) return;
        switch(e.getItem().getType()) {
            case COMPASS:
                createInventory(player);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(e.getClickedInventory() == null) return;
        Player player = (Player) e.getWhoClicked();
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        if(e.getClickedInventory().getTitle().equals(SPECTATOR_INV)) {
            e.setCancelled(true);
            if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                SkullMeta skullMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
                String name = skullMeta.getOwner();
                Player target = Bukkit.getPlayer(name);
                if(target != null && plugin.getPlayers().contains(target)) {
                    player.closeInventory();
                    player.teleport(target);
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                } else {
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    player.sendMessage(Utils.prefix + "§cDieser Spieler ist nicht mehr im Spiel");
                }
            }
        }
     }

    public ItemStack getSpectatorItem() {
        return spectator_item;
    }
}
