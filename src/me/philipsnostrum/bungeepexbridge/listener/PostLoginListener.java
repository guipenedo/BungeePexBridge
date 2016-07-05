package me.philipsnostrum.bungeepexbridge.listener;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import me.philipsnostrum.bungeepexbridge.modules.PermPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener{

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        PermPlayer.getPermPlayers().add(BungeePexBridge.get().loadPlayer(e.getPlayer().getUniqueId()));
    }
}
