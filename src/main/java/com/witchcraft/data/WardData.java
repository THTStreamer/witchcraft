package com.witchcraft.data;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Stores data about a warded chunk.
 */
public class WardData {

    private final UUID wardId;
    private final UUID ownerId;
    private final String worldName;
    private final int chunkX;
    private final int chunkZ;
    private final long expiryTime;
    private final String spellId;

    public WardData(UUID wardId, UUID ownerId, String worldName, int chunkX, int chunkZ,
                    long expiryTime, String spellId) {
        this.wardId = wardId;
        this.ownerId = ownerId;
        this.worldName = worldName;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.expiryTime = expiryTime;
        this.spellId = spellId;
    }

    public UUID getWardId() {
        return wardId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public String getSpellId() {
        return spellId;
    }

    /**
     * Checks if this ward has expired.
     *
     * @return true if the ward has expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiryTime;
    }

    /**
     * Gets the chunk key for efficient lookups.
     *
     * @return a unique key for this chunk
     */
    public String getChunkKey() {
        return worldName + ":" + chunkX + ":" + chunkZ;
    }

    /**
     * Checks if a location is within this ward's chunk.
     *
     * @param location the location to check
     * @return true if the location is in this ward's chunk
     */
    public boolean isLocationProtected(Location location) {
        if (!location.getWorld().getName().equals(worldName)) return false;
        return location.getBlockX() >> 4 == chunkX && location.getBlockZ() >> 4 == chunkZ;
    }
}
