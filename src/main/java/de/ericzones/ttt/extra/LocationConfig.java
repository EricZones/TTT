// Created by Eric B. 28.05.2020 15:39
package de.ericzones.ttt.extra;

import de.ericzones.ttt.main.Main;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationConfig {

    private Main plugin;
    private Location location;
    private String root;
    private String world;
    private int number;
    private String type;

    public LocationConfig(Main plugin, Location location, String root, String world, int number) {
        this.plugin = plugin;
        this.location = location;
        this.root = root;
        this.world = world;
        this.number = number;
    }

    public LocationConfig(Main plugin, Location location, String world, int number, String type) {
        this.plugin = plugin;
        this.location = location;
        this.world = world;
        this.number = number;
        this.type = type;
    }

    public LocationConfig(Main plugin, String root, String world, int number) {
        this(plugin, null, root, world, number);
    }

    public LocationConfig(Main plugin, String world, int number, String type) {
        this(plugin, null, world, number, type);
    }

    public void saveBlockLocation() {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        if(!hasBlockLocation()) {
            MySQL.update("INSERT INTO RoleTesting (World, Type, Number, X, Y, Z) VALUES ('" + world + "','" + type + "','" + number + "','" + x + "','" + y + "','" + z + "')");
        } else {
            MySQL.update("UPDATE RoleTesting SET X='"+x+"',Y='"+y+"',Z='"+z+"' WHERE World='"+this.world+"' AND Type='"+type+"' AND Number='"+number+"'");
        }
    }

    public Block loadBlockLocation() {
        ResultSet rs = MySQL.getResult("SELECT * FROM RoleTesting WHERE World='"+world+"' AND Type='"+type+"' AND Number='"+number+"'");
        Block block = null;
        try {
            while(rs.next()) {
                World world2 = Bukkit.getWorld(world);
                int x = rs.getInt("X");
                int y = rs.getInt("Y");
                int z = rs.getInt("Z");
                block = new Location(world2, x, y, z).getBlock();
                return block;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return block;
    }

    public void saveLocation() {

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        if(!hasLocation()) {
            MySQL.update("INSERT INTO Locations (Map, Number, World, X, Y, Z, Yaw, Pitch) VALUES ('" + root + "','" + number + "','" + world + "','" + x + "','" + y + "','" + z + "','" + yaw + "','" + pitch + "')");
        } else {
            MySQL.update("UPDATE Locations SET X='"+x+"',Y='"+y+"',Z='"+z+"',Yaw='"+yaw+"',Pitch='"+pitch+"' WHERE World='"+this.world+"' AND Map='"+root+"' AND Number='"+number+"'");
        }
    }

    public Location loadLocation() {
        ResultSet rs = MySQL.getResult("SELECT * FROM Locations WHERE World='"+world+"' AND Map='"+root+"' AND Number='"+number+"'");
        Location location = null;
        try {
            while(rs.next()) {
                World world2 = Bukkit.getWorld(world);
                double x = rs.getDouble("X");
                double y = rs.getDouble("Y");
                double z = rs.getDouble("Z");
                float yaw = rs.getFloat("Yaw");
                float pitch = rs.getFloat("Pitch");
                location = new Location(world2, x, y, z, yaw, pitch);
                return location;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return location;
    }

    public boolean hasLocation() {
        ResultSet rs = MySQL.getResult("SELECT * FROM Locations WHERE Map='"+root+"' AND World='"+world+"' AND Number='"+number+"'");
        try {
            while(rs.next()) {
                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasBlockLocation() {
        ResultSet rs = MySQL.getResult("SELECT * FROM RoleTesting WHERE World='"+world+"' AND Number='"+number+"' AND Type='"+type+"'");
        try {
            while(rs.next()) {
                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
