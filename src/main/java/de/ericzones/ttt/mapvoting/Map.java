// Created by Eric B. 29.05.2020 16:21
package de.ericzones.ttt.mapvoting;

import de.ericzones.ttt.extra.LocationConfig;
import de.ericzones.ttt.extra.MySQL;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.roles.RoleTesting;
import org.bukkit.*;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Map {

    private Main plugin;
    private String name;
    private String builder;
    private Location[] spawnLocations = new Location[LobbyState.MAX_PLAYERS];
    private Location spectatorLocation;
    private RoleTesting roleTesting;

    private int votes;

    public Map(Main plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.roleTesting = new RoleTesting(plugin, this);

    }

    public void create(String builder) {
        this.builder = builder;
        MySQL.update("INSERT INTO Maps (World, Builder) VALUES ('"+name+"','"+builder+"')");
    }

    public void delete() {
        MySQL.update("DELETE FROM Maps WHERE World='"+name+"'");
        MySQL.update("DELETE FROM Locations WHERE World='"+name+"'");
        MySQL.update("DELETE FROM RoleTesting WHERE World='"+name+"'");
        if(Bukkit.getWorld(name) != null)
            Bukkit.unloadWorld(name, false);
    }

    public void load() {
        spectatorLocation = new LocationConfig(plugin, "Ingame", name, 0).loadLocation();
        for(int i = 0; i < spawnLocations.length; i++) {
           spawnLocations[i] = new LocationConfig(plugin, "Ingame", name, i + 1).loadLocation();
        }
        if(roleTesting.exists())
            roleTesting.load();
        else
            Bukkit.getConsoleSender().sendMessage(Utils.prefix + "§cTester für "+getName()+" nicht gesetzt");
    }

    public boolean exists() {
        ResultSet rs = MySQL.getResult("SELECT * FROM Maps WHERE World='"+name+"'");
        try {
            while(rs.next()) {
                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setSpawnLocation(int spawnNumber, Location location) {
        spawnLocations[spawnNumber - 1] = location;
        new LocationConfig(plugin, location, "Ingame", name, spawnNumber).saveLocation();
    }

    public void setSpectatorLocation(Location location) {
        spectatorLocation = location;
        new LocationConfig(plugin, location, "Ingame", name, 0).saveLocation();
    }

    public void addVote() {
        votes++;
    }

    public void removeVote() {
        votes--;
    }

    public boolean isPlayable() {
        ArrayList spawnPoints = new ArrayList();
        ResultSet rs = MySQL.getResult("SELECT * FROM Locations WHERE World='"+name+"' AND Map='Ingame'");
        try {
            while(rs.next()) {
                spawnPoints.add(rs.getInt("Number"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        if(spawnPoints.size() < LobbyState.MAX_PLAYERS + 1) {
            return false;
        } else {
            return true;
        }
    }

    public String getName() {
        return name;
    }

    public String getBuilder() {
        if(builder == null) {
            ResultSet rs = MySQL.getResult("SELECT Builder FROM Maps WHERE World='"+name+"'");
            try {
                while(rs.next()) {
                    builder = rs.getString("Builder");
                    return builder;
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        return builder;
    }

    public Location[] getSpawnLocations() {
        return spawnLocations;
    }

    public Location getSpectatorLocation() {
        return spectatorLocation;
    }

    public int getVotes() {
        return votes;
    }

    public RoleTesting getRoleTesting() {
        return roleTesting;
    }
}
