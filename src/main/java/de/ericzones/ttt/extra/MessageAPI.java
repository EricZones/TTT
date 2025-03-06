// Created by Eric B. 29.05.2020 19:46
package de.ericzones.ttt.extra;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class MessageAPI {

    public static void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subTitle) {
        PlayerConnection con = ((CraftPlayer) p).getHandle().playerConnection;
        PacketPlayOutTitle PacketPlayOutTime = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn.intValue(), stay.intValue(), fadeOut.intValue());

        con.sendPacket(PacketPlayOutTime);
        if(subTitle != null) {
            IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subTitle + "\"}");
            PacketPlayOutTitle PacketPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
            con.sendPacket(PacketPlayOutSubTitle);
        }

        if(title != null) {
            IChatBaseComponent Title = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle PacketPlayOutBigTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, Title);
            con.sendPacket(PacketPlayOutBigTitle);
        }

    }

    public static void ActionBar(Player player, String Nachricht) {
        String s = Nachricht.replace("_", " ");
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s +
                "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(bar);
    }

    public static void sendActionBar(String message) {
        Bukkit.getOnlinePlayers().forEach(current -> ActionBar(current, message));
    }



}
