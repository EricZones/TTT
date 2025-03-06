// Created by Eric B. 05.06.2020 17:36
package de.ericzones.ttt.extra;

import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.cloudperms.CloudPermissionsManagement;
import de.ericzones.ttt.countdowns.LobbyCountdown;
import de.ericzones.ttt.game.EndingState;
import de.ericzones.ttt.game.GameStateManager;
import de.ericzones.ttt.game.IngameState;
import de.ericzones.ttt.game.LobbyState;
import de.ericzones.ttt.main.Main;
import de.ericzones.ttt.mapvoting.Map;
import de.ericzones.ttt.mapvoting.Voting;
import de.ericzones.ttt.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class ScoreBoard implements Listener {

    private Main plugin;
    private GameStateManager gameStateManager;

    public ScoreBoard(Main plugin) {
        this.plugin = plugin;
        gameStateManager = plugin.getGameStateManager();
        startUpdater();
    }

    public void setNewScoreBoard(Player player) {
        Scoreboard scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreBoard.getObjective("aaa");
        if(objective == null)
            objective = scoreBoard.registerNewObjective("aaa", "bbb");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§8• §b§lRyanCole§8§l.§b§lde §8•");
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);

        if(gameStateManager.getCurrentGameState() instanceof LobbyState) {
            LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
            Map finalMap = lobbyState.getCountdown().getFinalMap();

            objective.getScore(" ").setScore(6);
            objective.getScore("§fMap").setScore(5);
            if(finalMap != null)
                objective.getScore(updateScore(scoreBoard, "map", "§8» §a", finalMap.getName(), ChatColor.GREEN)).setScore(4);
            else
                objective.getScore(updateScore(scoreBoard, "map", "§8» §a", "Voting", ChatColor.GREEN)).setScore(4);
            objective.getScore("  ").setScore(3);
            objective.getScore("§fKarma").setScore(2);
            objective.getScore("§8» §b0").setScore(1);
        } else if(gameStateManager.getCurrentGameState() instanceof IngameState) {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            Map finalMap = ingameState.getMap();
            ArrayList<Player> players = plugin.getPlayers();

            objective.getScore(" ").setScore(14);
            objective.getScore("§fMap").setScore(13);
            objective.getScore("§8» §a"+finalMap.getName()).setScore(12);
            objective.getScore("  ").setScore(11);
            if(playerRole != null && !((IngameState) gameStateManager.getCurrentGameState()).getSpectators().contains(player) && playerRole == Role.TRAITOR) {
                int points = plugin.getRoleInventories().getPointManager().getPlayerPoints(player);

                objective.getScore("§fV-Punkte").setScore(10);
                objective.getScore(updateScore(scoreBoard, "vpoints", "§8» §4", String.valueOf(points), ChatColor.DARK_RED)).setScore(9);
                objective.getScore("   ").setScore(8);
            } else if(playerRole != null && !((IngameState) gameStateManager.getCurrentGameState()).getSpectators().contains(player) && playerRole == Role.DETECTIVE) {
                int points = plugin.getRoleInventories().getPointManager().getPlayerPoints(player);

                objective.getScore("§fD-Punkte").setScore(10);
                objective.getScore(updateScore(scoreBoard, "dpoints", "§8» §9", String.valueOf(points), ChatColor.BLUE)).setScore(9);
                objective.getScore("   ").setScore(8);
            }
            objective.getScore("§fLebende").setScore(7);
            objective.getScore(updateScore(scoreBoard, "players", "§8» §c", String.valueOf(players.size()), ChatColor.RED)).setScore(6);
            objective.getScore("    ").setScore(5);
            objective.getScore("§fKarma").setScore(4);
            objective.getScore("§8» §b0").setScore(3);
        } else if(gameStateManager.getCurrentGameState() instanceof EndingState) {
            Role winnerRole = Utils.winnerRole;

            objective.getScore(" ").setScore(6);
            objective.getScore("§fSieger").setScore(5);
            if(winnerRole == Role.TRAITOR)
                objective.getScore("§8» §4Verräter").setScore(4);
            else if(winnerRole == Role.INNOCENT)
                objective.getScore("§8» §aUnschuldigen").setScore(4);
            objective.getScore("  ").setScore(3);
            objective.getScore("§fKarma").setScore(2);
            objective.getScore("§8» §b0").setScore(1);
        }

        if(gameStateManager.getCurrentGameState() instanceof LobbyState || gameStateManager.getCurrentGameState() instanceof EndingState || ((gameStateManager.getCurrentGameState() instanceof IngameState) && ((IngameState) gameStateManager.getCurrentGameState()).isProtected())) {
            Team admin = registerTeam(scoreBoard, "0000Admin", "§cAdmin §8┃ §c", "");
            Team developer = registerTeam(scoreBoard, "0001Dev", "§bDev §8┃ §b", "");
            Team srmoderator = registerTeam(scoreBoard, "0002SrMod", "§aSrMod §8┃ §a", "");
            Team moderator = registerTeam(scoreBoard, "0003Mod", "§aMod §8┃ §a", "");
            Team supporter = registerTeam(scoreBoard, "0004Sup", "§9Sup §8┃ §9", "");
            Team jrsupporter = registerTeam(scoreBoard, "0005JrSup", "§9JrSup §8┃ §9", "");
            Team srbuilder = registerTeam(scoreBoard, "0006SrBuild", "§2SrBuild §8┃ §2", "");
            Team builder = registerTeam(scoreBoard, "0007Build", "§2Build §8┃ §2", "");
            Team premium = registerTeam(scoreBoard, "0008Prem", "§6Prem §8┃ §6", "");
            Team spieler = registerTeam(scoreBoard, "0009Spieler", "§7", "");

            for (Player current : Bukkit.getOnlinePlayers()) {
                IPermissionUser user = CloudPermissionsManagement.getInstance().getUser(current.getUniqueId());
                String gruppe = CloudPermissionsManagement.getInstance().getHighestPermissionGroup(user).getName();
                if (gruppe.equals("Admin")) {
                    admin.addPlayer(current);
                } else if (gruppe.equals("Developer")) {
                    developer.addPlayer(current);
                } else if (gruppe.equals("SrModerator")) {
                    srmoderator.addPlayer(current);
                } else if (gruppe.equals("Moderator")) {
                    moderator.addPlayer(current);
                } else if (gruppe.equals("Supporter")) {
                    supporter.addPlayer(current);
                } else if (gruppe.equals("JrSupporter")) {
                    jrsupporter.addPlayer(current);
                } else if (gruppe.equals("SrBuilder")) {
                    srbuilder.addPlayer(current);
                } else if (gruppe.equals("Builder")) {
                    builder.addPlayer(current);
                } else if (gruppe.equals("Premium")) {
                    premium.addPlayer(current);
                } else {
                    spieler.addPlayer(current);
                }
            }
        } else if(gameStateManager.getCurrentGameState() instanceof IngameState) {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            if(playerRole == Role.TRAITOR) {
                Team detective = registerTeam(scoreBoard, "0000Det", "§9", "");
                Team innocent = registerTeam(scoreBoard, "0001Inno", "§a", "");
                Team traitor = registerTeam(scoreBoard, "0002Trait", "§4", "");
                Team spectator = registerTeam(scoreBoard, "0003Spec", "§7Spec §8┃ §7", "");

                for (Player current : Bukkit.getOnlinePlayers()) {
                    Role currentRole = plugin.getRoleManager().getPlayerRole(current);
                    if(ingameState.getSpectators().contains(current))
                        currentRole = null;

                    if(currentRole == Role.DETECTIVE) {
                        detective.addPlayer(current);
                    } else if(currentRole == Role.TRAITOR) {
                        traitor.addPlayer(current);
                    } else if(currentRole == Role.INNOCENT) {
                        innocent.addPlayer(current);
                    } else {
                        spectator.addPlayer(current);
                    }
                }
            } else {
                Team detective = registerTeam(scoreBoard, "0000Det", "§9", "");
                Team innocent = registerTeam(scoreBoard, "0001Inno", "§a", "");
                Team spectator = registerTeam(scoreBoard, "0003Spec", "§7Spec §8┃ §7", "");

                for (Player current : Bukkit.getOnlinePlayers()) {
                    Role currentRole = plugin.getRoleManager().getPlayerRole(current);
                    if(ingameState.getSpectators().contains(current))
                        currentRole = null;

                    if(currentRole == Role.DETECTIVE) {
                        detective.addPlayer(current);
                    } else if(currentRole == Role.INNOCENT || currentRole == Role.TRAITOR) {
                        innocent.addPlayer(current);
                    } else {
                        spectator.addPlayer(current);
                    }
                }
            }
        }
        
        player.setScoreboard(scoreBoard);
    }

    public void updateScoreBoard(Player player) {
        if(player.getScoreboard() == null)
            setNewScoreBoard(player);
        Scoreboard scoreBoard = player.getScoreboard();
        Objective objective = scoreBoard.getObjective("aaa");
        if(objective == null)
            objective = scoreBoard.registerNewObjective("aaa", "bbb");
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);

        if(gameStateManager.getCurrentGameState() instanceof LobbyState) {
            LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
            Map finalMap = lobbyState.getCountdown().getFinalMap();
            if(finalMap != null)
                objective.getScore(updateScore(scoreBoard, "map", "§8» §a", finalMap.getName(), ChatColor.GREEN)).setScore(4);
            else
                objective.getScore(updateScore(scoreBoard, "map", "§8» §a", "Voting", ChatColor.GREEN)).setScore(4);
        } else if(gameStateManager.getCurrentGameState() instanceof IngameState) {
            ArrayList<Player> players = plugin.getPlayers();

            if(playerRole != null && !((IngameState) gameStateManager.getCurrentGameState()).getSpectators().contains(player) && playerRole == Role.TRAITOR) {
                int points = plugin.getRoleInventories().getPointManager().getPlayerPoints(player);
                objective.getScore(updateScore(scoreBoard, "vpoints", "§8» §4", String.valueOf(points), ChatColor.DARK_RED)).setScore(9);
            } else if(playerRole != null && !((IngameState) gameStateManager.getCurrentGameState()).getSpectators().contains(player) && playerRole == Role.DETECTIVE) {
                int points = plugin.getRoleInventories().getPointManager().getPlayerPoints(player);
                objective.getScore(updateScore(scoreBoard, "dpoints", "§8» §9", String.valueOf(points), ChatColor.BLUE)).setScore(9);
            }
            objective.getScore(updateScore(scoreBoard, "players", "§8» §c", String.valueOf(players.size()), ChatColor.RED)).setScore(6);
        }

        if(gameStateManager.getCurrentGameState() instanceof LobbyState || gameStateManager.getCurrentGameState() instanceof EndingState || ((gameStateManager.getCurrentGameState() instanceof IngameState) && ((IngameState) gameStateManager.getCurrentGameState()).isProtected())) {
            Team admin = registerTeam(scoreBoard, "0000Admin", "§cAdmin §8┃ §c", "");
            Team developer = registerTeam(scoreBoard, "0001Dev", "§bDev §8┃ §b", "");
            Team srmoderator = registerTeam(scoreBoard, "0002SrMod", "§aSrMod §8┃ §a", "");
            Team moderator = registerTeam(scoreBoard, "0003Mod", "§aMod §8┃ §a", "");
            Team supporter = registerTeam(scoreBoard, "0004Sup", "§9Sup §8┃ §9", "");
            Team jrsupporter = registerTeam(scoreBoard, "0005JrSup", "§9JrSup §8┃ §9", "");
            Team srbuilder = registerTeam(scoreBoard, "0006SrBuild", "§2SrBuild §8┃ §2", "");
            Team builder = registerTeam(scoreBoard, "0007Build", "§2Build §8┃ §2", "");
            Team premium = registerTeam(scoreBoard, "0008Prem", "§6Prem §8┃ §6", "");
            Team spieler = registerTeam(scoreBoard, "0009Spieler", "§7", "");

            for (Player current : Bukkit.getOnlinePlayers()) {
                IPermissionUser user = CloudPermissionsManagement.getInstance().getUser(current.getUniqueId());
                String gruppe = CloudPermissionsManagement.getInstance().getHighestPermissionGroup(user).getName();
                if (gruppe.equals("Admin")) {
                    admin.addPlayer(current);
                } else if (gruppe.equals("Developer")) {
                    developer.addPlayer(current);
                } else if (gruppe.equals("SrModerator")) {
                    srmoderator.addPlayer(current);
                } else if (gruppe.equals("Moderator")) {
                    moderator.addPlayer(current);
                } else if (gruppe.equals("Supporter")) {
                    supporter.addPlayer(current);
                } else if (gruppe.equals("JrSupporter")) {
                    jrsupporter.addPlayer(current);
                } else if (gruppe.equals("SrBuilder")) {
                    srbuilder.addPlayer(current);
                } else if (gruppe.equals("Builder")) {
                    builder.addPlayer(current);
                } else if (gruppe.equals("Premium")) {
                    premium.addPlayer(current);
                } else {
                    spieler.addPlayer(current);
                }
            }
        } else if(gameStateManager.getCurrentGameState() instanceof IngameState) {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            if(playerRole == Role.TRAITOR) {
                Team detective = registerTeam(scoreBoard, "0000Det", "§9", "");
                Team innocent = registerTeam(scoreBoard, "0001Inno", "§a", "");
                Team traitor = registerTeam(scoreBoard, "0002Trait", "§4", "");
                Team spectator = registerTeam(scoreBoard, "0003Spec", "§7Spec §8┃ §7", "");

                for (Player current : Bukkit.getOnlinePlayers()) {
                    Role currentRole = plugin.getRoleManager().getPlayerRole(current);
                    if(ingameState.getSpectators().contains(current))
                        currentRole = null;

                    if(currentRole == Role.DETECTIVE) {
                        detective.addPlayer(current);
                    } else if(currentRole == Role.TRAITOR) {
                        traitor.addPlayer(current);
                    } else if(currentRole == Role.INNOCENT) {
                        innocent.addPlayer(current);
                    } else {
                        spectator.addPlayer(current);
                    }
                }
            } else {
                Team detective = registerTeam(scoreBoard, "0000Det", "§9", "");
                Team innocent = registerTeam(scoreBoard, "0001Inno", "§a", "");
                Team spectator = registerTeam(scoreBoard, "0003Spec", "§7Spec §8┃ §7", "");

                for (Player current : Bukkit.getOnlinePlayers()) {
                    Role currentRole = plugin.getRoleManager().getPlayerRole(current);
                    if(ingameState.getSpectators().contains(current))
                        currentRole = null;

                    if(currentRole == Role.DETECTIVE) {
                        detective.addPlayer(current);
                    } else if(currentRole == Role.INNOCENT || currentRole == Role.TRAITOR) {
                        innocent.addPlayer(current);
                    } else {
                        spectator.addPlayer(current);
                    }
                }
            }
        }
    }

    private void startUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player current : Bukkit.getOnlinePlayers())
                    updateScoreBoard(current);
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private Team registerTeam(Scoreboard scoreBoard, String team, String prefix, String suffix) {
        Team rank = scoreBoard.getTeam(team);
        if(rank == null)
            rank = scoreBoard.registerNewTeam(team);
        rank.setPrefix(prefix);
        rank.setSuffix(suffix);
        return rank;
    }

    private String updateScore(Scoreboard scoreBoard, String team, String prefix, String suffix, ChatColor entry) {
        Team rank = scoreBoard.getTeam(team);
        if(rank == null)
            rank = scoreBoard.registerNewTeam(team);
        rank.setPrefix(prefix);
        rank.setSuffix(suffix);
        rank.addEntry(entry.toString());
        return entry.toString();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        setNewScoreBoard(e.getPlayer());
        for(Player current : Bukkit.getOnlinePlayers())
            updateScoreBoard(current);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        for(Player current : Bukkit.getOnlinePlayers())
            updateScoreBoard(current);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        for(Player current : Bukkit.getOnlinePlayers())
            updateScoreBoard(current);
    }



}
