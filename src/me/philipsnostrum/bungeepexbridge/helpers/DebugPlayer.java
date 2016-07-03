package me.philipsnostrum.bungeepexbridge.helpers;

import com.google.gson.Gson;
import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import me.philipsnostrum.bungeepexbridge.modules.PermGroup;
import me.philipsnostrum.bungeepexbridge.modules.PermPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DebugPlayer {
    public static void debug(ProxiedPlayer player){
        PermPlayer permPlayer = PermPlayer.getPlayer(player.getUniqueId());
        PermGroup permGroup = PermGroup.getPlayerGroup(player.getUniqueId());

        Gson gson = new Gson();

        File file = new File(BungeePexBridge.get().getDataFolder().getAbsolutePath() + File.separator + "debug", player.getDisplayName() + ".yml");
        System.out.println("Created file at: " + file.getAbsolutePath());

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("player:\n");
            if (permPlayer != null)
                fileWriter.write(gson.toJson(permPlayer));
            fileWriter.write("\ngroups:\n");
            if (permGroup != null)
                fileWriter.write(gson.toJson(permGroup) + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
