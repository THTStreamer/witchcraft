package com.witchcraft.api.events;

import com.witchcraft.core.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a spell is cast successfully.
 */
public class WitchSpellCastEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Spell spell;
    private final Player target;
    private boolean cancelled;

    public WitchSpellCastEvent(Player caster, Spell spell, Player target) {
        super(caster);
        this.spell = spell;
        this.target = target;
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
     * Gets the target player (may be null for non-player-targeting spells).
     *
     * @return the target player
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
