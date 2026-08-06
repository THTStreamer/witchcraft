package com.witchcraft.api.events;

import com.witchcraft.coven.CovenSpell;
import com.witchcraft.data.CovenData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

/**
 * Fired when a coven spell is cast.
 */
public class WitchCovenSpellCastEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final CovenSpell spell;
    private final CovenData coven;
    private final List<Player> participants;
    private final Player target;
    private boolean cancelled;

    public WitchCovenSpellCastEvent(Player caster, CovenSpell spell, CovenData coven,
                                    List<Player> participants, Player target) {
        super(caster);
        this.spell = spell;
        this.coven = coven;
        this.participants = participants;
        this.target = target;
    }

    /**
     * Gets the coven spell that was cast.
     *
     * @return the coven spell
     */
    public CovenSpell getCovenSpell() {
        return spell;
    }

    /**
     * Gets the coven that cast the spell.
     *
     * @return the coven
     */
    public CovenData getCoven() {
        return coven;
    }

    /**
     * Gets the players who participated in the incantation.
     *
     * @return list of participating players
     */
    public List<Player> getParticipants() {
        return participants;
    }

    /**
     * Gets the target player (may be null for AoE spells).
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
