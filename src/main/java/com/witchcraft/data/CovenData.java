package com.witchcraft.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a coven - a group of witches who practice magic together.
 */
public class CovenData {

    public static final int MAX_CLAIMED_CHUNKS = 4;

    private final UUID covenId;
    private final String name;
    private final UUID leaderId;
    private final Set<UUID> memberIds;
    private final Set<UUID> pendingInvites;
    private final Set<String> claimedChunks;
    private final long createdAt;

    public CovenData(UUID covenId, String name, UUID leaderId) {
        this.covenId = covenId;
        this.name = name;
        this.leaderId = leaderId;
        this.memberIds = new HashSet<>();
        this.pendingInvites = new HashSet<>();
        this.claimedChunks = new HashSet<>();
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

    public Set<String> getClaimedChunks() {
        return Collections.unmodifiableSet(claimedChunks);
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

    /**
     * Claims a chunk for this coven.
     *
     * @param chunkKey the chunk key in "world:chunkX:chunkZ" format
     * @return true if the chunk was claimed, false if already at max or already claimed
     */
    public boolean claimChunk(String chunkKey) {
        if (claimedChunks.size() >= MAX_CLAIMED_CHUNKS) {
            return false;
        }
        return claimedChunks.add(chunkKey);
    }

    /**
     * Unclaims a chunk.
     *
     * @param chunkKey the chunk key
     * @return true if the chunk was unclaimed
     */
    public boolean unclaimChunk(String chunkKey) {
        return claimedChunks.remove(chunkKey);
    }

    /**
     * Checks if a chunk is claimed by this coven.
     *
     * @param chunkKey the chunk key
     * @return true if claimed
     */
    public boolean isChunkClaimed(String chunkKey) {
        return claimedChunks.contains(chunkKey);
    }

    /**
     * Gets the number of claimed chunks.
     *
     * @return the count
     */
    public int getClaimedChunkCount() {
        return claimedChunks.size();
    }
}
