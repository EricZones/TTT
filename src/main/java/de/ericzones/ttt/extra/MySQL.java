// Created by Eric B. 28.05.2020 15:44
package de.ericzones.ttt.extra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

    private static final String username = "username";
    private static final String password = "password";
    private static final String database = "database";
    private static final String host = "localhost";
    private static final String port = "3306";
    private static Connection con;


    public static void connect() {
        if(!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
                System.out.println("[TTT] MySQL Verbindung hergestellt");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close() {
        if(isConnected()) {
            try {
                con.close();
                System.out.println("[TTT] MySQL Verbindung getrennt");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private static boolean isConnected() {
        return con != null;
    }

    public static void update(String query) {
        if(isConnected()) {
            try {
                con.createStatement().executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static ResultSet getResult(String query) {
        if(isConnected()) {
            try {
                return con.createStatement().executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public static void createTable() {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Locations (Map VARCHAR(100), Number VARCHAR(100), World VARCHAR(100), X VARCHAR(100), Y VARCHAR(100), Z VARCHAR(100), Yaw VARCHAR(100), Pitch VARCHAR(100))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTable2() {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Maps (World VARCHAR(100), Builder VARCHAR(100))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTable3() {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS RoleTesting (World VARCHAR(100), Type VARCHAR(100), Number VARCHAR(100), X VARCHAR(100), Y VARCHAR(100), Z VARCHAR(100))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTable4() {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Stats (Spieler VARCHAR(100), UUID VARCHAR(100), VTickets VARCHAR(100), DTickets VARCHAR(100), Karma VARCHAR(100), Spiele VARCHAR(100), Siege VARCHAR(100), Kills VARCHAR(100), Tode VARCHAR(100))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




}
