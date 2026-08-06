package com.witchcraft.core;

import com.witchcraft.Witchcraft;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Arcane Exhaustion status for players.
 * When exhausted, a player cannot cast magic.
 */
public class ArcaneExhaustion {

    private final Witchcraft plugin;
    private final Map<UUID, Long> exhaustedPlayers = new ConcurrentHashMap<>();

    /**
     * Duration of Arcane Exhaustion in Minecraft ticks (1 day = 24000 ticks).
     */
    private final long exhaustionDuration;

    public ArcaneExhaustion(Witchcraft plugin) {
        this.plugin = plugin;
        int days = plugin.getConfigManager().getConfig()
                .getInt("general.arcane-exhaustion-days", 3);
        this.exhaustionDuration = days * 24000L;
    }

    /**
     * Checks if a player is currently under Arcane Exhaustion.
     *
     * @param playerId the player's UUID
     * @return true if the player is exhausted
     */
    public boolean isExhausted(UUID playerId) {
        Long expiry = exhaustedPlayers.get(playerId);
        if (expiry == null) return false;

        if (System.currentTimeMillis() >= expiry) {
            exhaustedPlayers.remove(playerId);
            return false;
        }
        return true;
    }

    /**
     * Gets the remaining exhaustion time in ticks.
     *
     * @param playerId the player's UUID
     * @return remaining ticks, 0 if not exhausted
     */
    public long getRemainingTicks(UUID playerId) {
        Long expiry = exhaustedPlayers.get(playerId);
        if (expiry == null) return 0;

        long remainingMs = expiry - System.currentTimeMillis();
        return Math.max(0, remainingMs / 50); // Convert ms to ticks
    }

    /**
     * Applies Arcane Exhaustion to a player.
     *
     * @param playerId the player's UUID
     */
    public void applyExhaustion(UUID playerId) {
        long expiry = System.currentTimeMillis() + (exhaustionDuration * 50);
        exhaustedPlayers.put(playerId, expiry);

        Player player = plugin.getServer().getPlayer(playerId);
        if (player != null && player.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getMessage("anti-scrying.exhaustion"));
        }
    }

    /**
     * Removes Arcane Exhaustion from a player.
     *
     * @param playerId the player's UUID
     */
    public void removeExhaustion(UUID playerId) {
        exhaustedPlayers.remove(playerId);
    }

    /**
     * Checks if a player can cast spells.
     *
     * @param playerId the player's UUID
     * @return true if the player can cast
     */
    public boolean canCast(UUID playerId) {
        return !isExhausted(playerId);
    }

    /**
     * Gets all currently exhausted players.
     *
     * @return map of player UUIDs to expiry times
     */
    public Map<UUID, Long> getExhaustedPlayers() {
        return new ConcurrentHashMap<>(exhaustedPlayers);
    }

    /**
     * Cleans up expired exhaustion entries.
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        exhaustedPlayers.entrySet().removeIf(entry -> now >= entry.getValue());
    }
}
