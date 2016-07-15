package me.philipsnostrum.bungeepexbridge.helpers;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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

        Field[] fields = getClass().getDeclaredFields();
        for(Field f : fields) {
            f.setAccessible(true);
            try {
                /*String path = f.getName().replaceAll("_", ".");
                System.out.println("Path: " + path);
                System.out.println("default value: " + f.get(this));
                Object obj = configuration.get(path, f.get(this));
                f.set(this, obj);
                System.out.println("new value: " + f.get(this));*/
                f.set(this, configuration.get(f.getName().replaceAll("_", "."), f.get(this)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //mysql
    public String mysql_hostname = "localhost";
    public String mysql_user = "root";
    public String mysql_pass = "";
    public String mysql_db = "database";
    public String mysql_port = "3306";
    //permissions system
    public String permissionsSystem = "PEX";
    //update interval
    public int updateInterval = 120;
    //permissions systems settings
    //PEX
    public String pex_tables_permissions = "permissions";
    public String pex_tables_permissionsEntity = "permissions_entity";
    public String pex_tables_permissionsInheritance = "permissions_inheritance";
    //SEXYPEX
    public String sexypex_tables_permissions = "permissions";
    public String sexypex_tables_permissionsInheritance = "permissions_inheritance";
}
