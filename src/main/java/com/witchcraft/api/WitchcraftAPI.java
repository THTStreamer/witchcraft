package com.witchcraft.api;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellRegistry;
import com.witchcraft.data.DataManager;

import java.util.Collection;
import java.util.UUID;

/**
 * Public API for the Witchcraft plugin.
 * Allows external plugins to interact with the magic system.
 */
public final class WitchcraftAPI {

    private static Witchcraft plugin;

    private WitchcraftAPI() {
    }

    /**
     * Initializes the API with the plugin instance.
     *
     * @param plugin the Witchcraft plugin instance
     */
    public static void initialize(Witchcraft plugin) {
        WitchcraftAPI.plugin = plugin;
    }

    /**
     * Gets the spell registry.
     *
     * @return the spell registry
     */
    public static SpellRegistry getSpellRegistry() {
        return plugin.getSpellRegistry();
    }

    /**
     * Gets the data manager.
     *
     * @return the data manager
     */
    public static DataManager getDataManager() {
        return plugin.getDataManager();
    }

    /**
     * Checks if a player has learned a specific incantation.
     *
     * @param playerId    the player's UUID
     * @param incantation the incantation to check
     * @return true if the player has learned it
     */
    public static boolean hasLearnedIncantation(UUID playerId, String incantation) {
        return plugin.getDataManager().getPlayerData(playerId)
                .hasLearnedIncantation(incantation);
    }

    /**
     * Gets all spells in a specific category.
     *
     * @param category the category to filter by
     * @return collection of spells
     */
    public static Collection<Spell> getSpellsByCategory(SpellCategory category) {
        return plugin.getSpellRegistry().getSpellsByCategory(category);
    }

    /**
     * Gets a spell by its ID.
     *
     * @param spellId the spell ID
     * @return the spell, or null if not found
     */
    public static Spell getSpell(String spellId) {
        return plugin.getSpellRegistry().getSpell(spellId);
    }

    /**
     * Checks if a player is under Arcane Exhaustion.
     *
     * @param playerId the player's UUID
     * @return true if the player is exhausted
     */
    public static boolean isExhausted(UUID playerId) {
        return plugin.getArcaneExhaustion().isExhausted(playerId);
    }

    /**
     * Gets the remaining Arcane Exhaustion time for a player.
     *
     * @param playerId the player's UUID
     * @return remaining ticks
     */
    public static long getExhaustionRemaining(UUID playerId) {
        return plugin.getArcaneExhaustion().getRemainingTicks(playerId);
    }
}
