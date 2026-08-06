package com.witchcraft.coven;

import com.witchcraft.Witchcraft;
import com.witchcraft.api.events.WitchCovenCreateEvent;
import com.witchcraft.api.events.WitchCovenJoinEvent;
import com.witchcraft.api.events.WitchCovenLeaveEvent;
import com.witchcraft.data.CovenData;
import com.witchcraft.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all coven operations.
 */
public class CovenManager {

    private final Witchcraft plugin;
    private final Map<UUID, CovenData> covens = new ConcurrentHashMap<>();

    public CovenManager(Witchcraft plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a new coven.
     *
     * @param leader the leader
     * @param name   the coven name
     * @return the created coven, or null if player is already in one
     */
    public CovenData createCoven(Player leader, String name) {
        PlayerData data = plugin.getDataManager().getPlayerData(leader.getUniqueId());
        if (data.isInCoven()) {
            return null;
        }

        UUID covenId = UUID.randomUUID();
        CovenData coven = new CovenData(covenId, name, leader.getUniqueId());
        covens.put(covenId, coven);
        data.setCovenId(covenId);

        WitchCovenCreateEvent event = new WitchCovenCreateEvent(leader, coven, name);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            covens.remove(covenId);
            data.setCovenId(null);
            return null;
        }

        return coven;
    }

    /**
     * Invites a player to a coven.
     *
     * @param inviter the inviter
     * @param target  the player to invite
     * @return true if invite was sent
     */
    public boolean inviteToCoven(Player inviter, Player target) {
        CovenData coven = getCovenForMember(inviter.getUniqueId());
        if (coven == null || !coven.isLeader(inviter.getUniqueId())) {
            return false;
        }

        PlayerData targetData = plugin.getDataManager().getPlayerData(target.getUniqueId());
        if (targetData.isInCoven()) {
            return false;
        }

        coven.addInvite(target.getUniqueId());
        return true;
    }

    /**
     * Accepts a coven invite.
     *
     * @param player the player accepting
     * @return the coven joined, or null if no invite
     */
    public CovenData acceptInvite(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        if (data.isInCoven()) {
            return null;
        }

        for (CovenData coven : covens.values()) {
            if (coven.hasInvite(player.getUniqueId())) {
                coven.addMember(player.getUniqueId());
                data.setCovenId(coven.getCovenId());

                WitchCovenJoinEvent event = new WitchCovenJoinEvent(player, coven);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    coven.removeMember(player.getUniqueId());
                    data.setCovenId(null);
                    return null;
                }

                return coven;
            }
        }
        return null;
    }

    /**
     * Removes a player from their coven.
     *
     * @param player the player leaving
     * @return true if left successfully
     */
    public boolean leaveCoven(Player player) {
        CovenData coven = getCovenForMember(player.getUniqueId());
        if (coven == null) {
            return false;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());

        WitchCovenLeaveEvent event = new WitchCovenLeaveEvent(player, coven);
        Bukkit.getPluginManager().callEvent(event);

        coven.removeMember(player.getUniqueId());
        data.setCovenId(null);

        if (coven.isLeader(player.getUniqueId())) {
            disbandCoven(coven);
        }

        return true;
    }

    /**
     * Disbands a coven.
     *
     * @param coven the coven to disband
     */
    public void disbandCoven(CovenData coven) {
        for (UUID memberId : coven.getMemberIds()) {
            PlayerData data = plugin.getDataManager().getPlayerData(memberId);
            data.setCovenId(null);
        }
        covens.remove(coven.getCovenId());
    }

    /**
     * Gets the coven a player belongs to.
     *
     * @param playerId the player's UUID
     * @return the coven, or null
     */
    public CovenData getCovenForMember(UUID playerId) {
        PlayerData data = plugin.getDataManager().getPlayerData(playerId);
        if (data.getCovenId() == null) {
            return null;
        }
        return covens.get(data.getCovenId());
    }

    /**
     * Gets a coven by ID.
     *
     * @param covenId the coven UUID
     * @return the coven, or null
     */
    public CovenData getCoven(UUID covenId) {
        return covens.get(covenId);
    }

    /**
     * Gets all covens.
     *
     * @return collection of covens
     */
    public Collection<CovenData> getAllCovens() {
        return Collections.unmodifiableCollection(covens.values());
    }

    /**
     * Gets online members of a coven.
     *
     * @param coven the coven
     * @return list of online members
     */
    public List<Player> getOnlineMembers(CovenData coven) {
        List<Player> online = new ArrayList<>();
        for (UUID memberId : coven.getMemberIds()) {
            Player player = Bukkit.getPlayer(memberId);
            if (player != null && player.isOnline()) {
                online.add(player);
            }
        }
        return online;
    }

    /**
     * Counts online members near a location within a radius.
     *
     * @param coven  the coven
     * @param center the center location
     * @param radius the radius
     * @return number of online members within radius
     */
    public int countMembersNear(CovenData coven, org.bukkit.Location center, double radius) {
        int count = 0;
        for (UUID memberId : coven.getMemberIds()) {
            Player player = Bukkit.getPlayer(memberId);
            if (player != null && player.isOnline() && player.getWorld().equals(center.getWorld())) {
                if (player.getLocation().distance(center) <= radius) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Checks if a coven has at least a certain number of online members near a location.
     *
     * @param coven        the coven
     * @param center       the center location
     * @param radius       the radius
     * @param requiredSize the required number of members
     * @return true if enough members are present
     */
    public boolean hasEnoughMembersNear(CovenData coven, org.bukkit.Location center,
                                         double radius, int requiredSize) {
        return countMembersNear(coven, center, radius) >= requiredSize;
    }
}
