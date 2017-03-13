package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.BungeeCord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermPlayer {
    public static void setPermPlayers(ArrayList<PermPlayer> permPlayers) {
        PermPlayer.permPlayers = permPlayers;
    }

    private static ArrayList<PermPlayer> permPlayers = new ArrayList<>();
    private UUID uuid;
    private List<String> permissions;

    public static ArrayList<PermPlayer> getPermPlayers() {
        return permPlayers;
    }

    private UUID getUuid() {
        return uuid;
    }

    public PermPlayer (UUID uuid){
        this.uuid = uuid;
        try {
            permissions = BungeePexBridge.getPerms().getPlayerPermissions(BungeePexBridge.get().getProxy().getPlayer(uuid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PermPlayer getPlayer(UUID uuid){
        if(uuid == null) {
            BungeeCord.getInstance().broadcast("DAPKIN IS STUPID");
        }
        for(PermPlayer permPlayer : getPermPlayers())
            if(permPlayer.getUuid().toString().replace("-","").equalsIgnoreCase(uuid.toString().replace("-",""))) {
                return permPlayer;
            }
        return null;
    }

    public boolean hasPermission(String permission){
        return permissions.contains(permission);
    }
}
