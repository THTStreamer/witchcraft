package com.witchcraft.data;

import java.util.*;

/**
 * Represents a coven - a group of witches who practice magic together.
 * Supports ranks: PRIEST/PRIESTESS (leaders), COUNCIL, INITIATE.
 */
public class CovenData {

    public static final int MAX_CLAIMED_CHUNKS = 4;
    public static final int MAX_LEADERS = 2;

    private final UUID covenId;
    private final String name;
    private final Set<UUID> memberIds;
    private final Map<UUID, CovenRank> ranks;
    private final Set<UUID> pendingInvites;
    private final Set<String> claimedChunks;
    private final long createdAt;

    public CovenData(UUID covenId, String name, UUID leaderId) {
        this.covenId = covenId;
        this.name = name;
        this.memberIds = new HashSet<>();
        this.ranks = new HashMap<>();
        this.pendingInvites = new HashSet<>();
        this.claimedChunks = new HashSet<>();
        this.createdAt = System.currentTimeMillis();
        this.memberIds.add(leaderId);
        this.ranks.put(leaderId, CovenRank.PRIEST);
    }

    public UUID getCovenId() {
        return covenId;
    }

    public String getName() {
        return name;
    }

    public Set<UUID> getMemberIds() {
        return memberIds;
    }

    public Map<UUID, CovenRank> getRanks() {
        return Collections.unmodifiableMap(ranks);
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

    /**
     * Checks if a player is a leader (PRIEST or PRIESTESS).
     */
    public boolean isLeader(UUID playerId) {
        CovenRank rank = ranks.get(playerId);
        return rank != null && rank.isLeader();
    }

    public boolean hasInvite(UUID playerId) {
        return pendingInvites.contains(playerId);
    }

    public CovenRank getRank(UUID playerId) {
        return ranks.getOrDefault(playerId, CovenRank.INITIATE);
    }

    /**
     * Counts how many leaders of a specific rank type exist.
     */
    public int countLeadersOfType(CovenRank rankType) {
        int count = 0;
        for (CovenRank r : ranks.values()) {
            if (r == rankType) count++;
        }
        return count;
    }

    /**
     * Counts total leaders (PRIEST + PRIESTESS).
     */
    public int countLeaders() {
        int count = 0;
        for (CovenRank r : ranks.values()) {
            if (r.isLeader()) count++;
        }
        return count;
    }

    /**
     * Gets all members of a specific rank.
     */
    public Set<UUID> getMembersWithRank(CovenRank rank) {
        Set<UUID> result = new HashSet<>();
        for (var entry : ranks.entrySet()) {
            if (entry.getValue() == rank) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Sets a player's rank. Enforces max 2 leaders total.
     *
     * @return true if rank was set, false if leader limit reached
     */
    public boolean setRank(UUID playerId, CovenRank newRank) {
        if (!memberIds.contains(playerId)) return false;

        CovenRank oldRank = ranks.get(playerId);

        // If promoting to a leader rank, check the limit
        if (newRank.isLeader()) {
            // If already a leader, just swap the type (no limit issue)
            if (oldRank != null && oldRank.isLeader()) {
                ranks.put(playerId, newRank);
                return true;
            }
            // Check if we're at the leader cap
            if (countLeaders() >= MAX_LEADERS) {
                return false;
            }
        }

        ranks.put(playerId, newRank);
        return true;
    }

    /**
     * Promotes a member to the next higher rank.
     * INITIATE -> COUNCIL -> PRIEST/PRIESTESS (if slot available)
     *
     * @return the new rank, or null if already at max
     */
    public CovenRank promote(UUID playerId) {
        CovenRank current = getRank(playerId);
        return switch (current) {
            case INITIATE -> {
                setRank(playerId, CovenRank.COUNCIL);
                yield CovenRank.COUNCIL;
            }
            case COUNCIL -> {
                // Try to promote to PRIEST if slot open, else PRIESTESS
                if (countLeadersOfType(CovenRank.PRIEST) < MAX_LEADERS) {
                    setRank(playerId, CovenRank.PRIEST);
                    yield CovenRank.PRIEST;
                } else if (countLeadersOfType(CovenRank.PRIESTESS) < MAX_LEADERS) {
                    setRank(playerId, CovenRank.PRIESTESS);
                    yield CovenRank.PRIESTESS;
                }
                yield null; // Both leader slots full
            }
            default -> null; // Already at top or unknown
        };
    }

    /**
     * Demotes a member to the next lower rank.
     * PRIEST/PRIESTESS -> COUNCIL -> INITIATE
     *
     * @return the new rank, or null if already at bottom
     */
    public CovenRank demote(UUID playerId) {
        CovenRank current = getRank(playerId);
        return switch (current) {
            case PRIEST, PRIESTESS -> {
                setRank(playerId, CovenRank.COUNCIL);
                yield CovenRank.COUNCIL;
            }
            case COUNCIL -> {
                setRank(playerId, CovenRank.INITIATE);
                yield CovenRank.INITIATE;
            }
            default -> null; // Already at bottom
        };
    }

    public void addMember(UUID playerId) {
        memberIds.add(playerId);
        ranks.putIfAbsent(playerId, CovenRank.INITIATE);
        pendingInvites.remove(playerId);
    }

    public void addMember(UUID playerId, CovenRank rank) {
        memberIds.add(playerId);
        ranks.put(playerId, rank);
        pendingInvites.remove(playerId);
    }

    public void removeMember(UUID playerId) {
        memberIds.remove(playerId);
        ranks.remove(playerId);
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
