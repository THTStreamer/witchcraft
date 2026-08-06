package com.witchcraft.data;

import com.witchcraft.Witchcraft;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Manages all persistent data storage.
 */
public class DataManager {

    private final Witchcraft plugin;
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private final Map<String, WardData> wardDataMap = new ConcurrentHashMap<>();
    private final Map<String, FertilityRegion> fertilityRegionMap = new ConcurrentHashMap<>();

    private File playerDataFile;
    private File wardDataFile;
    private File fertilityDataFile;
    private FileConfiguration playerDataConfig;
    private FileConfiguration wardDataConfig;
    private FileConfiguration fertilityDataConfig;

    public DataManager(Witchcraft plugin) {
        this.plugin = plugin;
        initializeFiles();
        loadAll();
    }

    private void initializeFiles() {
        plugin.getDataFolder().mkdirs();
        playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        wardDataFile = new File(plugin.getDataFolder(), "wards.yml");
        fertilityDataFile = new File(plugin.getDataFolder(), "fertility.yml");

        if (!playerDataFile.exists()) {
            try { playerDataFile.createNewFile(); } catch (IOException e) { /* ignore */ }
        }
        if (!wardDataFile.exists()) {
            try { wardDataFile.createNewFile(); } catch (IOException e) { /* ignore */ }
        }
        if (!fertilityDataFile.exists()) {
            try { fertilityDataFile.createNewFile(); } catch (IOException e) { /* ignore */ }
        }

        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        wardDataConfig = YamlConfiguration.loadConfiguration(wardDataFile);
        fertilityDataConfig = YamlConfiguration.loadConfiguration(fertilityDataFile);
    }

    /**
     * Gets or creates player data.
     *
     * @param playerId the player's UUID
     * @return the player data
     */
    public PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, PlayerData::new);
    }

    /**
     * Saves a ward.
     *
     * @param ward the ward data to save
     */
    public void saveWard(WardData ward) {
        wardDataMap.put(ward.getWardId().toString(), ward);
    }

    /**
     * Removes a ward.
     *
     * @param wardId the ward's UUID
     */
    public void removeWard(UUID wardId) {
        wardDataMap.remove(wardId.toString());
    }

    /**
     * Gets all active wards.
     *
     * @return collection of ward data
     */
    public Collection<WardData> getActiveWards() {
        wardDataMap.values().removeIf(WardData::isExpired);
        return wardDataMap.values();
    }

    /**
     * Gets wards in a specific chunk.
     *
     * @param worldName the world name
     * @param chunkX    the chunk X coordinate
     * @param chunkZ    the chunk Z coordinate
     * @return collection of wards in the chunk
     */
    public Collection<WardData> getWardsInChunk(String worldName, int chunkX, int chunkZ) {
        String key = worldName + ":" + chunkX + ":" + chunkZ;
        return wardDataMap.values().stream()
                .filter(w -> w.getChunkKey().equals(key) && !w.isExpired())
                .toList();
    }

    /**
     * Gets the number of wards owned by a player.
     *
     * @param playerId the player's UUID
     * @return the number of active wards
     */
    public int getWardCount(UUID playerId) {
        return (int) wardDataMap.values().stream()
                .filter(w -> w.getOwnerId().equals(playerId) && !w.isExpired())
                .count();
    }

    /**
     * Saves a fertility region.
     *
     * @param region the fertility region to save
     */
    public void saveFertilityRegion(FertilityRegion region) {
        fertilityRegionMap.put(region.getRegionId().toString(), region);
    }

    /**
     * Removes a fertility region.
     *
     * @param regionId the region's UUID
     */
    public void removeFertilityRegion(UUID regionId) {
        fertilityRegionMap.remove(regionId.toString());
    }

    /**
     * Gets all active fertility regions.
     *
     * @return collection of fertility regions
     */
    public Collection<FertilityRegion> getActiveFertilityRegions() {
        fertilityRegionMap.values().removeIf(FertilityRegion::isExpired);
        return fertilityRegionMap.values();
    }

    /**
     * Checks if a location is in a fertility region.
     *
     * @param x         the x coordinate
     * @param z         the z coordinate
     * @param worldName the world name
     * @return true if the location is in a fertility region
     */
    public boolean isInFertilityRegion(int x, int z, String worldName) {
        return fertilityRegionMap.values().stream()
                .filter(r -> !r.isExpired())
                .anyMatch(r -> r.isLocationInside(x, z, worldName));
    }

    /**
     * Saves all data to disk.
     */
    public void saveAll() {
        savePlayerData();
        saveWardData();
        saveFertilityData();
        plugin.getLogger().finer("Witchcraft data saved.");
    }

    private void savePlayerData() {
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            String path = entry.getKey().toString();
            PlayerData data = entry.getValue();

            playerDataConfig.set(path + ".learned-incantations",
                    new java.util.ArrayList<>(data.getLearnedIncantations()));
            playerDataConfig.set(path + ".known-rituals",
                    new java.util.ArrayList<>(data.getKnownRituals()));
            playerDataConfig.set(path + ".last-login", data.getLastLogin());
            playerDataConfig.set(path + ".has-received-guide", data.hasReceivedGuide());
        }

        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save player data", e);
        }
    }

    private void saveWardData() {
        wardDataConfig = new YamlConfiguration();
        for (Map.Entry<String, WardData> entry : wardDataMap.entrySet()) {
            WardData ward = entry.getValue();
            if (ward.isExpired()) continue;

            String path = entry.getKey();
            wardDataConfig.set(path + ".owner", ward.getOwnerId().toString());
            wardDataConfig.set(path + ".world", ward.getWorldName());
            wardDataConfig.set(path + ".chunk-x", ward.getChunkX());
            wardDataConfig.set(path + ".chunk-z", ward.getChunkZ());
            wardDataConfig.set(path + ".expiry", ward.getExpiryTime());
            wardDataConfig.set(path + ".spell-id", ward.getSpellId());
        }

        try {
            wardDataConfig.save(wardDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save ward data", e);
        }
    }

    private void saveFertilityData() {
        fertilityDataConfig = new YamlConfiguration();
        for (Map.Entry<String, FertilityRegion> entry : fertilityRegionMap.entrySet()) {
            FertilityRegion region = entry.getValue();
            if (region.isExpired()) continue;

            String path = entry.getKey();
            fertilityDataConfig.set(path + ".owner", region.getOwnerId().toString());
            fertilityDataConfig.set(path + ".world", region.getWorldName());
            fertilityDataConfig.set(path + ".center-x", region.getCenterX());
            fertilityDataConfig.set(path + ".center-z", region.getCenterZ());
            fertilityDataConfig.set(path + ".radius", region.getRadius());
            fertilityDataConfig.set(path + ".expiry", region.getExpiryTime());
            fertilityDataConfig.set(path + ".spell-id", region.getSpellId());
        }

        try {
            fertilityDataConfig.save(fertilityDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save fertility data", e);
        }
    }

    /**
     * Loads all data from disk.
     */
    public void loadAll() {
        loadPlayerData();
        loadWardData();
        loadFertilityData();
    }

    private void loadPlayerData() {
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        for (String key : playerDataConfig.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                PlayerData data = new PlayerData(playerId);

                var incantations = playerDataConfig.getStringList(key + ".learned-incantations");
                for (String inc : incantations) {
                    data.learnIncantation(inc);
                }

                var rituals = playerDataConfig.getStringList(key + ".known-rituals");
                for (String ritual : rituals) {
                    data.learnRitual(ritual);
                }

                data.setLastLogin(playerDataConfig.getLong(key + ".last-login", System.currentTimeMillis()));
                data.setHasReceivedGuide(playerDataConfig.getBoolean(key + ".has-received-guide", false));

                playerDataMap.put(playerId, data);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load player data for key: " + key, e);
            }
        }
    }

    private void loadWardData() {
        wardDataConfig = YamlConfiguration.loadConfiguration(wardDataFile);
        for (String key : wardDataConfig.getKeys(false)) {
            try {
                UUID wardId = UUID.fromString(key);
                UUID owner = UUID.fromString(wardDataConfig.getString(key + ".owner"));
                String world = wardDataConfig.getString(key + ".world");
                int chunkX = wardDataConfig.getInt(key + ".chunk-x");
                int chunkZ = wardDataConfig.getInt(key + ".chunk-z");
                long expiry = wardDataConfig.getLong(key + ".expiry");
                String spellId = wardDataConfig.getString(key + ".spell-id", "unknown");

                WardData ward = new WardData(wardId, owner, world, chunkX, chunkZ, expiry, spellId);
                if (!ward.isExpired()) {
                    wardDataMap.put(key, ward);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load ward data for key: " + key, e);
            }
        }
    }

    private void loadFertilityData() {
        fertilityDataConfig = YamlConfiguration.loadConfiguration(fertilityDataFile);
        for (String key : fertilityDataConfig.getKeys(false)) {
            try {
                UUID regionId = UUID.fromString(key);
                UUID owner = UUID.fromString(fertilityDataConfig.getString(key + ".owner"));
                String world = fertilityDataConfig.getString(key + ".world");
                int centerX = fertilityDataConfig.getInt(key + ".center-x");
                int centerZ = fertilityDataConfig.getInt(key + ".center-z");
                int radius = fertilityDataConfig.getInt(key + ".radius", 16);
                long expiry = fertilityDataConfig.getLong(key + ".expiry");
                String spellId = fertilityDataConfig.getString(key + ".spell-id", "unknown");

                FertilityRegion region = new FertilityRegion(regionId, owner, world, centerX, centerZ,
                        radius, expiry, spellId);
                if (!region.isExpired()) {
                    fertilityRegionMap.put(key, region);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load fertility data for key: " + key, e);
            }
        }
    }
}
