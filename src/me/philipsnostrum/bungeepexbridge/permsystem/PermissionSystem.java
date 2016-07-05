package me.philipsnostrum.bungeepexbridge.permsystem;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public interface PermissionSystem {

    /**
     * Whether or not this permission system requires MySQL to be enabled
     */
    boolean requiresMySQL();

    /**
     * Retrieves all existent groups from the permissions system
     *
     * @return list of group names
     */
    List<String> getGroups() throws Exception;

    /**
     * Retrieves a group's permissions
     *
     * @param group Permissions group name
     * @return list of group permissions
     */
    List<String> getGroupPermissions(String group);

    /**
     * Retrieves all groups specified group inherits from
     *
     * @param group Permissions group name
     * @return list of groups this group inherits
     */
    List<String> getInheritance(String group) throws Exception;

    /**
     * Retrieves player's individual permissions
     *
     * @param player UUID of player
     * @return list of player's permissions
     */
    List<String> getPlayerPermissions(ProxiedPlayer player) throws Exception;

    /**
     * Retrieves all groups the player belongs to (global, not world based)
     *
     * @param player UUID of player
     * @return list of player's groups
     */
    List<String> getPlayerGroups(ProxiedPlayer player) throws Exception;

    /**
     * Retrieves the name of the default group
     *
     * @return name of the default group
     */
    String getDefaultGroup() throws Exception;
}
