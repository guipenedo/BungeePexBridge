package me.philipsnostrum.bungeepexbridge.permsystem;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionsEx implements PermissionSystem {

    @Override
    public boolean requiresMySQL() {
        return true;
    }

    @Override
    public List<String> getGroups() throws SQLException {
        Connection c = BungeePexBridge.getDB().getConnection();
        ResultSet res = c.createStatement().executeQuery("SELECT name FROM `" + BungeePexBridge.getConfig().pex_tables_permissionsEntity + "` WHERE type='0'");
        return BungeePexBridge.getDB().resultSetToList(res, "name");
    }

    @Override
    public List<String> getGroupPermissions(String group) {
        List<String> perms = new ArrayList<String>();
        Connection c = BungeePexBridge.getDB().getConnection();
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().pex_tables_permissions + "` WHERE name = '" + group + "'");

            while (res.next()) {
                if (Arrays.asList("rank", "prefix", "default").contains(res.getString("permission")))
                    continue;
                perms.add(res.getString("permission"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perms;
    }

    @Override
    public List<String> getInheritance(String group) throws SQLException {
        Connection c = BungeePexBridge.getDB().getConnection();
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT parent FROM `" + BungeePexBridge.getConfig().pex_tables_permissionsInheritance + "` WHERE child = '" + group + "' AND type='0'");
            return BungeePexBridge.getDB().resultSetToList(res, "parent");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    @Override
    public long getRank(String group) {
        Connection c = BungeePexBridge.getDB().getConnection();
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT value FROM `" + BungeePexBridge.getConfig().pex_tables_permissions + "` WHERE name = '" + group + "' AND permission = 'rank'");
            if (res.next())
                return Long.parseLong(res.getString("value"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> getPlayerPermissions(ProxiedPlayer player) throws SQLException {
        Connection c = BungeePexBridge.getDB().getConnection();
        List<String> permissions = new ArrayList<String>();
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().pex_tables_permissions + "` WHERE name = '" + player.getUniqueId().toString() + "'");
            while (res.next()) {
                if (Arrays.asList("rank", "prefix", "name").contains(res.getString("permission")))
                    continue;
                permissions.add(res.getString("permission"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    @Override
    public List<String> getPlayerGroups(ProxiedPlayer player) throws SQLException {
        Connection c = BungeePexBridge.getDB().getConnection();
        List<String> groups = new ArrayList<String>();
        ResultSet res = c.createStatement().executeQuery("SELECT parent FROM `" + BungeePexBridge.getConfig().pex_tables_permissionsInheritance + "` WHERE child ='" + player.getUniqueId().toString() + "' AND type='1'");
        while (res.next())
            groups.add(res.getString("parent"));
        return groups;
    }

    @Override
    public String getDefaultGroup() {
        Connection c = BungeePexBridge.getDB().getConnection();
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT name FROM `" + BungeePexBridge.getConfig().pex_tables_permissions + "` WHERE permission = 'default' AND value = 'true'");
            if (res.next())
                return res.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
