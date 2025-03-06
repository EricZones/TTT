// Created by Eric B. 31.05.2020 16:51
package de.ericzones.ttt.roles;

import org.bukkit.ChatColor;

public enum Role {

    INNOCENT("§aUnschuldiger", ChatColor.GREEN, "§eBleibe am Leben"),
    TRAITOR("§4Verräter", ChatColor.DARK_RED, "§eTöte alle Spieler"),
    DETECTIVE("§9Detektiv", ChatColor.BLUE, "§eFinde die Verräter");

    private Role(String name, ChatColor chatColor, String mission) {
        this.name = name;
        this.chatColor = chatColor;
        this.mission = mission;
    }

    private String name;
    private ChatColor chatColor;
    private String mission;

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public String getMission() {
        return mission;
    }
}
