package me.philipsnostrum.bungeepexbridge.modules;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PermPlayer {
    private static ArrayList<PermPlayer> permPlayers = new ArrayList<PermPlayer>();
    private UUID uuid;
    private ArrayList<String> permissions = new ArrayList<String>();

    public static ArrayList<PermPlayer> getPermPlayers() {
        return permPlayers;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PermPlayer (UUID uuid){
        this.uuid = uuid;
        if (BungeePexBridge.getDB().enabled){
            try{
                Connection c = BungeePexBridge.getDB().getCon();
                ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + BungeePexBridge.getConfig().mysql_tableNames_permissions + "` WHERE name = '" + uuid.toString() + "'");
                while(res.next()){
                    if(Arrays.asList("rank", "prefix", "name").contains(res.getString("permission")))
                        continue;
                    permissions.add(res.getString("permission"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static PermPlayer getPlayer(UUID uuid){
        for(PermPlayer permPlayer : getPermPlayers())
            if(permPlayer.getUuid().toString().equalsIgnoreCase(uuid.toString()))
                return permPlayer;
        return null;
    }

    public boolean hasPermission(String permission){
        return permissions.contains(permission);
    }
}
