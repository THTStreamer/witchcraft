package com.witchcraft.core;

import org.bukkit.Material;

/**
 * Represents an ingredient used in rituals.
 */
public enum Ingredient {

    // Common ingredients
    NETHER_WART("Nether Wart", Material.NETHER_WART),
    REDSTONE("Redstone Dust", Material.REDSTONE),
    GLOWSTONE_DUST("Glowstone Dust", Material.GLOWSTONE_DUST),
    BONE_MEAL("Bone Meal", Material.BONE_MEAL),
    SUGAR("Sugar", Material.SUGAR),
    GUNPOWDER("Gunpowder", Material.GUNPOWDER),
    SPIDER_EYE("Spider Eye", Material.SPIDER_EYE),
    FERMENTED_SPIDER_EYE("Fermented Spider Eye", Material.FERMENTED_SPIDER_EYE),
    BLAZE_POWDER("Blaze Powder", Material.BLAZE_POWDER),
    MAGMA_CREAM("Magma Cream", Material.MAGMA_CREAM),
    PHANTOM_MEMBRANE("Phantom Membrane", Material.PHANTOM_MEMBRANE),
    ECHO_SHARD("Echo Shard", Material.ECHO_SHARD),
    AMETHYST_SHARD("Amethyst Shard", Material.AMETHYST_SHARD),
    QUARTZ("Nether Quartz", Material.QUARTZ),
    COAL("Coal", Material.COAL),
    CHARCOAL("Charcoal", Material.CHARCOAL),
    FEATHER("Feather", Material.FEATHER),
    FLINT("Flint", Material.FLINT),
    OBSIDIAN("Obsidian", Material.OBSIDIAN),
    CRYING_OBSIDIAN("Crying Obsidian", Material.CRYING_OBSIDIAN),
    GHAST_TEAR("Ghast Tear", Material.GHAST_TEAR),
    DRAGON_BREATH("Dragon's Breath", Material.DRAGON_BREATH),
    SHULKER_SHELL("Shulker Shell", Material.SHULKER_SHELL),
    NAUTILUS_SHELL("Nautilus Shell", Material.NAUTILUS_SHELL),
    HEART_OF_THE_SEA("Heart of the Sea", Material.HEART_OF_THE_SEA),
    ENDER_PEARL("Ender Pearl", Material.ENDER_PEARL),
    PRISMARINE_SHARD("Prismarine Shard", Material.PRISMARINE_SHARD),
    PRISMARINE_CRYSTALS("Prismarine Crystals", Material.PRISMARINE_CRYSTALS),
    COPPER_INGOT("Copper Ingot", Material.COPPER_INGOT),
    IRON_INGOT("Iron Ingot", Material.IRON_INGOT),
    GOLD_INGOT("Gold Ingot", Material.GOLD_INGOT);

    private final String displayName;
    private final Material material;

    Ingredient(String displayName, Material material) {
        this.displayName = displayName;
        this.material = material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    /**
     * Finds an ingredient by material.
     *
     * @param material the material to search for
     * @return the ingredient, or null if not found
     */
    public static Ingredient fromMaterial(Material material) {
        for (Ingredient ingredient : values()) {
            if (ingredient.material == material) {
                return ingredient;
            }
        }
        return null;
    }

    /**
     * Finds an ingredient by name (case-insensitive).
     *
     * @param name the name to search for
     * @return the ingredient, or null if not found
     */
    public static Ingredient fromName(String name) {
        String normalized = name.toUpperCase().replace(" ", "_");
        for (Ingredient ingredient : values()) {
            if (ingredient.name().equals(normalized)) {
                return ingredient;
            }
        }
        return null;
    }
}
