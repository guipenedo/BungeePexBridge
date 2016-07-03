package me.philipsnostrum.bungeepexbridge.listener;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener{

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        BungeePexBridge.get().loadPlayer(e.getPlayer().getUniqueId());
    }
}
