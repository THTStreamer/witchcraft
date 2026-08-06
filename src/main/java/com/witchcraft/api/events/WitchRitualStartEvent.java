package com.witchcraft.api.events;

import com.witchcraft.core.Spell;
import com.witchcraft.ritual.RitualRecipe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

/**
 * Fired when a ritual starts in a cauldron.
 */
public class WitchRitualStartEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Spell spell;
    private final RitualRecipe recipe;
    private final Location cauldronLocation;
    private final List<String> addedIngredients;
    private final Player target;
    private boolean cancelled;

    public WitchRitualStartEvent(Player caster, Spell spell, RitualRecipe recipe,
                                 Location cauldronLocation, List<String> addedIngredients,
                                 Player target) {
        super(caster);
        this.spell = spell;
        this.recipe = recipe;
        this.cauldronLocation = cauldronLocation;
        this.addedIngredients = addedIngredients;
        this.target = target;
    }

    /**
     * Gets the spell being cast.
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
     * Gets the ingredients that were added.
     *
     * @return list of ingredient names
     */
    public List<String> getAddedIngredients() {
        return addedIngredients;
    }

    /**
     * Gets the target player (may be null).
     *
     * @return the target
     */
    public Player getTarget() {
        return target;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
