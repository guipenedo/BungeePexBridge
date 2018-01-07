package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermGroup implements Comparable<PermGroup> {
    private static ArrayList<PermGroup> permGroups = new ArrayList<>();
    private String name;
    private long rank;
    private ArrayList<String> permissions = new ArrayList<>(), revoked = new ArrayList<>(), players = new ArrayList<>();
    private boolean inheritanceSetup = false;
    private boolean defaultGroup = false;

    public PermGroup(String name) {
        this.name = name;
        loadPermissions(BungeePexBridge.getPerms().getGroupPermissions(name));
        this.rank = BungeePexBridge.getPerms().getRank(name);
    }

    private static ArrayList<PermGroup> getPermGroups() {
        return permGroups;
    }

    public static void setPermGroups(ArrayList<PermGroup> permGroups) {
        PermGroup.permGroups = permGroups;
    }

    public static PermGroup getPermGroup(String name) {
        for (PermGroup permGroup : getPermGroups())
            if (permGroup.getName().equalsIgnoreCase(name))
                return permGroup;
        return null;
    }

    private static PermGroup getDefaultGroup() {
        for (PermGroup permGroup : getPermGroups())
            if (permGroup.isDefaultGroup())
                return permGroup;
        return null;
    }

    public static ArrayList<PermGroup> getPlayerGroups(UUID uuid) {
        ArrayList<PermGroup> groups = new ArrayList<>();
        for (PermGroup permGroup : getPermGroups())
            if (permGroup.getPlayers().contains(uuid.toString()))
                groups.add(permGroup);
        if (groups.size() == 0 && getDefaultGroup() != null)
            groups.add(getDefaultGroup());
        return groups;
    }

    public long getRank() {
        return rank;
    }

    private void loadPermissions(List<String> permissions) {
        for (String permission : permissions) {
            String perm = permission.toLowerCase();
            if (perm.startsWith("-"))
                revoked.add(perm.replace("-", ""));
            else this.permissions.add(perm);
        }
        for (String perm : revoked)
            this.permissions.remove(perm);
    }


    private boolean isDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup() {
        this.defaultGroup = true;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public boolean isInheritanceSetup() {
        return this.inheritanceSetup;
    }

    public void setInheritanceSetup() {
        this.inheritanceSetup = true;
    }

    @Override
    public int compareTo(PermGroup o) {
        if (o.rank == rank)
            return 1;
        return Long.compare(rank, o.rank);
    }

    @Override
    public String toString() {
        return "name=" + name + " rank=" + rank;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public ArrayList<String> getRevoked() {
        return revoked;
    }
}
