package com.witchcraft.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks spell cooldowns per player.
 */
public class SpellCooldown {

    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    /**
     * Checks if a player is on cooldown for a specific spell.
     *
     * @param playerId the player's UUID
     * @param spellId  the spell's ID
     * @return true if the player is on cooldown
     */
    public boolean isOnCooldown(UUID playerId, String spellId) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) return false;

        Long expiry = playerCooldowns.get(spellId);
        if (expiry == null) return false;

        return System.currentTimeMillis() < expiry;
    }

    /**
     * Gets the remaining cooldown time in milliseconds.
     *
     * @param playerId the player's UUID
     * @param spellId  the spell's ID
     * @return remaining cooldown in ms, 0 if not on cooldown
     */
    public long getRemainingCooldown(UUID playerId, String spellId) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) return 0;

        Long expiry = playerCooldowns.get(spellId);
        if (expiry == null) return 0;

        long remaining = expiry - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * Sets a cooldown for a player on a specific spell.
     *
     * @param playerId       the player's UUID
     * @param spellId        the spell's ID
     * @param cooldownTicks  the cooldown duration in ticks (20 ticks = 1 second)
     */
    public void setCooldown(UUID playerId, String spellId, long cooldownTicks) {
        cooldowns.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(spellId, System.currentTimeMillis() + (cooldownTicks * 50));
    }

    /**
     * Clears all cooldowns for a player.
     *
     * @param playerId the player's UUID
     */
    public void clearCooldowns(UUID playerId) {
        cooldowns.remove(playerId);
    }

    /**
     * Clears a specific spell cooldown for a player.
     *
     * @param playerId the player's UUID
     * @param spellId  the spell's ID
     */
    public void clearCooldown(UUID playerId, String spellId) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns != null) {
            playerCooldowns.remove(spellId);
        }
    }
}
