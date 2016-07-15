package me.philipsnostrum.bungeepexbridge.listener;

import me.philipsnostrum.bungeepexbridge.modules.PermGroup;
import me.philipsnostrum.bungeepexbridge.modules.PermPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnect (PlayerDisconnectEvent e){
        List<PermGroup> permGroups = PermGroup.getPlayerGroups(e.getPlayer().getUniqueId());
        for (PermGroup group : permGroups)
            group.getPlayers().remove(e.getPlayer().getUniqueId().toString());

        PermPlayer permPlayer = PermPlayer.getPlayer(e.getPlayer().getUniqueId());
        if(permPlayer != null)
            PermPlayer.getPermPlayers().remove(permPlayer);

        //clear groups
        e.getPlayer().removeGroups(e.getPlayer().getGroups().toArray(new String[e.getPlayer().getGroups().size()]));
    }
}
