package com.witchcraft.api.events;

import com.witchcraft.data.CovenData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a player joins a coven.
 */
public class WitchCovenJoinEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final CovenData coven;
    private boolean cancelled;

    public WitchCovenJoinEvent(Player player, CovenData coven) {
        super(player);
        this.coven = coven;
    }

    /**
     * Gets the coven the player joined.
     *
     * @return the coven
     */
    public CovenData getCoven() {
        return coven;
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
