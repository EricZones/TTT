// Created by Eric B. 02.06.2020 17:42
package de.ericzones.ttt.extra;

import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class TesterSetup implements Listener {

    private Main plugin;
    private Player player;
    private Map map;
    private int phase;
    private boolean finished;

    private Block[] borderBlocks, lamps, floor;
    private Block button;
    private Location blockedLocation;
    private Location testerLocation;

    public TesterSetup(Main plugin, Player player, Map map) {
        this.plugin = plugin;
        this.player = player;
        this.map = map;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        phase = 1;
        finished = false;

        borderBlocks = new Block[3];
        lamps = new Block[2];
        floor = new Block[9];

        startSetup();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(!(e.getPlayer().getName().equals(player.getName()))) return;
        if(finished) return;
        e.setCancelled(true);
        switch (phase) {
            case 1: case 2: case 3:
                borderBlocks[phase-1] = e.getBlock();
                player.sendMessage(Utils.prefix + "§7Begrenzungsblock §a" + phase + " §7gesetzt");
                phase++;
                startPhase(phase);
                break;
            case 4: case 5:
                if(e.getBlock().getType() == Material.STAINED_GLASS) {
                    lamps[phase-4] = e.getBlock();
                    player.sendMessage(Utils.prefix + "§7Lampenblock §a" + (phase-3) + " §7gesetzt");
                    phase++;
                    startPhase(phase);
                } else {
                    player.sendMessage(Utils.prefix + "§cKlicke auf einen §eGlasblock");
                }
                break;
            case 6:
                if(e.getBlock().getType() == Material.WOOD_BUTTON || e.getBlock().getType() == Material.STONE_BUTTON) {
                    button = e.getBlock();
                    player.sendMessage(Utils.prefix + "§7Testknopf gesetzt");
                    phase++;
                    startPhase(phase);
                } else {
                    player.sendMessage(Utils.prefix + "§cKlicke auf einen §eKnopf");
                }
                break;
            case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16:
                if(e.getBlock().getType() == Material.IRON_BLOCK) {
                    floor[phase-9] = e.getBlock();
                    player.sendMessage(Utils.prefix + "§7Bodenblock §a"+(phase-8)+" §7gesetzt");
                    phase++;
                    startPhase(phase);
                } else {
                    player.sendMessage(Utils.prefix + "§cKlicke auf einen §eEisenblock");
                }
                break;
            case 17:
                if(e.getBlock().getType() == Material.IRON_BLOCK) {
                    floor[phase-9] = e.getBlock();
                    player.sendMessage(Utils.prefix + "§7Bodenblock §a"+(phase-8)+" §7gesetzt");
                    finishSetup();
                } else {
                    player.sendMessage(Utils.prefix + "§cKlicke auf einen §eEisenblock");
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        if(!(e.getPlayer().getName().equals(player.getName()))) return;
        if(finished) return;
        if(phase == 7) {
            blockedLocation = player.getLocation();
            player.sendMessage(Utils.prefix + "§7Tester-Blockiert Position gesetzt");
            finished = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    finished = false;
                    phase++;
                    startPhase(phase);
                }
            }, 25);
        } else if(phase == 8) {
            testerLocation = player.getLocation();
            player.sendMessage(Utils.prefix + "§7Tester Position gesetzt");
            phase++;
            startPhase(phase);
        }
    }

    public void finishSetup() {
        for(int i = 0; i < borderBlocks.length; i++) {
            new LocationConfig(plugin, borderBlocks[i].getLocation(), map.getName(), i, "Border").saveBlockLocation();
        }
        for(int i = 0; i < lamps.length; i++) {
            new LocationConfig(plugin, lamps[i].getLocation(), map.getName(), i, "Lamp").saveBlockLocation();
        }
        for(int i = 0; i < floor.length; i++) {
            new LocationConfig(plugin, floor[i].getLocation(), map.getName(), i, "Floor").saveBlockLocation();
        }
        new LocationConfig(plugin, button.getLocation(), map.getName(), 0, "Button").saveBlockLocation();
        new LocationConfig(plugin, blockedLocation, "Tester", map.getName(), 0).saveLocation();
        new LocationConfig(plugin, testerLocation, "Tester", map.getName(), 1).saveLocation();

        player.sendMessage(Utils.prefix + "§7Setup §aabgeschlossen");
        finished = true;
    }

    public void startPhase(int phase) {
        switch (phase) {
            case 1: case 2: case 3:
                player.sendMessage(Utils.prefix + "§7Klicke auf den §aBegrenzungsblock §7Nummer §a" + phase);
                break;
            case 4: case 5:
                player.sendMessage(Utils.prefix + "§7Klicke auf den §aLampenblock §7Nummer §a" + (phase - 3));
                break;
            case 6:
                player.sendMessage(Utils.prefix + "§7Klicke auf den §aTestknopf");
                break;
            case 7:
                player.sendMessage(Utils.prefix + "§7Sneake auf der §aTester-Blockiert §7Position");
                break;
            case 8:
                player.sendMessage(Utils.prefix + "§7Sneake auf der §aTester §7Position");
                break;
            case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17:
                player.sendMessage(Utils.prefix + "§7Klicke auf den §aBodenblock §7Nummer §a" + (phase - 8));
                break;
        }
    }

    public void startSetup() {
        player.sendMessage(Utils.prefix + "§7Tester-Setup §aaktiviert");
        startPhase(phase);
    }

}
