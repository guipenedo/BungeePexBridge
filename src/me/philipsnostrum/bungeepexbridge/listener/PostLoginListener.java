package me.philipsnostrum.bungeepexbridge.listener;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import me.philipsnostrum.bungeepexbridge.models.PermPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        try {
            PermPlayer.getPermPlayers().put(e.getPlayer().getUniqueId(), BungeePexBridge.get().loadPlayer(e.getPlayer().getUniqueId()));
        } catch (Exception ex) {
            System.err.println("Can't create permission player for " + e.getPlayer().getName() + "! (" + ex.getMessage() + ")");
            ex.printStackTrace();
        }
    }
}
