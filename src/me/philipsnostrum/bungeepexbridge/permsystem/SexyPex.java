package me.philipsnostrum.bungeepexbridge.permsystem;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SexyPex implements PermissionSystem {

    @Override
    public boolean requiresMySQL() {
        return true;
    }

    //No need to get groups as we let bungeecord handle permissions
    @Override
    public List<String> getGroups() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getGroupPermissions(String group) {
        return new ArrayList<>();
    }

    //No support for this yet
    @Override
    public List<String> getInheritance(String group) {
        return new ArrayList<>();
    }

    @Override
    public long getRank(String group) {
        return 0;
    }

    //same as PermissionsEx
    @Override
    public List<String> getPlayerPermissions(ProxiedPlayer player) {
        List<String> permissions = new ArrayList<>();
        try {
            Connection c = BungeePexBridge.getDB().getNextConnection();
            ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().sexypex_tables_permissions + "` WHERE name = '" + player.getUniqueId().toString() + "'");
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

    //No need to return groups as we let bungeecord handle permissions. Simply add player to his groups
    @Override
    public List<String> getPlayerGroups(ProxiedPlayer player) {
        //remove player groups
        player.removeGroups(player.getGroups().toArray(new String[player.getGroups().size()]));
        try {
            Connection c = BungeePexBridge.getDB().getNextConnection();
            ResultSet res = c.createStatement().executeQuery("SELECT parent FROM `" + BungeePexBridge.getConfig().sexypex_tables_permissionsInheritance + "` WHERE child ='" + player.getUniqueId().toString() + "' AND type='0'");
            while (res.next()) {
                BungeePexBridge.debug("Group: " + res.getString("parent"));
                player.addGroups(res.getString("parent"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    //No need to get groups as we let bungeecord handle permissions
    @Override
    public String getDefaultGroup() {
        return null;
    }
}
