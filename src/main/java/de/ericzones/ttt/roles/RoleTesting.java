// Created by Eric B. 02.06.2020 16:53
package de.ericzones.ttt.roles;

import com.google.gson.internal.$Gson$Types;
import de.ericzones.ttt.extra.LocationConfig;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RoleTesting {

    private static final int TESTING_TIME = 5;

    private Main plugin;
    private Map map;

    private Block[] borderBlocks, lamps, floor;
    private Block button;
    private Location blockedLocation;
    private Location testerLocation;
    private boolean inUse;
    private World world;

    private int taskID;

    public RoleTesting(Main plugin, Map map) {
        this.plugin = plugin;
        this.map = map;

        borderBlocks = new Block[3];
        lamps = new Block[2];
        floor = new Block[9];
    }

    public void testPlayer(Player player) {
        Role role = plugin.getRoleManager().getPlayerRole(player);
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.getSpectators().contains(player)) return;
        if(role == Role.DETECTIVE) {
            player.sendMessage(Utils.prefix + "§cAls Detektiv kannst du den Tester nicht nutzen");
            return;
        }
        if(inUse) {
            player.sendMessage(Utils.prefix + "§cDer Tester ist gleich wieder bereit");
            return;
        }
        Bukkit.broadcastMessage(Utils.prefix + Role.INNOCENT.getChatColor()+player.getName()+" §7hat den Tester betreten");
        player.teleport(testerLocation);
        inUse = true;
        for(Block current : borderBlocks) {
            world.getBlockAt(current.getLocation()).setType(Material.REDSTONE_BLOCK);
        }

        for(Entity current : player.getNearbyEntities(4, 4, 4)) {
            if(current instanceof Player)
                ((Player)current).teleport(blockedLocation);
        }

        if(role == Role.TRAITOR) {
            if(RoleInventories.removeItem(player, Material.PAPER)) {
                player.sendMessage(Utils.prefix + "§7Durch das §cTester-Ticket §7bleibst du zu 75% unentdeckt");
                if(Math.random() <= 0.75D)
                    role = Role.INNOCENT;
            }
        }

        Role endRole = role;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int count = TESTING_TIME;
            @Override
            public void run() {
                switch (count) {
                    case 5: case 4: case 3: case 2: case 1:
                        for(Player current : Bukkit.getOnlinePlayers())
                            current.playSound(testerLocation, Sound.ORB_PICKUP, 5, 1.0F);

                        Location loc = new Location(testerLocation.getWorld(), testerLocation.getX(), testerLocation.getY()+1, testerLocation.getZ());
                        Location loc1 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
                        Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()+1);
                        Location loc3 = new Location(loc.getWorld(), loc.getX()-1, loc.getY(), loc.getZ());
                        Location loc4 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-1);
                        Location loc5 = new Location(loc.getWorld(), loc.getX()+1, loc.getY()-1, loc.getZ());
                        Location loc6 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()+1);
                        Location loc7 = new Location(loc.getWorld(), loc.getX()-1, loc.getY()-1, loc.getZ());
                        Location loc8 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()-1);
                        for(Player current : Bukkit.getOnlinePlayers()) {
                            current.spigot().playEffect(loc1, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                            current.spigot().playEffect(loc2, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                            current.spigot().playEffect(loc3, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                            current.spigot().playEffect(loc4, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                            current.spigot().playEffect(loc5, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                            current.spigot().playEffect(loc6, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                            current.spigot().playEffect(loc7, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                            current.spigot().playEffect(loc8, Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 10, 5, 12);
                        }
                        break;
                    case 0:
                        finishTesting(player, endRole);
                        Bukkit.getScheduler().cancelTask(taskID);
                        break;
                }
                count--;
            }
        }, 0, 20);

    }

    private void finishTesting(Player player, Role role) {
        if(role == Role.TRAITOR) {
            for(Block current : lamps)
                setColoredGlass(current.getLocation(), (role == Role.INNOCENT) ? DyeColor.LIME : DyeColor.RED);
            for(Block current : floor)
                current.setType(Material.AIR);
            for (Player current : Bukkit.getOnlinePlayers())
                current.playSound(testerLocation, Sound.NOTE_PLING, 10, 0.9F);
            Bukkit.broadcastMessage(Utils.prefix + Role.INNOCENT.getChatColor()+player.getName()+" §7ist ein "+Role.TRAITOR.getName());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    resetTester();
                }
            }, TESTING_TIME * 20);
        } else {
            for(Block current : lamps)
                setColoredGlass(current.getLocation(), (role == Role.INNOCENT) ? DyeColor.LIME : DyeColor.RED);
            for (Block current : borderBlocks)
                world.getBlockAt(current.getLocation()).setType(Material.IRON_BLOCK);
            for (Player current : Bukkit.getOnlinePlayers())
                current.playSound(testerLocation, Sound.NOTE_PLING, 10, 1.2F);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    resetTester();
                }
            }, TESTING_TIME * 20);
        }
    }

    public void load() {
            for (int i = 0; i < borderBlocks.length; i++)
                borderBlocks[i] = new LocationConfig(plugin, map.getName(), i, "Border").loadBlockLocation();
            for (int i = 0; i < lamps.length; i++)
                lamps[i] = new LocationConfig(plugin, map.getName(), i, "Lamp").loadBlockLocation();
            for (int i = 0; i < floor.length; i++)
                floor[i] = new LocationConfig(plugin, map.getName(), i, "Floor").loadBlockLocation();
            button = new LocationConfig(plugin, map.getName(), 0, "Button").loadBlockLocation();
            blockedLocation = new LocationConfig(plugin, "Tester", map.getName(), 0).loadLocation();
            testerLocation = new LocationConfig(plugin, "Tester", map.getName(), 1).loadLocation();

            world = map.getSpectatorLocation().getWorld();
            resetTester();
    }

    private void resetTester() {
        inUse = false;
        for(Block current : borderBlocks)
            world.getBlockAt(current.getLocation()).setType(Material.IRON_BLOCK);
        for(Block current : lamps)
            setColoredGlass(current.getLocation(), DyeColor.WHITE);
        for(Block current : floor)
            world.getBlockAt(current.getLocation()).setType(Material.IRON_BLOCK);
    }

    private void setColoredGlass(Location location, DyeColor dyeColor) {
        Block block = world.getBlockAt(location);
        block.setType(Material.STAINED_GLASS);
        block.setData(dyeColor.getData());
    }


    public boolean exists() {
        return new LocationConfig(plugin, "Tester", map.getName(), 0).hasLocation();
    }

    public Block getButton() {
        return button;
    }
}
