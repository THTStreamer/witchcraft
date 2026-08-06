package com.witchcraft.data;

import java.util.UUID;

/**
 * Stores data about a fertility region.
 */
public class FertilityRegion {

    private final UUID regionId;
    private final UUID ownerId;
    private final String worldName;
    private final int centerX;
    private final int centerZ;
    private final int radius;
    private final long expiryTime;
    private final String spellId;

    public FertilityRegion(UUID regionId, UUID ownerId, String worldName, int centerX, int centerZ,
                           int radius, long expiryTime, String spellId) {
        this.regionId = regionId;
        this.ownerId = ownerId;
        this.worldName = worldName;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.expiryTime = expiryTime;
        this.spellId = spellId;
    }

    public UUID getRegionId() {
        return regionId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public int getRadius() {
        return radius;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public String getSpellId() {
        return spellId;
    }

    /**
     * Checks if this region has expired.
     *
     * @return true if the region has expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiryTime;
    }

    /**
     * Checks if a location is within this fertility region.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @param worldName the world name
     * @return true if the location is within the region
     */
    public boolean isLocationInside(int x, int z, String worldName) {
        if (!this.worldName.equals(worldName)) return false;
        int dx = x - centerX;
        int dz = z - centerZ;
        return (dx * dx + dz * dz) <= (radius * radius);
    }
}
