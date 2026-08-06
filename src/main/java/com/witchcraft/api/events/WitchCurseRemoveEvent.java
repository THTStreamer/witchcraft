package com.witchcraft.api.events;

import com.witchcraft.core.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a curse is removed from a player.
 */
public class WitchCurseRemoveEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Spell curseSpell;
    private final Player target;

    public WitchCurseRemoveEvent(Player target, Spell curseSpell) {
        super(target);
        this.target = target;
        this.curseSpell = curseSpell;
    }

    /**
     * Gets the curse spell being removed.
     *
     * @return the curse spell
     */
    public Spell getCurseSpell() {
        return curseSpell;
    }

    /**
     * Gets the player the curse is being removed from.
     *
     * @return the target
     */
    public Player getTarget() {
        return target;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
