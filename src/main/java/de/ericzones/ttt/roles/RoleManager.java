// Created by Eric B. 31.05.2020 16:57
package de.ericzones.ttt.roles;

import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.main.Main;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RoleManager {

    private Main plugin;
    private HashMap<String, Role> playerRoles;
    private ArrayList<Player> players;
    private ArrayList<String> traitorPlayers;
    private ArrayList<Integer> traitorArmor;

    private int innocents, traitors, detectives;

    public RoleManager(Main plugin) {
        this.plugin = plugin;
        playerRoles = new HashMap<>();
        players = plugin.getPlayers();
        traitorPlayers = new ArrayList<>();
        traitorArmor = new ArrayList<>();
    }

    public void giveRoles(int playerSize) {
        traitors = (int) Math.round(Math.log(playerSize) * 1.2);
        detectives = (int) Math.round(Math.log(playerSize) * 0.75);
        innocents = playerSize - traitors - detectives;

        Collections.shuffle(players);
        int count = 0;
        for(int i = count; i < traitors; i++) {
            playerRoles.put(players.get(i).getName(), Role.TRAITOR);
            traitorPlayers.add(players.get(i).getName());
        }
        count += traitors;

        for(int i = count; i < detectives + count; i++) {
            playerRoles.put(players.get(i).getName(), Role.DETECTIVE);
        }
        count += detectives;

        for(int i = count; i < innocents + count; i++) {
            playerRoles.put(players.get(i).getName(), Role.INNOCENT);
        }

        for(Player current : players) {
            switch(getPlayerRole(current)) {

                case TRAITOR:
//                    for(Player others : players)
//                        setRoleArmor(others, current.getEntityId(), (getPlayerRole(others) != Role.TRAITOR) ? Color.LIME : Color.RED);
                    setArmor(current, Color.RED);
                    for(Player others : Bukkit.getOnlinePlayers()) {
                        if(getPlayerRole(others) != Role.TRAITOR) {
                            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(current.getEntityId(), 3, CraftItemStack.asNMSCopy(getChestplate(Color.LIME)));
                            ((CraftPlayer) others).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                    traitorArmor.add(current.getEntityId());
                    current.getInventory().addItem(plugin.getRoleInventories().getTraitorItem());
                    break;
                case DETECTIVE:
                    setArmor(current, Color.BLUE);
                    current.getInventory().addItem(plugin.getRoleInventories().getDetectiveItem());
                    break;
                case INNOCENT:
                    setArmor(current, Color.LIME);
                    break;
                default:
                    break;
            }
        }
        startFakeArmor();
    }

    public void giveArmor() {
        for(Player current : players) {
            setArmor(current, Color.LIME);
        }
    }

    private void setArmor(Player player, Color color) {
        player.getInventory().setChestplate(getChestplate(color));
    }

    private void startFakeArmor() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            final ItemStack armor = getChestplate(Color.LIME);
            final int ARMOR_SLOT = 3;
            @Override
            public void run() {
                for(int entityID : traitorArmor) {
                    for (Player current : Bukkit.getOnlinePlayers()) {
                        if (getPlayerRole(current) != Role.TRAITOR) {
                            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityID, ARMOR_SLOT, CraftItemStack.asNMSCopy(armor));
                            ((CraftPlayer) current).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                }
            }
        }, 0, 5);
    }

//    private void setRoleArmor(Player player, int entityID, Color color) {
//        ItemStack armor = getChestplate(color);
//
//        final int ARMOR_SLOT = 3;
//        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityID, ARMOR_SLOT, CraftItemStack.asNMSCopy(armor));
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
//    }

    private ItemStack getChestplate(Color color) {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return item;
    }

    public Role getPlayerRole(Player player) {
        return playerRoles.get(player.getName());
    }

    public ArrayList<String> getTraitorPlayers() {
        return traitorPlayers;
    }
}
