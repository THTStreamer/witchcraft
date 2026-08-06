package com.witchcraft.api.events;

import com.witchcraft.core.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a spell backfires on the caster.
 */
public class WitchBackfireEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Spell spell;
    private final Player caster;

    public WitchBackfireEvent(Player caster, Spell spell) {
        super(caster);
        this.caster = caster;
        this.spell = spell;
    }

    /**
     * Gets the spell that backfired.
     *
     * @return the spell
     */
    public Spell getSpell() {
        return spell;
    }

    /**
     * Gets the caster the spell backfired on.
     *
     * @return the caster
     */
    public Player getCaster() {
        return caster;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
