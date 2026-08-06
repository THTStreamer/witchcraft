package com.witchcraft.api;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellRegistry;
import com.witchcraft.coven.CovenManager;
import com.witchcraft.coven.CovenSpell;
import com.witchcraft.coven.CovenSpellRegistry;
import com.witchcraft.data.CovenData;
import com.witchcraft.data.DataManager;
import com.witchcraft.incantation.Incantation;
import com.witchcraft.incantation.IncantationManager;
import com.witchcraft.ritual.RitualManager;
import com.witchcraft.ritual.RitualRecipe;
import com.witchcraft.ritual.RitualRecipeRegistry;
import com.witchcraft.core.Ingredient;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
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

    // ==================== Spell Registry ====================

    /**
     * Gets the spell registry.
     *
     * @return the spell registry
     */
    public static SpellRegistry getSpellRegistry() {
        return plugin.getSpellRegistry();
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
     * Gets all registered spells.
     *
     * @return collection of all spells
     */
    public static Collection<Spell> getAllSpells() {
        return plugin.getSpellRegistry().getAllSpells();
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

    // ==================== Data Manager ====================

    /**
     * Gets the data manager.
     *
     * @return the data manager
     */
    public static DataManager getDataManager() {
        return plugin.getDataManager();
    }

    // ==================== Incantations ====================

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
     * Gets the incantation manager.
     *
     * @return the incantation manager
     */
    public static IncantationManager getIncantationManager() {
        return plugin.getIncantationManager();
    }

    /**
     * Gets an incantation by its ID.
     *
     * @param id the incantation ID
     * @return the incantation, or null if not found
     */
    public static Incantation getIncantation(String id) {
        return plugin.getIncantationManager().getIncantation(id);
    }

    /**
     * Gets all registered incantations.
     *
     * @return collection of incantations
     */
    public static Collection<Incantation> getAllIncantations() {
        return plugin.getIncantationManager().getAllIncantations();
    }

    /**
     * Matches a chat message to an incantation.
     *
     * @param input the chat message
     * @return the matching incantation, or null
     */
    public static Incantation matchIncantation(String input) {
        return plugin.getIncantationManager().matchIncantation(input);
    }

    /**
     * Checks if a player is on cooldown for a specific spell.
     *
     * @param playerId the player's UUID
     * @param spellId  the spell ID
     * @return true if on cooldown
     */
    public static boolean isOnCooldown(UUID playerId, String spellId) {
        return plugin.getIncantationManager().getCooldowns().isOnCooldown(playerId, spellId);
    }

    /**
     * Gets remaining cooldown time in milliseconds for a spell.
     *
     * @param playerId the player's UUID
     * @param spellId  the spell ID
     * @return remaining cooldown in ms, or 0 if not on cooldown
     */
    public static long getCooldownRemaining(UUID playerId, String spellId) {
        return plugin.getIncantationManager().getCooldowns().getRemainingCooldown(playerId, spellId);
    }

    // ==================== Arcane Exhaustion ====================

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

    // ==================== Ritual Recipes ====================

    /**
     * Gets the ritual recipe registry.
     *
     * @return the ritual recipe registry
     */
    public static RitualRecipeRegistry getRitualRecipeRegistry() {
        return plugin.getRitualManager().getRecipeRegistry();
    }

    /**
     * Gets a ritual recipe by spell ID.
     *
     * @param spellId the spell ID
     * @return the recipe, or null if not found
     */
    public static RitualRecipe getRitualRecipe(String spellId) {
        return plugin.getRitualManager().getRecipeRegistry().getBySpellId(spellId);
    }

    /**
     * Gets all registered ritual recipes.
     *
     * @return collection of recipes
     */
    public static Collection<RitualRecipe> getAllRitualRecipes() {
        return plugin.getRitualManager().getRecipeRegistry().getAllRecipes();
    }

    /**
     * Finds a ritual recipe that matches the given ingredients.
     *
     * @param ingredients the ingredients to match
     * @return the matching recipe, or null
     */
    public static RitualRecipe findRitualRecipeByIngredients(List<Ingredient> ingredients) {
        return plugin.getRitualManager().getRecipeRegistry().findByIngredients(ingredients);
    }

    // ==================== Covens ====================

    /**
     * Gets the coven manager.
     *
     * @return the coven manager
     */
    public static CovenManager getCovenManager() {
        return plugin.getCovenManager();
    }

    /**
     * Gets the coven a player belongs to.
     *
     * @param playerId the player's UUID
     * @return the coven, or null if not in one
     */
    public static CovenData getCovenForMember(UUID playerId) {
        return plugin.getCovenManager().getCovenForMember(playerId);
    }

    /**
     * Gets a coven by its ID.
     *
     * @param covenId the coven UUID
     * @return the coven, or null
     */
    public static CovenData getCoven(UUID covenId) {
        return plugin.getCovenManager().getCoven(covenId);
    }

    /**
     * Gets all covens on the server.
     *
     * @return collection of covens
     */
    public static Collection<CovenData> getAllCovens() {
        return plugin.getCovenManager().getAllCovens();
    }

    /**
     * Checks if a player is a member of any coven.
     *
     * @param playerId the player's UUID
     * @return true if in a coven
     */
    public static boolean isInCoven(UUID playerId) {
        return plugin.getCovenManager().getCovenForMember(playerId) != null;
    }

    /**
     * Checks if a player is the leader of their coven.
     *
     * @param playerId the player's UUID
     * @return true if the player is a coven leader
     */
    public static boolean isCovenLeader(UUID playerId) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(playerId);
        return coven != null && coven.isLeader(playerId);
    }

    /**
     * Gets the size of a player's coven.
     *
     * @param playerId the player's UUID
     * @return coven size, or 0 if not in a coven
     */
    public static int getCovenSize(UUID playerId) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(playerId);
        return coven != null ? coven.getSize() : 0;
    }

    /**
     * Gets online members of a player's coven.
     *
     * @param playerId the player's UUID
     * @return list of online coven members, or empty list
     */
    public static List<Player> getOnlineCovenMembers(UUID playerId) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(playerId);
        if (coven == null) return List.of();
        return plugin.getCovenManager().getOnlineMembers(coven);
    }

    /**
     * Checks if a coven has enough members near a location.
     *
     * @param covenId      the coven UUID
     * @param center       the center location
     * @param radius       the radius
     * @param requiredSize the required number of members
     * @return true if enough members are present
     */
    public static boolean covenHasEnoughMembersNear(UUID covenId, org.bukkit.Location center,
                                                     double radius, int requiredSize) {
        CovenData coven = plugin.getCovenManager().getCoven(covenId);
        if (coven == null) return false;
        return plugin.getCovenManager().hasEnoughMembersNear(coven, center, radius, requiredSize);
    }

    // ==================== Coven Spells ====================

    /**
     * Gets the coven spell registry.
     *
     * @return the coven spell registry
     */
    public static CovenSpellRegistry getCovenSpellRegistry() {
        return plugin.getCovenSpellRegistry();
    }

    /**
     * Gets a coven spell by its ID.
     *
     * @param spellId the spell ID
     * @return the coven spell, or null if not found
     */
    public static CovenSpell getCovenSpell(String spellId) {
        return plugin.getCovenSpellRegistry().getSpell(spellId);
    }

    /**
     * Gets all registered coven spells.
     *
     * @return collection of coven spells
     */
    public static Collection<CovenSpell> getAllCovenSpells() {
        return plugin.getCovenSpellRegistry().getAllSpells();
    }

    /**
     * Finds a coven spell that matches a chat message.
     *
     * @param message the chat message
     * @return the matching coven spell, or null
     */
    public static CovenSpell findCovenSpellByMessage(String message) {
        return plugin.getCovenSpellRegistry().findByMessage(message);
    }
}
