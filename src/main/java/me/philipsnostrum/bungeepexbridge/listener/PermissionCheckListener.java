package me.philipsnostrum.bungeepexbridge.listener;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionCheckListener implements Listener {

    @EventHandler
    public void onPermissionCheck(PermissionCheckEvent e) {
        if (BungeePexBridge.getDB().enabled && e.getSender() instanceof ProxiedPlayer) {
            e.setHasPermission(BungeePexBridge.get().hasPermission(((ProxiedPlayer) e.getSender()).getUniqueId(), e.getPermission()));
        }
    }
}
