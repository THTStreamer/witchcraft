package com.witchcraft.ritual;

import com.witchcraft.core.Ingredient;

import java.util.List;

/**
 * Defines a ritual recipe with required ingredients and settings.
 */
public class RitualRecipe {

    private final String spellId;
    private final String displayName;
    private final List<Ingredient> requiredIngredients;
    private final int requiredFillLevel;
    private final boolean requiresNearbyPlayer;
    private final long ritualDurationTicks;
    private final boolean moonPhaseRequired;
    private final int requiredMoonPhase;
    private final boolean weatherRequired;
    private final String requiredWeather;

    private RitualRecipe(Builder builder) {
        this.spellId = builder.spellId;
        this.displayName = builder.displayName;
        this.requiredIngredients = builder.requiredIngredients;
        this.requiredFillLevel = builder.requiredFillLevel;
        this.requiresNearbyPlayer = builder.requiresNearbyPlayer;
        this.ritualDurationTicks = builder.ritualDurationTicks;
        this.moonPhaseRequired = builder.moonPhaseRequired;
        this.requiredMoonPhase = builder.requiredMoonPhase;
        this.weatherRequired = builder.weatherRequired;
        this.requiredWeather = builder.requiredWeather;
    }

    public String getSpellId() {
        return spellId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Ingredient> getRequiredIngredients() {
        return requiredIngredients;
    }

    public int getRequiredFillLevel() {
        return requiredFillLevel;
    }

    public boolean isRequiresNearbyPlayer() {
        return requiresNearbyPlayer;
    }

    public long getRitualDurationTicks() {
        return ritualDurationTicks;
    }

    public boolean isMoonPhaseRequired() {
        return moonPhaseRequired;
    }

    public int getRequiredMoonPhase() {
        return requiredMoonPhase;
    }

    public boolean isWeatherRequired() {
        return weatherRequired;
    }

    public String getRequiredWeather() {
        return requiredWeather;
    }

    /**
     * Checks if the given ingredients match this recipe.
     *
     * @param ingredients the ingredients to check
     * @return true if the ingredients match
     */
    public boolean matchesIngredients(List<Ingredient> ingredients) {
        if (ingredients.size() != requiredIngredients.size()) {
            return false;
        }

        List<Ingredient> sorted = new java.util.ArrayList<>(ingredients);
        List<Ingredient> requiredSorted = new java.util.ArrayList<>(requiredIngredients);
        sorted.sort(java.util.Comparator.comparing(Enum::ordinal));
        requiredSorted.sort(java.util.Comparator.comparing(Enum::ordinal));

        return sorted.equals(requiredSorted);
    }

    /**
     * Creates a new builder for a RitualRecipe.
     *
     * @param spellId the spell ID
     * @return a new builder
     */
    public static Builder builder(String spellId) {
        return new Builder(spellId);
    }

    public static class Builder {
        private final String spellId;
        private String displayName;
        private List<Ingredient> requiredIngredients = List.of();
        private int requiredFillLevel = 3;
        private boolean requiresNearbyPlayer = true;
        private long ritualDurationTicks = 100;
        private boolean moonPhaseRequired = false;
        private int requiredMoonPhase = 0;
        private boolean weatherRequired = false;
        private String requiredWeather = "clear";

        Builder(String spellId) {
            this.spellId = spellId;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder ingredients(Ingredient... ingredients) {
            this.requiredIngredients = List.of(ingredients);
            return this;
        }

        public Builder requiredFillLevel(int level) {
            this.requiredFillLevel = level;
            return this;
        }

        public Builder requiresNearbyPlayer(boolean required) {
            this.requiresNearbyPlayer = required;
            return this;
        }

        public Builder ritualDuration(long ticks) {
            this.ritualDurationTicks = ticks;
            return this;
        }

        public Builder moonPhase(int phase) {
            this.moonPhaseRequired = true;
            this.requiredMoonPhase = phase;
            return this;
        }

        public Builder weather(String weather) {
            this.weatherRequired = true;
            this.requiredWeather = weather;
            return this;
        }

        public RitualRecipe build() {
            if (displayName == null) {
                displayName = spellId;
            }
            return new RitualRecipe(this);
        }
    }
}
