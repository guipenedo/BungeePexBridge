package me.philipsnostrum.bungeepexbridge.helpers;

import com.google.gson.Gson;
import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import me.philipsnostrum.bungeepexbridge.models.PermPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DebugPlayer {
    public static void debug(ProxiedPlayer player, CommandSender sender) {
        PermPlayer permPlayer = PermPlayer.getPlayer(player.getUniqueId());

        Gson gson = new Gson();

        File file = new File(BungeePexBridge.get().getDataFolder().getAbsolutePath() + File.separator + "debug", player.getDisplayName() + ".yml");
        sender.sendMessage(new ComponentBuilder("Created file at: " + file.getAbsolutePath()).color(ChatColor.GREEN).create());

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("player:\n");
            fileWriter.write(gson.toJson(permPlayer));
            if (BungeePexBridge.getConfig().permissionsSystem.equalsIgnoreCase("Sexypex")) {
                fileWriter.write("\ngroups:\n");
                fileWriter.write(player.getGroups() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
