package com.witchcraft.core;

/**
 * Represents the result of a spell cast attempt.
 */
public enum SpellResult {

    /**
     * The spell was cast successfully.
     */
    SUCCESS,

    /**
     * The spell failed but did not backfire.
     */
    FAILURE,

    /**
     * The spell failed and caused a backfire.
     */
    BACKFIRE,

    /**
     * The spell was blocked (e.g., by protection).
     */
    BLOCKED,

    /**
     * The caster was under Arcane Exhaustion.
     */
    EXHAUSTED,

    /**
     * The caster did not meet the requirements.
     */
    INVALID
}
