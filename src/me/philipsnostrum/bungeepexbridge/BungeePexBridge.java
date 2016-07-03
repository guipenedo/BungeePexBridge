package me.philipsnostrum.bungeepexbridge;

import me.philipsnostrum.bungeepexbridge.commands.BPerms;
import me.philipsnostrum.bungeepexbridge.helpers.Config;
import me.philipsnostrum.bungeepexbridge.helpers.MySQL;
import me.philipsnostrum.bungeepexbridge.listener.PermissionCheckListener;
import me.philipsnostrum.bungeepexbridge.listener.PlayerDisconnectListener;
import me.philipsnostrum.bungeepexbridge.listener.PostLoginListener;
import me.philipsnostrum.bungeepexbridge.modules.PermGroup;
import me.philipsnostrum.bungeepexbridge.modules.PermPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.scheduler.BungeeScheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeePexBridge extends Plugin {
    private static BungeePexBridge instance;
    public Config config;
    private MySQL mysql;

    public static BungeePexBridge get() {
        return instance;
    }

    public static Config getConfig() {
        return instance.config;
    }

    public static MySQL getDB() {
        return instance.mysql;
    }

    public void onEnable() {
        instance = this;

        //config
        config = new Config();

        //commands
        getProxy().getPluginManager().registerCommand(this, new BPerms());

        //event listeners
        getProxy().getPluginManager().registerListener(this, new PermissionCheckListener());
        getProxy().getPluginManager().registerListener(this, new PostLoginListener());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener());

        //database
        mysql = new MySQL();

        initialize(null);

        //update every x minutes
        new BungeeScheduler().schedule(instance, new Runnable() {
            @Override
            public void run() {
                if (mysql.enabled)
                    initialize(null);
            }
        }, config.updateInterval, config.updateInterval, TimeUnit.MINUTES);
    }

    public void onDisable() {
        if (mysql.enabled)
            mysql.closeConnection();
    }

    public void initialize(final CommandSender sender) {
        getProxy().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                PermGroup.getPermGroups().clear();
                PermPlayer.getPermPlayers().clear();
                if (getDB().enabled && !config.sexypex) {
                    try {
                        Connection c = getDB().getCon();
                        ResultSet res = c.createStatement().executeQuery("SELECT DISTINCT(name) as name FROM `" + config.mysql_tableNames_permissions + "` WHERE name NOT LIKE 'system' AND name NOT LIKE '%-%' AND type='0'");

                        while (res.next())
                            PermGroup.getPermGroups().add(new PermGroup(res.getString("name")));

                        for (PermGroup permGroup : PermGroup.getPermGroups()) {
                            setupInheritance(permGroup);
                        }
                        for (ProxiedPlayer player : getProxy().getPlayers())
                            loadPlayer(player.getUniqueId());
                        if (sender != null)
                            sender.sendMessage(new ComponentBuilder("Bungee permissions synced with PEX database!").color(ChatColor.GREEN).create());
                    } catch (SQLException e) {
                        if (sender != null)
                            sender.sendMessage(new ComponentBuilder("Bungee permissions could not sync with PEX database!").color(ChatColor.RED).create());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setupInheritance(PermGroup group) {
        if (getDB().enabled) {
            Connection c = getDB().getCon();
            group.setInheritanceSetup(true);
            try {
                ResultSet res = c.createStatement().executeQuery("SELECT parent FROM `" + config.mysql_tableNames_permissionsInheritance + "` WHERE child = '" + group.getName() + "' AND type='0'");
                while (res.next()) {
                    PermGroup childGroup = PermGroup.getPermGroup(res.getString("parent"));
                    if (childGroup == null)
                        continue;
                    if (!childGroup.isInheritanceSetup())
                        setupInheritance(childGroup);
                    //get child permissions and remove ones revoked by this group
                    ArrayList<String> permissions = new ArrayList<String>();
                    permissions.addAll(childGroup.getPermissions());
                    for (String perm : group.getRevoked())
                        permissions.remove(perm);
                    //get child revoked permissions and remove ones given to this group
                    ArrayList<String> revoked = new ArrayList<String>();
                    revoked.addAll(childGroup.getRevoked());
                    for (String perm : group.getPermissions())
                        revoked.remove("-" + perm);
                    group.setPermissions(permissions);
                    group.setRevoked(revoked);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPlayer(UUID uuid) {
        if (getDB().enabled) {
            Connection c = getDB().getCon();
            try {
                ProxiedPlayer player = getProxy().getPlayer(uuid);
                ResultSet res = c.createStatement().executeQuery("SELECT * FROM `" + config.mysql_tableNames_permissions + "` WHERE name ='" + uuid.toString() + "' AND type='"+(config.sexypex ? "0" : "1")+"' AND permission NOT LIKE 'name' AND permission NOT LIKE 'prefix'");
                if (res.next())
                    PermPlayer.getPermPlayers().add(new PermPlayer(uuid));
                res = c.createStatement().executeQuery("SELECT parent FROM `" + config.mysql_tableNames_permissionsInheritance + "` WHERE child ='" + uuid.toString() + "' AND type='"+(config.sexypex ? "0" : "1")+"'");
                if (config.sexypex) {
                    player.removeGroups(player.getGroups().toArray(new String[player.getGroups().size()]));
                    while (res.next()) {
                        player.addGroups(res.getString("parent"));
                    }
                } else if (res.next()) {
                    PermGroup permGroup = PermGroup.getPermGroup(res.getString("parent"));
                    if (permGroup != null)
                        permGroup.getPlayers().add(uuid.toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasPermission(UUID uuid, String permission) {
        PermPlayer permPlayer = PermPlayer.getPlayer(uuid);
        if (permPlayer != null && (permPlayer.hasPermission(permission) || permPlayer.hasPermission("*")))
            return true;
        PermGroup permGroup = PermGroup.getPlayerGroup(uuid);
        return permGroup != null && (permGroup.hasPermission(permission) || permGroup.hasPermission("*"));
    }
}

