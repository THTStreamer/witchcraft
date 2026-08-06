package com.witchcraft.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a coven - a group of witches who practice magic together.
 */
public class CovenData {

    private final UUID covenId;
    private final String name;
    private final UUID leaderId;
    private final Set<UUID> memberIds;
    private final Set<UUID> pendingInvites;
    private final long createdAt;

    public CovenData(UUID covenId, String name, UUID leaderId) {
        this.covenId = covenId;
        this.name = name;
        this.leaderId = leaderId;
        this.memberIds = new HashSet<>();
        this.pendingInvites = new HashSet<>();
        this.createdAt = System.currentTimeMillis();
        this.memberIds.add(leaderId);
    }

    public UUID getCovenId() {
        return covenId;
    }

    public String getName() {
        return name;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public Set<UUID> getMemberIds() {
        return memberIds;
    }

    public Set<UUID> getPendingInvites() {
        return pendingInvites;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getSize() {
        return memberIds.size();
    }

    public boolean isMember(UUID playerId) {
        return memberIds.contains(playerId);
    }

    public boolean isLeader(UUID playerId) {
        return leaderId.equals(playerId);
    }

    public boolean hasInvite(UUID playerId) {
        return pendingInvites.contains(playerId);
    }

    public void addMember(UUID playerId) {
        memberIds.add(playerId);
        pendingInvites.remove(playerId);
    }

    public void removeMember(UUID playerId) {
        memberIds.remove(playerId);
    }

    public void addInvite(UUID playerId) {
        pendingInvites.add(playerId);
    }

    public void removeInvite(UUID playerId) {
        pendingInvites.remove(playerId);
    }
}
