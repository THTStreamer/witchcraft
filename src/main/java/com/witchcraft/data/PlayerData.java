package com.witchcraft.data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Stores persistent data for a player.
 */
public class PlayerData {

    private final UUID playerId;
    private final Set<String> learnedIncantations;
    private final Set<String> knownRituals;
    private long lastLogin;
    private boolean hasReceivedGuide;
    private UUID covenId;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.learnedIncantations = new java.util.HashSet<>();
        this.knownRituals = new java.util.HashSet<>();
        this.lastLogin = System.currentTimeMillis();
        this.hasReceivedGuide = false;
        this.covenId = null;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Set<String> getLearnedIncantations() {
        return learnedIncantations;
    }

    public boolean hasLearnedIncantation(String incantation) {
        return learnedIncantations.contains(incantation.toLowerCase());
    }

    public void learnIncantation(String incantation) {
        learnedIncantations.add(incantation.toLowerCase());
    }

    public void unlearnIncantation(String incantation) {
        learnedIncantations.remove(incantation.toLowerCase());
    }

    public Set<String> getKnownRituals() {
        return knownRituals;
    }

    public boolean knowsRitual(String ritualId) {
        return knownRituals.contains(ritualId);
    }

    public void learnRitual(String ritualId) {
        knownRituals.add(ritualId);
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean hasReceivedGuide() {
        return hasReceivedGuide;
    }

    public void setHasReceivedGuide(boolean hasReceivedGuide) {
        this.hasReceivedGuide = hasReceivedGuide;
    }

    public UUID getCovenId() {
        return covenId;
    }

    public void setCovenId(UUID covenId) {
        this.covenId = covenId;
    }

    public boolean isInCoven() {
        return covenId != null;
    }
}
