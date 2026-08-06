package com.witchcraft.ritual;

import com.witchcraft.core.Ingredient;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an active cauldron ritual.
 * Tracks the location, ingredients added, charge progress, and state.
 * Optionally stores a target player UUID when a target paper is used.
 */
public class RitualCauldron {

    private final UUID cauldronId;
    private final UUID casterId;
    private final Location cauldronLocation;
    private RitualRecipe recipe;
    private final List<Ingredient> addedIngredients;
    private long chargeDuration;
    private int fillLevel;
    private boolean active;
    private boolean completed;
    private long startTime;
    private UUID targetPlayerId;
    private String targetPlayerName;

    public RitualCauldron(UUID casterId, Location cauldronLocation, RitualRecipe recipe) {
        this.cauldronId = UUID.randomUUID();
        this.casterId = casterId;
        this.cauldronLocation = cauldronLocation.clone();
        this.recipe = recipe;
        this.addedIngredients = new ArrayList<>();
        this.fillLevel = 0;
        this.active = false;
        this.completed = false;
        this.startTime = 0;
        this.chargeDuration = recipe.getRitualDurationTicks();
        this.targetPlayerId = null;
        this.targetPlayerName = null;
    }

    public UUID getCauldronId() {
        return cauldronId;
    }

    public UUID getCasterId() {
        return casterId;
    }

    /**
     * Gets the location of the cauldron block.
     *
     * @return the cauldron location
     */
    public Location getCauldronLocation() {
        return cauldronLocation;
    }

    /**
     * Returns a unique key for this cauldron location for map lookups.
     *
     * @return "world:x:y:z"
     */
    public String getLocationKey() {
        return cauldronLocation.getWorld().getName() + ":" +
                cauldronLocation.getBlockX() + ":" +
                cauldronLocation.getBlockY() + ":" +
                cauldronLocation.getBlockZ();
    }

    public RitualRecipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe for this cauldron (used when switching to a better match).
     *
     * @param recipe the new recipe
     */
    public void setRecipe(RitualRecipe recipe) {
        this.recipe = recipe;
        this.chargeDuration = recipe.getRitualDurationTicks();
    }

    public List<Ingredient> getAddedIngredients() {
        return addedIngredients;
    }

    /**
     * Adds an ingredient to the cauldron.
     *
     * @param ingredient the ingredient to add
     * @return true if the ingredient was added, false if the cauldron is full
     */
    public boolean addIngredient(Ingredient ingredient) {
        if (addedIngredients.size() >= recipe.getRequiredIngredients().size()) {
            return false;
        }
        addedIngredients.add(ingredient);
        fillLevel++;
        return true;
    }

    /**
     * Checks if the cauldron has received all required ingredients.
     *
     * @return true if all ingredients are present
     */
    public boolean hasAllIngredients() {
        return recipe.matchesIngredients(addedIngredients);
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getChargeDuration() {
        return chargeDuration;
    }

    /**
     * Gets the target player UUID (from a target paper).
     *
     * @return the target UUID, or null if no target paper was used
     */
    public UUID getTargetPlayerId() {
        return targetPlayerId;
    }

    /**
     * Sets the target player UUID.
     *
     * @param targetPlayerId the target player's UUID
     */
    public void setTargetPlayerId(UUID targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    /**
     * Gets the target player's name.
     *
     * @return the target name, or null if no target paper was used
     */
    public String getTargetPlayerName() {
        return targetPlayerName;
    }

    /**
     * Sets the target player's name.
     *
     * @param targetPlayerName the target player's name
     */
    public void setTargetPlayerName(String targetPlayerName) {
        this.targetPlayerName = targetPlayerName;
    }

    /**
     * Checks if this ritual has a target.
     *
     * @return true if a target paper was used
     */
    public boolean hasTarget() {
        return targetPlayerId != null;
    }

    /**
     * Checks if the ritual has been charging long enough to complete.
     *
     * @param currentTick the current server tick
     * @return true if the ritual should complete
     */
    public boolean shouldComplete(long currentTick) {
        return currentTick - startTime >= chargeDuration;
    }

    /**
     * Gets the charge progress as a percentage.
     *
     * @param currentTick the current server tick
     * @return progress from 0.0 to 1.0
     */
    public double getChargeProgress(long currentTick) {
        long elapsed = currentTick - startTime;
        return Math.min(1.0, (double) elapsed / chargeDuration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RitualCauldron other = (RitualCauldron) obj;
        return cauldronId.equals(other.cauldronId);
    }

    @Override
    public int hashCode() {
        return cauldronId.hashCode();
    }
}
