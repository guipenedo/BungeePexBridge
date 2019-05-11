package me.philipsnostrum.bungeepexbridge.listener;

import me.philipsnostrum.bungeepexbridge.models.PermPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        PermPlayer.getPermPlayers().remove(e.getPlayer().getUniqueId());
        //clear groups
        e.getPlayer().removeGroups(e.getPlayer().getGroups().toArray(new String[0]));
    }
}
