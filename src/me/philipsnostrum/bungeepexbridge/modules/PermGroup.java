package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PermGroup {
    private static ArrayList<PermGroup> permGroups = new ArrayList<PermGroup>();
    private String name;
    private ArrayList<String> permissions = new ArrayList<String>(), revoked = new ArrayList<String>(), players = new ArrayList<String>();
    private boolean inheritanceSetup = false, defaultGroup = false;

    public PermGroup(String name) {
        this.name = name;
        if (!BungeePexBridge.getConfig().sexypex)
            if (BungeePexBridge.getDB().enabled) {
                try {
                    List<String> perms = new ArrayList<String>();
                    Connection c = BungeePexBridge.getDB().getCon();
                    ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().mysql_tableNames_permissions + "` WHERE name = '" + name + "'");
                    while (res.next()) {
                        if (res.getString("permission").equalsIgnoreCase("default")) {
                            defaultGroup = res.getString("value").equals("true");
                            continue;
                        }

                        if (Arrays.asList("rank", "prefix").contains(res.getString("permission")))
                            continue;
                        perms.add(res.getString("permission"));
                    }
                    loadPermissions(perms);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        else{
                //load stuff from bungeeconfig
                if (name.equals("default"))
                    defaultGroup = true;
                if (BungeePexBridge.getBungeeConfig().getStringList("permissions." + name) != null)
                    loadPermissions(BungeePexBridge.getBungeeConfig().getStringList("permissions." + name));
            }
    }

    private void loadPermissions (List<String> permissions){
        for (String perm : permissions) {
            if (perm.startsWith("-"))
                revoked.add(perm.replace("-", ""));
            else permissions.add(perm);
        }
        for (String perm : revoked)
            permissions.remove(perm);
    }

    public static ArrayList<PermGroup> getPermGroups() {
        return permGroups;
    }

    public static PermGroup getPermGroup(String name) {
        for (PermGroup permGroup : getPermGroups())
            if (permGroup.getName().equalsIgnoreCase(name))
                return permGroup;
        return null;
    }

    public static PermGroup getDefaultGroup() {
        for (PermGroup permGroup : getPermGroups())
            if (permGroup.isDefaultGroup())
                return permGroup;
        return null;
    }

    public static PermGroup getPlayerGroup(UUID uuid) {
        for (PermGroup permGroup : getPermGroups())
            if (permGroup.getPlayers().contains(uuid.toString()))
                return permGroup;
        return getDefaultGroup();
    }

    public ArrayList<String> getRevoked() {
        return revoked;
    }

    public void setRevoked(ArrayList<String> revoked) {
        this.revoked = revoked;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean isInheritanceSetup() {
        return this.inheritanceSetup;
    }

    public void setInheritanceSetup(boolean inheritanceSetup) {
        this.inheritanceSetup = inheritanceSetup;
    }

}
