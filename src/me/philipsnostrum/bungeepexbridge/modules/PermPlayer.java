package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.ArrayList;
import java.util.UUID;

public class PermPlayer {
    private static ArrayList<PermPlayer> permPlayers = new ArrayList<>();
    private UUID uuid;
    private ArrayList<String> permissions = new ArrayList<>(), revoked = new ArrayList<>();

    public PermPlayer(UUID uuid) throws Exception {
        this.uuid = uuid;

        for (String permission : BungeePexBridge.getPerms().getPlayerPermissions(BungeePexBridge.get().getProxy().getPlayer(uuid))) {
            String perm = permission.toLowerCase();
            if (perm.startsWith("-"))
                revoked.add(perm.replace("-", ""));
            else this.permissions.add(perm);
        }
        for (String perm : revoked)
            this.permissions.remove(perm);
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
                if (permPlayer.getUuid().toString().replace("-", "").equalsIgnoreCase(uuid.toString().replace("-", "")))
                    return permPlayer;
        return null;
    }

    private UUID getUuid() {
        return uuid;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    public ArrayList<String> getPermissions() {
        return permissions;
    }
    
    public ArrayList<String> getRevoked() {
        return revoked;
    }
}
