package com.witchcraft.api.events;

import com.witchcraft.data.CovenData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a player leaves a coven (or is kicked).
 */
public class WitchCovenLeaveEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final CovenData coven;

    public WitchCovenLeaveEvent(Player player, CovenData coven) {
        super(player);
        this.coven = coven;
    }

    /**
     * Gets the coven the player left.
     *
     * @return the coven
     */
    public CovenData getCoven() {
        return coven;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
