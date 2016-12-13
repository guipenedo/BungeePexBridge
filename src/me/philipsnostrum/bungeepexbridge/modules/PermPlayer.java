package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermPlayer {
    public static void setPermPlayers(ArrayList<PermPlayer> permPlayers) {
        PermPlayer.permPlayers = permPlayers;
    }

    private static ArrayList<PermPlayer> permPlayers = new ArrayList<PermPlayer>();
    private UUID uuid;
    private List<String> permissions;

    public static ArrayList<PermPlayer> getPermPlayers() {
        return permPlayers;
    }

    public UUID getUuid() {
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
    	if(uuid == null)
    		throw new NullPointerException("Invalid uuid!");
        for(PermPlayer permPlayer : getPermPlayers())
        	if(permPlayer != null) //Funny why can this be null
	            if(permPlayer.getUuid().toString().equalsIgnoreCase(uuid.toString()))
	                return permPlayer;
        return null;
    }

    public boolean hasPermission(String permission){
        return permissions.contains(permission);
    }
}
