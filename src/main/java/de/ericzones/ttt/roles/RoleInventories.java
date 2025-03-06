// Created by Eric B. 03.06.2020 21:46
package de.ericzones.ttt.roles;

import de.ericzones.ttt.extra.ItemBuilder;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.main.Main;
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
import org.bukkit.inventory.meta.ItemMeta;

public class RoleInventories implements Listener {

    public static final String
            TRAITOR_INV = Utils.traitor_inv, TRAITOR_ITEM = Utils.traitor_item, TRAITOR_CREEPER = Utils.traitor_creeper_item, TRAITOR_FAKER = Utils.traitor_faker_item,
            DETECTIVE_INV = Utils.detective_inv, DETECTIVE_ITEM = Utils.detective_item, DETECTIVE_HEALER = Utils.detective_healer_item;

    private Main plugin;
    private ItemStack traitorItem, detectiveItem;
    private Inventory traitorShop, detectiveShop;
    private PointManager pointManager;

    public RoleInventories(Main plugin) {
        this.plugin = plugin;
        traitorItem = new ItemBuilder(Material.EMERALD).setDisplayName(TRAITOR_ITEM).build();
        detectiveItem = new ItemBuilder(Material.EMERALD).setDisplayName(DETECTIVE_ITEM).build();

        traitorShop = Bukkit.createInventory(null, 9*3, TRAITOR_INV);
        detectiveShop = Bukkit.createInventory(null, 9*3, DETECTIVE_INV);
        pointManager = new PointManager();
        createInventories();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(e.getClickedInventory() == null) return;
        Player player = (Player) e.getWhoClicked();
        if(e.getClickedInventory().getTitle().equals(Utils.traitor_inv)) {
            e.setCancelled(true);
            switch (e.getCurrentItem().getType()) {
                case MONSTER_EGG:
                    if(!pointManager.removePoints(player, 3)) {
                        player.sendMessage(Utils.prefix + "§cNicht ausreichend §4V-Punkte §cvorhanden");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        player.closeInventory();
                    } else {
                        plugin.getScoreBoard().updateScoreBoard(player);
                        player.getInventory().addItem(new ItemBuilder(Material.MONSTER_EGG, 50).setDisplayName(TRAITOR_CREEPER).setAmount(3).build());
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                        player.closeInventory();
                    }
                    break;
                case PAPER:
                    if(!pointManager.removePoints(player, 5)) {
                        player.sendMessage(Utils.prefix + "§cNicht ausreichend §4V-Punkte §cvorhanden");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        player.closeInventory();
                    } else {
                        plugin.getScoreBoard().updateScoreBoard(player);
                        player.getInventory().addItem(new ItemBuilder(Material.PAPER).setDisplayName(TRAITOR_FAKER).build());
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                        player.closeInventory();
                    }
                    break;
                default:
                    break;
            }

        } else if(e.getClickedInventory().getTitle().equals(Utils.detective_inv)) {
            e.setCancelled(true);
            switch (e.getCurrentItem().getType()) {
                case BEACON:
                    if(!pointManager.removePoints(player, 4)) {
                        player.sendMessage(Utils.prefix + "§cNicht ausreichend §9D-Punkte §cvorhanden");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        player.closeInventory();
                    } else {
                        plugin.getScoreBoard().updateScoreBoard(player);
                        player.getInventory().addItem(new ItemBuilder(Material.BEACON).setDisplayName(DETECTIVE_HEALER).build());
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                        player.closeInventory();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player player = e.getPlayer();
        if(!(e.hasItem() && e.getItem().hasItemMeta())) return;
        switch (e.getItem().getItemMeta().getDisplayName()) {
            case Utils.traitor_item:
                player.openInventory(traitorShop);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                break;
            case Utils.detective_item:
                player.openInventory(detectiveShop);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                break;
            default:
                break;
        }
    }

    private void createInventories(){
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta backgroundmeta = background.getItemMeta();
        backgroundmeta.setDisplayName(" ");
        background.setItemMeta(backgroundmeta);
        
        traitorShop.setItem(0, background);
        traitorShop.setItem(1, background);
        traitorShop.setItem(2, background);
        traitorShop.setItem(3, background);
        traitorShop.setItem(4, background);
        traitorShop.setItem(5, background);
        traitorShop.setItem(6, background);
        traitorShop.setItem(7, background);
        traitorShop.setItem(8, background);
        traitorShop.setItem(18, background);
        traitorShop.setItem(19, background);
        traitorShop.setItem(20, background);
        traitorShop.setItem(21, background);
        traitorShop.setItem(22, background);
        traitorShop.setItem(23, background);
        traitorShop.setItem(24, background);
        traitorShop.setItem(25, background);
        traitorShop.setItem(26, background);
        detectiveShop.setItem(0, background);
        detectiveShop.setItem(1, background);
        detectiveShop.setItem(2, background);
        detectiveShop.setItem(3, background);
        detectiveShop.setItem(4, background);
        detectiveShop.setItem(5, background);
        detectiveShop.setItem(6, background);
        detectiveShop.setItem(7, background);
        detectiveShop.setItem(8, background);
        detectiveShop.setItem(18, background);
        detectiveShop.setItem(19, background);
        detectiveShop.setItem(20, background);
        detectiveShop.setItem(21, background);
        detectiveShop.setItem(22, background);
        detectiveShop.setItem(23, background);
        detectiveShop.setItem(24, background);
        detectiveShop.setItem(25, background);
        detectiveShop.setItem(26, background);

        traitorShop.setItem(10, new ItemBuilder(Material.MONSTER_EGG, 50).setDisplayName(TRAITOR_CREEPER).setLore("§8§m---------------------", "§7Preis§8: §a§l3 §4V-Punkte", "§8§m---------------------").build());
        traitorShop.setItem(11, new ItemBuilder(Material.PAPER).setDisplayName(TRAITOR_FAKER).setLore("§8§m-------------------", "§7Preis§8: §a§l5 §4V-Punkte", "§8§m-------------------").build());

        detectiveShop.setItem(10, new ItemBuilder(Material.BEACON).setDisplayName(DETECTIVE_HEALER).setLore("§8§m-------------------", "§7Preis§8: §a§l4 §9D-Punkte", "§8§m-------------------").build());
    }

    public static boolean removeItem(Player player, Material material) {
        for(int i = 0; i < player.getInventory().getSize(); i++) {
            if(player.getInventory().getContents()[i] != null) {
                if (player.getInventory().getContents()[i].getType() == material) {
                    if (player.getInventory().getContents()[i].getAmount() > 1) {
                        player.getInventory().getContents()[i].setAmount(player.getInventory().getContents()[i].getAmount() - 1);
                        player.updateInventory();
                    } else {
                        player.getInventory().clear(i);
                        player.updateInventory();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack getTraitorItem() {
        return traitorItem;
    }

    public Inventory getTraitorShop() {
        return traitorShop;
    }

    public Inventory getDetectiveShop() {
        return detectiveShop;
    }

    public ItemStack getDetectiveItem() {
        return detectiveItem;
    }

    public PointManager getPointManager() {
        return pointManager;
    }
}
