package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermGroup {
    public static void setPermGroups(ArrayList<PermGroup> permGroups) {
        PermGroup.permGroups = permGroups;
    }

    private static ArrayList<PermGroup> permGroups = new ArrayList<PermGroup>();
    private String name;
    private ArrayList<String> permissions = new ArrayList<String>(), revoked = new ArrayList<String>(), players = new ArrayList<String>();
    private boolean inheritanceSetup = false;

    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    private boolean defaultGroup = false;

    public PermGroup(String name) {
        this.name = name;
        loadPermissions(BungeePexBridge.getPerms().getGroupPermissions(name));
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

    public static ArrayList<PermGroup> getPlayerGroups(UUID uuid) {
        ArrayList<PermGroup> groups = new ArrayList<PermGroup>();
        for (PermGroup permGroup : getPermGroups())
            if (permGroup.getPlayers().contains(uuid.toString()))
                groups.add(permGroup);
        if (groups.size() == 0 && getDefaultGroup() != null)
            groups.add(getDefaultGroup());
        return groups;
    }

    private void loadPermissions(List<String> permissions) {
        for (String perm : permissions) {
            if (perm.startsWith("-"))
                revoked.add(perm.replace("-", ""));
            else this.permissions.add(perm);
        }
        for (String perm : revoked)
            this.permissions.remove(perm);
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
