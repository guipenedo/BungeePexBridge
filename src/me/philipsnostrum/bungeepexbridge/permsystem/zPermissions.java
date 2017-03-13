package me.philipsnostrum.bungeepexbridge.permsystem;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class zPermissions implements PermissionSystem{

    @Override
    public boolean requiresMySQL() {
        return true;
    }

    @Override
    public List<String> getGroups() throws SQLException {
        Connection c = BungeePexBridge.getDB().getConnection();
        ResultSet res = c.createStatement().executeQuery("SELECT DISTINCT(id) as id FROM `" + BungeePexBridge.getConfig().zperms_tables_entities + "` WHERE is_group= '1'");
        return BungeePexBridge.getDB().resultSetToList(res, "id");
    }

    @Override
    public List<String> getGroupPermissions(String group) {
        List<String> perms = new ArrayList<>();
        Connection c = BungeePexBridge.getDB().getConnection();
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().zperms_tables_entries + "` WHERE entity_id = '" + Integer.parseInt(group)+"'");

            while (res.next()) {
                if (Arrays.asList("rank", "prefix", "default").contains(res.getString("permission")))
                    continue;
                if(res.getInt("value") == 1) {
                    perms.add(res.getString("permission"));
                }else {
                    perms.add("-" + res.getString("permission"));
                }
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
            ResultSet res = c.createStatement().executeQuery("SELECT parent_id FROM `" + BungeePexBridge.getConfig().zperms_tables_permissionsInheritance + "` WHERE child_id = '" + Integer.parseInt(group)+"'");
            return BungeePexBridge.getDB().resultSetToList(res, "parent_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public long getRank(String group) {
        Connection c = BungeePexBridge.getDB().getConnection();
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT priority FROM `" + BungeePexBridge.getConfig().zperms_tables_entities + "` WHERE id = '" + Integer.parseInt(group) + "' AND is_group = '1'");
            if (res.next())
                return Long.parseLong(res.getString("priority"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> getPlayerPermissions(ProxiedPlayer player) throws SQLException {
        Connection c = BungeePexBridge.getDB().getConnection();
        List<String> permissions = new ArrayList<>();
        int entity_id = 0;
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().zperms_tables_entities + "` WHERE name = '" + player.getUniqueId().toString().replace("-","") + "'");
            if(res.next()) {
                entity_id = res.getInt("id");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().zperms_tables_entries + "` WHERE entity_id = '" + entity_id + "'");
            while (res.next()) {
                if (Arrays.asList("rank", "prefix", "name").contains(res.getString("permission")))
                    continue;
                if(res.getInt("value") == 1) {
                    permissions.add(res.getString("permission"));
                }else {
                    permissions.add("-" + res.getString("permission"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    @Override
    public List<String> getPlayerGroups(ProxiedPlayer player) throws SQLException {
        Connection c = BungeePexBridge.getDB().getConnection();
        List<String> groups = new ArrayList<>();
        ResultSet res = c.createStatement().executeQuery("SELECT group_id FROM `" + BungeePexBridge.getConfig().zperms_tables_memberships + "` WHERE member = '" + player.getUniqueId().toString().replace("-","") + "'");
        while (res.next())
            groups.add(res.getString("group_id"));
        return groups;
    }

    @Override
    public String getDefaultGroup() {
        return BungeePexBridge.getConfig().configuration.getString("def-group");
    }
}
