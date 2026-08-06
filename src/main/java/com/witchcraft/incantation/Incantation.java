package com.witchcraft.incantation;

/**
 * Represents a learnable incantation.
 */
public class Incantation {

    private final String id;
    private final String incantation;
    private final String spellId;
    private final String displayName;
    private final String description;
    private final String[] aliases;

    public Incantation(String id, String incantation, String spellId, String displayName,
                       String description, String... aliases) {
        this.id = id;
        this.incantation = incantation;
        this.spellId = spellId;
        this.displayName = displayName;
        this.description = description;
        this.aliases = aliases;
    }

    public String getId() {
        return id;
    }

    public String getIncantation() {
        return incantation;
    }

    public String getSpellId() {
        return spellId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    /**
     * Checks if the given input matches this incantation (case-insensitive, punctuation-agnostic).
     *
     * @param input the player's chat input
     * @return true if the input matches
     */
    public boolean matches(String input) {
        String normalizedInput = normalizeInput(input);
        String normalizedIncantation = normalizeInput(incantation);

        if (normalizedInput.equals(normalizedIncantation)) {
            return true;
        }

        for (String alias : aliases) {
            if (normalizedInput.equals(normalizeInput(alias))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Normalizes input by removing punctuation, extra spaces, and converting to lowercase.
     *
     * @param input the input to normalize
     * @return the normalized input
     */
    public static String normalizeInput(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Incantation other = (Incantation) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
