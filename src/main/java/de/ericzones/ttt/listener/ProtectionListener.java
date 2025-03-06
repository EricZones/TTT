// Created by Eric B. 01.06.2020 18:12
package de.ericzones.ttt.listener;

import de.ericzones.ttt.extra.LocationConfig;
import de.ericzones.ttt.extra.MessageAPI;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.EndingState;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ProtectionListener implements Listener {

    private Main plugin;
    private ArrayList<String> buildPlayers;

    public ProtectionListener(Main plugin) {
        this.plugin = plugin;
        buildPlayers = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerBlockPlace(BlockPlaceEvent e) {
        if(buildPlayers.contains(e.getPlayer().getName())) return;
        e.setBuild(false);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent e) {
        if(buildPlayers.contains(e.getPlayer().getName())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(buildPlayers.contains(e.getPlayer().getName())) return;
        if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            if(ingameState.getSpectators().contains(e.getPlayer())) {
                e.setCancelled(true);
            } else {
                e.setCancelled(false);
            }
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if(buildPlayers.contains(e.getPlayer().getName())) return;
        if(e.getRightClicked() instanceof ArmorStand || e.getRightClicked() instanceof ItemFrame)
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player)) return;
        if(buildPlayers.contains(e.getDamager().getName())) return;
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            e.setCancelled(true);
        } else {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            if(e.getEntity() instanceof ArmorStand || e.getEntity() instanceof ItemFrame || ingameState.getSpectators().contains((Player) e.getDamager())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if(buildPlayers.contains(e.getPlayer().getName())) return;
        if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState || plugin.getGameStateManager().getCurrentGameState() instanceof EndingState) {
            e.setCancelled(true);
            return;
        }
        Material material = e.getItemDrop().getItemStack().getType();
        if(material == Material.LEATHER_CHESTPLATE || material == Material.EMERALD || material == Material.COMPASS) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        if(buildPlayers.contains(player.getName())) return;
        if(e.getClickedInventory() == null) return;
        if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState || plugin.getGameStateManager().getCurrentGameState() instanceof EndingState) {
            e.setCancelled(true);
            return;
        }
        if(e.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE || e.getCurrentItem().getType() == Material.COMPASS)
            e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if(e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onRegainHealth(EntityRegainHealthEvent e) {
//        e.setCancelled(true);
//    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            e.setCancelled(true);
            return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isProtected())
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            e.setCancelled(true);
            return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isProtected()) {
            e.setCancelled(true);
            return;
        }
        if(!(e.getEntity() instanceof Player)) return;
        if(ingameState.getSpectators().contains((Player) e.getEntity())) {
            e.setCancelled(true);
            return;
        }
        if(!(e.getDamager() instanceof Player)) return;
        if(ingameState.getSpectators().contains((Player) e.getDamager())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            e.setCancelled(true);
            return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isProtected()) {
            e.setCancelled(true);
            return;
        }
        if(!(e.getEntity() instanceof Player)) return;
        if(ingameState.getSpectators().contains((Player) e.getEntity()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        player.spigot().respawn();
        if (plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            ingameState.addSpectator(player);
            plugin.getScoreBoard().setNewScoreBoard(player);
            MessageAPI.sendTitle(player, 5, 40, 5, "§8• §cGestorben §8•", null);
        } else if(plugin.getGameStateManager().getCurrentGameState() instanceof EndingState) {
            LocationConfig locationConfig = new LocationConfig(plugin, "Lobby", "Endinglobby", 1);
            if(locationConfig.hasLocation()) {
                player.teleport(locationConfig.loadLocation());
            } else {
                Bukkit.getConsoleSender().sendMessage(Utils.prefix + "§cSpawn für Endinglobby wurde nicht gesetzt");
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            e.setCancelled(true);
            return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.getSpectators().contains(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }
        if(e.getItem().getItemStack().getType() == Material.ARROW) {
            e.setCancelled(true);
            ItemStack item = e.getItem().getItemStack();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6Pfeil");
            item.setItemMeta(meta);
            e.getPlayer().getInventory().addItem(item);
            e.getItem().remove();
            for(Player current : Bukkit.getOnlinePlayers())
                current.playSound(e.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1, 1);
        }

    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if(!(e.getTarget() instanceof Player)) return;
        Player player = (Player) e.getTarget();
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.getSpectators().contains(player)) {
            e.setCancelled(true);
            e.setTarget(null);
        }
    }

    @EventHandler
    public void onEntityTarget2(EntityTargetEvent e) {
        if(!(e.getTarget() instanceof Player)) return;
        Player player = (Player) e.getTarget();
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.getSpectators().contains(player)) {
            e.setCancelled(true);
            e.setTarget(null);
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        if(buildPlayers.contains(e.getPlayer().getName())) return;
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
                e.setCancelled(true);
                return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.getSpectators().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        Player player = e.getPlayer();
        if(player.getLocation().getBlockY() <= 0) {
            if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                LocationConfig locationConfig = new LocationConfig(plugin, "Lobby", "Spawn", 1);
                if(locationConfig.hasLocation())
                    player.teleport(locationConfig.loadLocation());
            } else if(plugin.getGameStateManager().getCurrentGameState() instanceof EndingState) {
                EndingState endingState = (EndingState) plugin.getGameStateManager().getCurrentGameState();
                if(endingState.getLocationConfig().hasLocation())
                    player.teleport(endingState.getLocationConfig().loadLocation());
            }
        }
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDestroy(HangingBreakByEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onGrow(BlockGrowEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onForm(BlockFormEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }


    public ArrayList<String> getBuildPlayers() {
        return buildPlayers;
    }


}
