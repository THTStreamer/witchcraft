package com.witchcraft.core;

/**
 * Categories for organizing spells.
 */
public enum SpellCategory {

    CURSE("Curse", "Dark spells that afflict targets"),
    PROTECTION("Protection", "Defensive spells and wards"),
    DIVINATION("Divination", "Scrying and revelation spells"),
    FERTILITY("Fertility", "Blessings for growth and abundance"),
    WARDING("Warding", "Prevention and area denial spells"),
    CLEANSING("Cleansing", "Removal of negative effects"),
    ENCHANTMENT("Enchantment", "Bolstering and enhancement spells");

    private final String displayName;
    private final String description;

    SpellCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
