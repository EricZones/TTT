// Created by Eric B. 28.05.2020 12:54
package de.ericzones.ttt.main;

import de.ericzones.ttt.commands.BuildCommand;
import de.ericzones.ttt.commands.ForcemapCommand;
import de.ericzones.ttt.commands.SetupCommand;
import de.ericzones.ttt.commands.StartCommand;
import de.ericzones.ttt.extra.MySQL;
import de.ericzones.ttt.extra.ScoreBoard;
import de.ericzones.ttt.extra.SpectatorInventory;
import de.ericzones.ttt.extra.Utils;
import de.ericzones.ttt.game.GameState;
import de.ericzones.ttt.game.GameStateManager;
import de.ericzones.ttt.listener.*;
import de.ericzones.ttt.mapvoting.Map;
import de.ericzones.ttt.mapvoting.Voting;
import de.ericzones.ttt.roles.PointManager;
import de.ericzones.ttt.roles.Role;
import de.ericzones.ttt.roles.RoleInventories;
import de.ericzones.ttt.roles.RoleManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends JavaPlugin {

    private GameStateManager gameStateManager;
    private ArrayList<Player> players;
    private ArrayList<Map> maps;
    private Voting voting;
    private RoleManager roleManager;
    private ProtectionListener protectionListener;
    private RoleInventories roleInventories;
    private ScoreBoard scoreBoard;
    private ArrayList<Map> load;
    private SpectatorInventory spectatorInventory;

    @Override
    public void onEnable() {
        MySQL.connect();
        MySQL.createTable();
        MySQL.createTable2();
        MySQL.createTable3();
        MySQL.createTable4();
        gameStateManager = new GameStateManager(this);
        players = new ArrayList<>();
        load = new ArrayList<>();
        gameStateManager.setGameState(GameState.LOBBY_STATE);
        registerPlugins(Bukkit.getPluginManager());

    }

    @Override
    public void onDisable() {
        MySQL.close();
    }

    private void registerPlugins(PluginManager pluginManager) {
        registerMaps();
        loadMaps();
        roleManager = new RoleManager(this);
        protectionListener = new ProtectionListener(this);
        roleInventories = new RoleInventories(this);
        scoreBoard = new ScoreBoard(this);
        spectatorInventory = new SpectatorInventory(this);
        getCommand("setup").setExecutor(new SetupCommand(this));
        getCommand("start").setExecutor(new StartCommand(this));
        getCommand("build").setExecutor(new BuildCommand(this));
        getCommand("forcemap").setExecutor(new ForcemapCommand(this));

        pluginManager.registerEvents(new PlayerLobbyJoinListener(this), this);
        pluginManager.registerEvents(new VotingListener(this), this);
        pluginManager.registerEvents(new GameListener(this), this);
        pluginManager.registerEvents(protectionListener, this);
        pluginManager.registerEvents(new ChatListener(this), this);
        pluginManager.registerEvents(new ChestListener(this), this);
        pluginManager.registerEvents(new RoleTestingListener(this), this);
        pluginManager.registerEvents(roleInventories, this);
        pluginManager.registerEvents(new ShopItemListener(this), this);
        pluginManager.registerEvents(new PlayerEndingJoinListener(this) ,this);
        pluginManager.registerEvents(scoreBoard, this);
        pluginManager.registerEvents(spectatorInventory, this);
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    private void registerMaps() {
        maps = new ArrayList<>();
        ResultSet rs = MySQL.getResult("SELECT World FROM Maps");
        try{
            while(rs.next()) {
                Map map = new Map(this, rs.getString("World"));
                if(map.isPlayable()) {
                    maps.add(map);
                    load.add(map);
                } else {
                    Bukkit.getConsoleSender().sendMessage(Utils.prefix + "§cDie Map " + map.getName() + " ist nicht eingerichtet");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        if(maps.size() >= Voting.MIN_MAPS) {
            voting = new Voting(this, maps);
        } else {
            Bukkit.getConsoleSender().sendMessage(Utils.prefix + "§cEs müssen mindestens " + Voting.MIN_MAPS + " Maps eingerichtet sein");
            voting = null;
        }
    }

    private void loadMaps() {
        Map map = new Map(this, "Endinglobby");
        load.add(map);
        for(int i = 0; i < load.size(); i++) {
            String name = load.get(i).getName();
            WorldCreator creator = (WorldCreator) WorldCreator.name(name).environment(org.bukkit.World.Environment.NORMAL).type(WorldType.NORMAL);
            Bukkit.createWorld(creator);
            Bukkit.getWorld(name).setGameRuleValue("doFireTick", "false");
        }
    }

    public Voting getVoting() {
        return voting;
    }

    /*
    Komplette Spielerliste
     */

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void removePlayer(Player player) {
        if(players.contains(player)) {
            players.remove(player);
        }
    }

    public void addPlayer(Player player)  {
        if(!players.contains(player)) {
            players.add(player);
        }
    }

    public ArrayList<Map> getMaps() {
        return maps;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public ProtectionListener getProtectionListener() {
        return protectionListener;
    }

    public RoleInventories getRoleInventories() {
        return roleInventories;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public SpectatorInventory getSpectatorInventory() {
        return spectatorInventory;
    }
}
