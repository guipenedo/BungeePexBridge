package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermPlayer {
    private static ArrayList<PermPlayer> permPlayers = new ArrayList<>();
    private UUID uuid;
    private List<String> permissions = new ArrayList<>();

    public PermPlayer(UUID uuid) throws Exception {
        this.uuid = uuid;
        for (String p : BungeePexBridge.getPerms().getPlayerPermissions(BungeePexBridge.get().getProxy().getPlayer(uuid)))
            this.permissions.add(p.toLowerCase());
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
}
