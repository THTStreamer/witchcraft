package com.witchcraft.api.events;

import com.witchcraft.data.CovenData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a coven is created.
 */
public class WitchCovenCreateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final CovenData coven;
    private final String covenName;
    private boolean cancelled;

    public WitchCovenCreateEvent(Player leader, CovenData coven, String covenName) {
        super(leader);
        this.coven = coven;
        this.covenName = covenName;
    }

    /**
     * Gets the newly created coven.
     *
     * @return the coven
     */
    public CovenData getCoven() {
        return coven;
    }

    /**
     * Gets the name of the coven.
     *
     * @return the coven name
     */
    public String getCovenName() {
        return covenName;
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
