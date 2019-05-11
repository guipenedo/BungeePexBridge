package me.philipsnostrum.bungeepexbridge.models;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PermPlayer {
    private static ConcurrentHashMap<UUID, PermPlayer> permPlayers = new ConcurrentHashMap<>();
    private List<PermGroup> groups;
    private UUID uuid;
    private Set<String> permissions = new TreeSet<>();

    public PermPlayer(UUID uuid) throws Exception {
        this.uuid = uuid;
        for (String p : BungeePexBridge.getPerms().getPlayerPermissions(BungeePexBridge.get().getProxy().getPlayer(uuid)))
            this.permissions.add(p.toLowerCase());
    }

    public static ConcurrentHashMap<UUID, PermPlayer> getPermPlayers() {
        return permPlayers;
    }

    public static void setPermPlayers(ConcurrentHashMap<UUID, PermPlayer> permPlayers) {
        PermPlayer.permPlayers = permPlayers;
    }

    public static PermPlayer getPlayer(UUID uuid) {
        if (uuid == null)
            throw new NullPointerException("Invalid uuid!");
        return permPlayers.get(uuid);
    }

    public List<PermGroup> getGroups() {
        if (groups.isEmpty() && PermGroup.getDefaultGroup() != null)
            return Collections.singletonList(PermGroup.getDefaultGroup());
        return groups;
    }

    public void setGroups(List<PermGroup> groups) {
        this.groups = groups;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
