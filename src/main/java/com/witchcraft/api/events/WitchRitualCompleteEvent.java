package com.witchcraft.api.events;

import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellResult;
import com.witchcraft.ritual.RitualRecipe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a ritual completes.
 */
public class WitchRitualCompleteEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Spell spell;
    private final RitualRecipe recipe;
    private final Location cauldronLocation;
    private final Player target;
    private final SpellResult result;

    public WitchRitualCompleteEvent(Player caster, Spell spell, RitualRecipe recipe,
                                    Location cauldronLocation, Player target,
                                    SpellResult result) {
        super(caster);
        this.spell = spell;
        this.recipe = recipe;
        this.cauldronLocation = cauldronLocation;
        this.target = target;
        this.result = result;
    }

    /**
     * Gets the spell that was cast.
     *
     * @return the spell
     */
    public Spell getSpell() {
        return spell;
    }

    /**
     * Gets the ritual recipe.
     *
     * @return the recipe
     */
    public RitualRecipe getRecipe() {
        return recipe;
    }

    /**
     * Gets the cauldron location.
     *
     * @return the location
     */
    public Location getCauldronLocation() {
        return cauldronLocation;
    }

    /**
     * Gets the target player (may be null).
     *
     * @return the target
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Gets the result of the ritual.
     *
     * @return the spell result
     */
    public SpellResult getResult() {
        return result;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
