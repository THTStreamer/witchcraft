package com.witchcraft.core;

import com.witchcraft.Witchcraft;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Base class for all spells in Witchcraft.
 * Spells define effects, cooldowns, and XP costs.
 * Rituals (cauldron + ingredients) and incantations (spoken words) are separate systems
 * that both trigger these spell effects.
 */
public abstract class Spell {

    protected final Witchcraft plugin;

    private final String id;
    private final String displayName;
    private final SpellCategory category;
    private final long cooldownTicks;
    private final int xpCost;
    private final double successChance;
    private final double failureChance;
    private final double backfireChance;
    private final String permission;
    private final boolean requiresTarget;

    protected Spell(Witchcraft plugin, String id, String displayName, SpellCategory category,
                    long cooldownTicks, int xpCost, double successChance, double failureChance,
                    double backfireChance, String permission) {
        this(plugin, id, displayName, category, cooldownTicks, xpCost, successChance,
                failureChance, backfireChance, permission, false);
    }

    protected Spell(Witchcraft plugin, String id, String displayName, SpellCategory category,
                    long cooldownTicks, int xpCost, double successChance, double failureChance,
                    double backfireChance, String permission, boolean requiresTarget) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.category = category;
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

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public SpellCategory getCategory() { return category; }
    public long getCooldownTicks() { return cooldownTicks; }
    public int getXpCost() { return xpCost; }
    public double getSuccessChance() { return successChance; }
    public double getFailureChance() { return failureChance; }
    public double getBackfireChance() { return backfireChance; }
    public String getPermission() { return permission; }
    public boolean requiresTarget() { return requiresTarget; }

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
