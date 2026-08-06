package com.witchcraft.core;

import com.witchcraft.Witchcraft;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Base class for all spells in Witchcraft.
 * Each spell has a unique ID, category, ingredients, and effects.
 */
public abstract class Spell {

    protected final Witchcraft plugin;

    private final String id;
    private final String displayName;
    private final SpellCategory category;
    private final List<Ingredient> ingredients;
    private final String incantation;
    private final long cooldownTicks;
    private final int xpCost;
    private final double successChance;
    private final double failureChance;
    private final double backfireChance;
    private final String permission;
    private final boolean requiresTarget;

    protected Spell(Witchcraft plugin, String id, String displayName, SpellCategory category,
                    List<Ingredient> ingredients, String incantation, long cooldownTicks,
                    int xpCost, double successChance, double failureChance, double backfireChance,
                    String permission) {
        this(plugin, id, displayName, category, ingredients, incantation, cooldownTicks,
                xpCost, successChance, failureChance, backfireChance, permission, false);
    }

    protected Spell(Witchcraft plugin, String id, String displayName, SpellCategory category,
                    List<Ingredient> ingredients, String incantation, long cooldownTicks,
                    int xpCost, double successChance, double failureChance, double backfireChance,
                    String permission, boolean requiresTarget) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.category = category;
        this.ingredients = ingredients;
        this.incantation = incantation;
        this.cooldownTicks = cooldownTicks;
        this.xpCost = xpCost;
        this.successChance = successChance;
        this.failureChance = failureChance;
        this.backfireChance = backfireChance;
        this.permission = permission;
        this.requiresTarget = requiresTarget;
    }

    /**
     * Executes the spell's effect.
     *
     * @param caster   the player casting the spell
     * @param location the target location (may be null for self-targeting spells)
     * @param target   the target player (may be null for non-player-targeting spells)
     * @return the result of the spell cast
     */
    public abstract SpellResult execute(Player caster, Location location, Player target);

    /**
     * Gets the unique ID of this spell.
     *
     * @return the spell ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the display name of this spell.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the category of this spell.
     *
     * @return the spell category
     */
    public SpellCategory getCategory() {
        return category;
    }

    /**
     * Gets the ingredients required for this spell.
     *
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Gets the incantation to cast this spell via chat.
     *
     * @return the incantation, or null if this spell cannot be cast via incantation
     */
    public String getIncantation() {
        return incantation;
    }

    /**
     * Gets the cooldown in ticks.
     *
     * @return cooldown ticks
     */
    public long getCooldownTicks() {
        return cooldownTicks;
    }

    /**
     * Gets the XP cost to cast this spell.
     *
     * @return the XP level cost
     */
    public int getXpCost() {
        return xpCost;
    }

    /**
     * Gets the success chance (0.0 to 1.0).
     *
     * @return success chance
     */
    public double getSuccessChance() {
        return successChance;
    }

    /**
     * Gets the failure chance (0.0 to 1.0).
     *
     * @return failure chance
     */
    public double getFailureChance() {
        return failureChance;
    }

    /**
     * Gets the backfire chance (0.0 to 1.0).
     *
     * @return backfire chance
     */
    public double getBackfireChance() {
        return backfireChance;
    }

    /**
     * Gets the permission required to cast this spell.
     *
     * @return the permission node
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Checks if this spell requires a target paper to be cast on another player.
     *
     * @return true if a target paper is required
     */
    public boolean requiresTarget() {
        return requiresTarget;
    }

    /**
     * Checks if a player has the required ingredients.
     *
     * @param player the player to check
     * @return true if the player has all ingredients
     */
    public boolean hasIngredients(Player player) {
        Map<Ingredient, Integer> ingredientCounts = new java.util.HashMap<>();
        for (Ingredient ingredient : ingredients) {
            ingredientCounts.merge(ingredient, 1, Integer::sum);
        }

        for (Map.Entry<Ingredient, Integer> entry : ingredientCounts.entrySet()) {
            int count = 0;
            for (var item : player.getInventory().getContents()) {
                if (item != null && item.getType() == entry.getKey().getMaterial()) {
                    count += item.getAmount();
                }
            }
            if (count < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes the required ingredients from a player's inventory.
     *
     * @param player the player to remove ingredients from
     */
    public void removeIngredients(Player player) {
        Map<Ingredient, Integer> ingredientCounts = new java.util.HashMap<>();
        for (Ingredient ingredient : ingredients) {
            ingredientCounts.merge(ingredient, 1, Integer::sum);
        }

        for (Map.Entry<Ingredient, Integer> entry : ingredientCounts.entrySet()) {
            int remaining = entry.getValue();
            for (var item : player.getInventory().getContents()) {
                if (item != null && item.getType() == entry.getKey().getMaterial() && remaining > 0) {
                    int toRemove = Math.min(item.getAmount(), remaining);
                    item.setAmount(item.getAmount() - toRemove);
                    remaining -= toRemove;
                }
            }
        }
    }

    /**
     * Determines the spell result based on configured chances.
     *
     * @return the calculated result (SUCCESS, FAILURE, or BACKFIRE)
     */
    protected SpellResult rollResult() {
        double roll = Math.random();
        if (roll < backfireChance) {
            return SpellResult.BACKFIRE;
        } else if (roll < backfireChance + failureChance) {
            return SpellResult.FAILURE;
        } else if (roll < backfireChance + failureChance + successChance) {
            return SpellResult.SUCCESS;
        }
        // Remaining probability is failure without backfire
        return SpellResult.FAILURE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Spell other = (Spell) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
