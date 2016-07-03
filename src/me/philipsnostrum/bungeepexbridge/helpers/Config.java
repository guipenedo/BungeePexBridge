package me.philipsnostrum.bungeepexbridge.helpers;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Config {
    public Configuration configuration;

    public void loadConfig() {
        try {
            if (!BungeePexBridge.get().getDataFolder().exists())
                BungeePexBridge.get().getDataFolder().mkdir();

            File file = new File(BungeePexBridge.get().getDataFolder(), "config.yml");

            if (!file.exists())
                Files.copy(BungeePexBridge.get().getResourceAsStream("config.yml"), file.toPath());

            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(BungeePexBridge.get().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config(){
        loadConfig();
        mysql_hostname = configuration.getString("mysql.hostname", mysql_hostname);
        mysql_user = configuration.getString("mysql.user", mysql_user);
        mysql_pass = configuration.getString("mysql.pass", mysql_pass);
        mysql_db = configuration.getString("mysql.db", mysql_db);

        mysql_port = configuration.getString("mysql.port", mysql_port);
        mysql_tableNames_permissions = configuration.getString("mysql.tableNames.permissions", mysql_tableNames_permissions);
        mysql_tableNames_permissionsInheritance = configuration.getString("mysql.tableNames.permissions_inheritance", mysql_tableNames_permissionsInheritance);
        mysql_tableNames_permissionsEntity = configuration.getString("mysql.tableNames.permissions_entity", mysql_tableNames_permissionsEntity);
        updateInterval = configuration.getInt("updateInterval", updateInterval);
        sexypex = configuration.getBoolean("sexypex", sexypex);
    }

    public String mysql_hostname = "localhost";
    public String mysql_user = "root";
    public String mysql_pass = "";
    public String mysql_db = "database";
    public String mysql_port = "3306";
    public String mysql_tableNames_permissions = "permissions";
    public String mysql_tableNames_permissionsInheritance = "permissions_inheritance";
    public String mysql_tableNames_permissionsEntity = "permissions_entity";
    public boolean sexypex = false;
    public int updateInterval = 120;
}
