package me.philipsnostrum.bungeepexbridge.commands;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import me.philipsnostrum.bungeepexbridge.helpers.DebugPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BPerms extends Command {
    public BPerms() {
        super("bpb", "bungeepexbridge.reload", "bungeepexbridge", "bpexb", "bpexbridge");
    }

	@Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 2 && strings[0].equalsIgnoreCase("debug")) {
            ProxiedPlayer player = BungeePexBridge.get().getProxy().getPlayer(strings[1]);
            if (player == null) {
                commandSender.sendMessage(new ComponentBuilder("Player not found - must be online!").color(ChatColor.RED).create());
                return;
            }
            DebugPlayer.debug(player, commandSender);
        } else
            BungeePexBridge.get().initialize(commandSender);
    }
}
