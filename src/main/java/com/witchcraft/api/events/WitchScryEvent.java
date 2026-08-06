package com.witchcraft.api.events;

import com.witchcraft.core.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a scrying spell is cast on a target.
 */
public class WitchScryEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Spell scrySpell;
    private final Player caster;
    private final Player target;
    private boolean cancelled;

    public WitchScryEvent(Player caster, Player target, Spell scrySpell) {
        super(caster);
        this.caster = caster;
        this.target = target;
        this.scrySpell = scrySpell;
    }

    public Spell getScrySpell() {
        return scrySpell;
    }

    public Player getCaster() {
        return caster;
    }

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
