package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermPlayer {
    private static ArrayList<PermPlayer> permPlayers = new ArrayList<>();
    private UUID uuid;
    private List<String> permissions;

    public PermPlayer(UUID uuid) {
        this.uuid = uuid;
        try {
            permissions = BungeePexBridge.getPerms().getPlayerPermissions(BungeePexBridge.get().getProxy().getPlayer(uuid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<PermPlayer> getPermPlayers() {
        return permPlayers;
    }

    public static void setPermPlayers(ArrayList<PermPlayer> permPlayers) {
        PermPlayer.permPlayers = permPlayers;
    }

    public static PermPlayer getPlayer(UUID uuid) {
        if (uuid == null)
            throw new NullPointerException("Invalid uuid!");
        for (PermPlayer permPlayer : getPermPlayers())
            if (permPlayer != null) //Funny why can this be null
                if (permPlayer.getUuid().toString().equalsIgnoreCase(uuid.toString()))
                    return permPlayer;
        return null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
