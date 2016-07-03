package me.philipsnostrum.bungeepexbridge.helpers;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.sql.*;

public class MySQL {
    public Connection c = null;
    public boolean enabled = false;

    public MySQL() {
        open();
    }

    private void open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.c = DriverManager.getConnection("jdbc:mysql://" + BungeePexBridge.getConfig().mysql_hostname + ":" + BungeePexBridge.getConfig().mysql_port + "/" + BungeePexBridge.getConfig().mysql_db + "?autoReconnect=true&useUnicode=yes", BungeePexBridge.getConfig().mysql_user, BungeePexBridge.getConfig().mysql_pass);
            BungeePexBridge.get().getLogger().info("Connected to MySQL successfully");
            enabled = true;
        } catch (SQLException e) {
            BungeePexBridge.get().getLogger().info("Could not connect to MySQL server! Disabling data loading!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            BungeePexBridge.get().getLogger().info("JDBC Driver not found!");
        }
    }

    public Connection getCon() {
        try {
            if (c == null || c.isClosed())
                open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public void closeConnection() {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        c = null;
        enabled = false;
    }
}