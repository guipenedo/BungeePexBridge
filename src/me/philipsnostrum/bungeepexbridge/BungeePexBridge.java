package me.philipsnostrum.bungeepexbridge;

import me.philipsnostrum.bungeepexbridge.commands.BPerms;
import me.philipsnostrum.bungeepexbridge.helpers.Config;
import me.philipsnostrum.bungeepexbridge.helpers.Metrics;
import me.philipsnostrum.bungeepexbridge.helpers.MySQL;
import me.philipsnostrum.bungeepexbridge.listener.PermissionCheckListener;
import me.philipsnostrum.bungeepexbridge.listener.PlayerDisconnectListener;
import me.philipsnostrum.bungeepexbridge.listener.PostLoginListener;
import me.philipsnostrum.bungeepexbridge.models.PermGroup;
import me.philipsnostrum.bungeepexbridge.models.PermPlayer;
import me.philipsnostrum.bungeepexbridge.permsystem.PermissionSystem;
import me.philipsnostrum.bungeepexbridge.permsystem.PermissionsEx;
import me.philipsnostrum.bungeepexbridge.permsystem.SexyPex;
import me.philipsnostrum.bungeepexbridge.permsystem.zPermissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.scheduler.BungeeScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    public static void debug(String msg) {
        if (getConfig().debug)
            instance.getLogger().log(Level.INFO, msg);
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
        } else
            getLogger().log(Level.INFO, "Permission system " + config.permissionsSystem + " loaded successfully!");

        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("permission_plugin", () -> config.permissionsSystem));

        if (permissionSystem.requiresMySQL()) {
            //database
            mysql = new MySQL(config.mysql_hostname, config.mysql_user, config.mysql_pass, config.mysql_db, config.mysql_port);
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
        new BungeeScheduler().schedule(instance, () -> {
            if (mysql.enabled)
                initialize(null);
        }, config.updateInterval, config.updateInterval, TimeUnit.MINUTES);
    }

    private PermissionSystem loadPermissionsSystem() {
        if (config.permissionsSystem.equalsIgnoreCase("pex"))
            return new PermissionsEx();
        else if (config.permissionsSystem.equalsIgnoreCase("sexypex"))
            return new SexyPex();
        else if (config.permissionsSystem.equalsIgnoreCase("ZPERMS"))
            return new zPermissions();
        else return null;
    }

    public void onDisable() {
        if (mysql.enabled)
            mysql.close();
    }

    public void initialize(final CommandSender sender) {
        try {
            getProxy().getScheduler().runAsync(this, () -> {
                TreeMap<String, PermGroup> groups = new TreeMap<>();
                TreeMap<UUID, PermPlayer> players = new TreeMap<>();
                try {
                    for (String group : getPerms().getGroups())
                        groups.put(group, new PermGroup(group));

                    for (PermGroup permGroup : groups.values())
                        setupInheritance(permGroup, groups);

                    PermGroup.setDefaultGroup(null);
                    PermGroup.setPermGroups(groups);

                    String defaultGroupName = permissionSystem.getDefaultGroup();
                    if (defaultGroupName != null)
                        PermGroup.setDefaultGroup(PermGroup.getPermGroup(defaultGroupName));

                    // players
                    for (ProxiedPlayer player : getProxy().getPlayers())
                        players.put(player.getUniqueId(), loadPlayer(player.getUniqueId()));

                    PermPlayer.setPermPlayers(new ConcurrentHashMap<>(players));


                    if (sender != null)
                        sender.sendMessage(new ComponentBuilder("Bungee permissions synced with " + config.permissionsSystem).color(ChatColor.GREEN).create());
                } catch (Exception e) {
                    if (sender != null)
                        sender.sendMessage(new ComponentBuilder("Bungee permissions could not sync with " + config.permissionsSystem).color(ChatColor.RED).create());
                    e.printStackTrace();
                }
            });
        } catch (java.util.concurrent.RejectedExecutionException ignored) {
        }
    }

    private void setupInheritance(PermGroup group, Map<String, PermGroup> groups) {
        try {
            group.setInheritanceSetup();
            for (String groupName : permissionSystem.getInheritance(group.getName())) {
                PermGroup childGroup = groups.get(groupName);
                if (childGroup == null)
                    continue;

                if (!childGroup.isInheritanceSetup())
                    setupInheritance(childGroup, groups);

                //get child permissions and remove ones revoked by this group
                ArrayList<String> permissions = new ArrayList<>(childGroup.getPermissions());
                for (String perm : group.getRevoked())
                    permissions.remove(perm);

                //get child revoked permissions and remove ones given to this group
                ArrayList<String> revoked = new ArrayList<>(childGroup.getRevoked());
                for (String perm : group.getPermissions())
                    revoked.remove("-" + perm);
                group.getPermissions().addAll(permissions);
                group.getRevoked().addAll(revoked);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PermPlayer loadPlayer(UUID uuid) throws Exception {
        ProxiedPlayer player = getProxy().getPlayer(uuid);
        if (player == null)
            throw new NullPointerException("Can't find the player `" + (uuid == null ? "unknown" : uuid.toString()) + "`");
        PermPlayer pplayer = new PermPlayer(player.getUniqueId());
        List<PermGroup> groups = new LinkedList<>();
        for (String group : permissionSystem.getPlayerGroups(player)) {
            PermGroup permGroup = PermGroup.getPermGroup(group);
            if (permGroup != null)
                groups.add(permGroup);
        }
        pplayer.setGroups(groups);
        return pplayer;
    }

    public boolean hasPermission(UUID uuid, String permission, boolean hasPermission) {
        //fixes groups for sexypex - let bungeecord handle it
        if (BungeePexBridge.getConfig().permissionsSystem.equalsIgnoreCase("Sexypex"))
            return hasPermission;
        permission = permission.toLowerCase();
        PermPlayer permPlayer = PermPlayer.getPlayer(uuid);
        if (permPlayer == null) return false;
        if (permPlayer.hasPermission("-" + permission))
            return false;
        if (permPlayer.hasPermission(permission) || permPlayer.hasPermission("*"))
            return true;

        for (PermGroup group : permPlayer.getGroups()) {
            if (group == null) continue;
            if (group.getRevoked().contains(permission)) return false;
            if (group.getPermissions().contains(permission) || group.getPermissions().contains("*")) return true;
        }
        return false;
    }
}

