package me.philipsnostrum.bungeepexbridge.models;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class PermGroup implements Comparable<PermGroup> {
    private static TreeMap<String, PermGroup> permGroups = new TreeMap<>();
    private static PermGroup defaultGroup = null;
    private String name;
    private long rank;
    private boolean inheritanceSetup = false;
    private TreeSet<String> permissions = new TreeSet<>(), revoked = new TreeSet<>();

    public PermGroup(String name) {
        this.name = name;
        loadPermissions(BungeePexBridge.getPerms().getGroupPermissions(name));
        this.rank = BungeePexBridge.getPerms().getRank(name);
    }

    public static void setPermGroups(TreeMap<String, PermGroup> permGroups) {
        PermGroup.permGroups = permGroups;
    }

    public static PermGroup getPermGroup(String name) {
        return permGroups.get(name);
    }

    static PermGroup getDefaultGroup() {
        return defaultGroup;
    }

    public static void setDefaultGroup(PermGroup group) {
        defaultGroup = group;
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

    public TreeSet<String> getRevoked() {
        return revoked;
    }

    public String getName() {
        return name;
    }

    public TreeSet<String> getPermissions() {
        return permissions;
    }

    public boolean isInheritanceSetup() {
        return this.inheritanceSetup;
    }

    public void setInheritanceSetup() {
        this.inheritanceSetup = true;
    }

    @Override
    public int compareTo(PermGroup o) {
        if (rank == o.rank)
            return name.compareTo(o.name);
        return Long.compare(rank, o.rank);
    }

    @Override
    public String toString() {
        return "name=" + name + " rank=" + rank;
    }
}
