package me.philipsnostrum.bungeepexbridge.listener;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import me.philipsnostrum.bungeepexbridge.modules.PermGroup;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

public class PermissionCheckListener implements Listener {

    @EventHandler
    public void onPermissionCheck(PermissionCheckEvent e) {
        if (BungeePexBridge.getDB().enabled && e.getSender() instanceof ProxiedPlayer) {
            if (!e.hasPermission()) {
                e.setHasPermission(BungeePexBridge.get().hasPermission(((ProxiedPlayer) e.getSender()).getUniqueId(), e.getPermission()));
            }

            ArrayList<PermGroup> permGroups = PermGroup.getPlayerGroups(((ProxiedPlayer) e.getSender()).getUniqueId());
            if (e.hasPermission() && permGroups != null && permGroups.size() > 0){
                e.setHasPermission(!permGroups.get(0).getRevoked().contains(e.getPermission()));
            }
        }
    }
}
