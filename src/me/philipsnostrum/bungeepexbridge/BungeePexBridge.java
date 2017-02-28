package me.philipsnostrum.bungeepexbridge;

import me.philipsnostrum.bungeepexbridge.commands.BPerms;
import me.philipsnostrum.bungeepexbridge.helpers.MySQL;
import me.philipsnostrum.bungeepexbridge.helpers.Config;
import me.philipsnostrum.bungeepexbridge.listener.PermissionCheckListener;
import me.philipsnostrum.bungeepexbridge.listener.PlayerDisconnectListener;
import me.philipsnostrum.bungeepexbridge.listener.PostLoginListener;
import me.philipsnostrum.bungeepexbridge.modules.PermGroup;
import me.philipsnostrum.bungeepexbridge.modules.PermPlayer;
import me.philipsnostrum.bungeepexbridge.permsystem.PermissionSystem;
import me.philipsnostrum.bungeepexbridge.permsystem.PermissionsEx;
import me.philipsnostrum.bungeepexbridge.permsystem.SexyPex;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.scheduler.BungeeScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BungeePexBridge extends Plugin {
    private static BungeePexBridge instance;
    private PermissionSystem permissionSystem;
    private Config config;
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

    public static PermissionSystem getPerms() {
        return instance.permissionSystem;
    }

    public void onEnable() {
        instance = this;

        //config
        config = new Config();

        //load permissionssystem
        permissionSystem = loadPermissionsSystem();
        if (permissionSystem == null) {
            getLogger().log(Level.SEVERE, "Disabling plugin! Permission system " + config.permissionsSystem + " not found! Check the plugin page for configuration help.");
            return;
        }else
            getLogger().log(Level.INFO, "Permission system " + config.permissionsSystem + " loaded successfully!");


        if (permissionSystem.requiresMySQL()) {
            //database
            mysql = new MySQL(config.mysql_hostname, config.mysql_user, config.mysql_pass, config.mysql_db, config.mysql_port, config.mysql_usessl.equals("true"));
            if (!mysql.enabled) {
                getLogger().log(Level.SEVERE, "Disabling plugin! Permissions System requires a MySQL connection and one could not be established!");
                return;
            }
        }

        //commands
        getProxy().getPluginManager().registerCommand(this, new BPerms());

        //event listeners
        getProxy().getPluginManager().registerListener(this, new PermissionCheckListener());
        getProxy().getPluginManager().registerListener(this, new PostLoginListener());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener());

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

    private PermissionSystem loadPermissionsSystem() {
        if (config.permissionsSystem.equals("PEX"))
            return new PermissionsEx();
        else if (config.permissionsSystem.equals("SEXYPEX"))
            return new SexyPex();
        else return null;
    }

    public void onDisable() {
        if (mysql.enabled)
            mysql.close();
    }

    public void initialize(final CommandSender sender) {
        try {
            getProxy().getScheduler().runAsync(this, new Runnable() {
                @Override
                public void run() {
                    ArrayList<PermGroup> groups = new ArrayList<PermGroup>();
                    ArrayList<PermPlayer> players = new ArrayList<PermPlayer>();
                    try {
                        for (String group : getPerms().getGroups())
                            groups.add(new PermGroup(group));

                        //sort based on rank if available
                        Collections.sort(groups);

                        PermGroup.setPermGroups(groups);


                        for (PermGroup permGroup : groups) {
                            setupInheritance(permGroup);
                        }

                        String defaultGroupName = permissionSystem.getDefaultGroup();
                        if (defaultGroupName != null) {
                            PermGroup defaultGroup = PermGroup.getPermGroup(defaultGroupName);
                            if (defaultGroup != null)
                                defaultGroup.setDefaultGroup(true);
                        }

                        for (ProxiedPlayer player : getProxy().getPlayers())
                            players.add(loadPlayer(player.getUniqueId()));
                        if (sender != null)
                            sender.sendMessage(new ComponentBuilder("Bungee permissions synced with " + config.permissionsSystem).color(ChatColor.GREEN).create());
                    } catch (Exception e) {
                        if (sender != null)
                            sender.sendMessage(new ComponentBuilder("Bungee permissions could not sync with " + config.permissionsSystem).color(ChatColor.RED).create());
                        e.printStackTrace();
                    }
                    PermPlayer.setPermPlayers(players);
                }
            });
        }catch (java.util.concurrent.RejectedExecutionException ignored){}
    }

    public void setupInheritance(PermGroup group) {
        try {
            group.setInheritanceSetup(true);
            for (String groupName : permissionSystem.getInheritance(group.getName())) {
                PermGroup childGroup = PermGroup.getPermGroup(groupName);
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
                group.getPermissions().addAll(permissions);
                group.getRevoked().addAll(revoked);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PermPlayer loadPlayer(UUID uuid) {
        try {
            ProxiedPlayer player = getProxy().getPlayer(uuid);
            for (String group : permissionSystem.getPlayerGroups(player)){
                PermGroup permGroup = PermGroup.getPermGroup(group);
                if (permGroup != null)
                    permGroup.getPlayers().add(uuid.toString());
            }
            return new PermPlayer(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasPermission(UUID uuid, String permission) {
        PermPlayer permPlayer = PermPlayer.getPlayer(uuid);
        if (permPlayer != null && permPlayer.hasPermission("-" + permission))
            return false;
        if (permPlayer != null && (permPlayer.hasPermission(permission) || permPlayer.hasPermission("*")))
            return true;
        ArrayList<PermGroup> permGroups = PermGroup.getPlayerGroups(uuid);
        for (PermGroup group : permGroups){
            if (group == null) continue;
            if (group.getRevoked().contains(permission)) return false;
            if (group.getPermissions().contains(permission) || group.getPermissions().contains("*")) return true;
        }
        return false;
    }
}

