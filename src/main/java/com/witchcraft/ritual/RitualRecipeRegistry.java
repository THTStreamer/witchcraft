package com.witchcraft.ritual;

import com.witchcraft.core.Ingredient;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central registry of all ritual recipes.
 * Each recipe maps a set of ingredients to a spell that can be cast via cauldron.
 */
public class RitualRecipeRegistry {

    private final Map<String, RitualRecipe> recipes = new LinkedHashMap<>();

    public RitualRecipeRegistry() {
        registerDefaultRecipes();
    }

    /**
     * Registers all default ritual recipes.
     */
    private void registerDefaultRecipes() {
        // === CURSE RITUALS ===
        // Curse of the Deep Mine: Nether Wart + Redstone + Coal
        register(RitualRecipe.builder("mining_fatigue_curse")
                .displayName("Curse of the Deep Mine")
                .ingredients(Ingredient.NETHER_WART, Ingredient.REDSTONE, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // Curse of Misfortune: Spider Eye + Fermented Spider Eye + Coal
        register(RitualRecipe.builder("bad_luck_curse")
                .displayName("Curse of Misfortune")
                .ingredients(Ingredient.SPIDER_EYE, Ingredient.FERMENTED_SPIDER_EYE, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // Curse of the Withering: Bone Meal + Ghast Tear + Coal
        register(RitualRecipe.builder("slow_healing_curse")
                .displayName("Curse of the Withering")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.GHAST_TEAR, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // Curse of Feebleness: Bone Meal + Sugar + Coal
        register(RitualRecipe.builder("weakness_curse")
                .displayName("Curse of Feebleness")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.SUGAR, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // Curse of the Night Watch: Phantom Membrane + Echo Shard + Coal
        // Requires night (moon phase 4 = full moon)
        register(RitualRecipe.builder("phantom_activity_curse")
                .displayName("Curse of the Night Watch")
                .ingredients(Ingredient.PHANTOM_MEMBRANE, Ingredient.ECHO_SHARD, Ingredient.COAL)
                .ritualDuration(120)
                .moonPhase(4)
                .build());

        // Curse of Barren Fields: Bone Meal + Sugar + Nether Wart
        register(RitualRecipe.builder("crop_failure_curse")
                .displayName("Curse of Barren Fields")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.SUGAR, Ingredient.NETHER_WART)
                .ritualDuration(100)
                .build());

        // Curse of Sterility: Bone Meal + Spider Eye + Nether Wart
        register(RitualRecipe.builder("animal_breeding_curse")
                .displayName("Curse of Sterility")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.SPIDER_EYE, Ingredient.NETHER_WART)
                .ritualDuration(100)
                .build());

        // Curse of the Empty Net: Nautilus Shell + Prismarine Shard + Coal
        register(RitualRecipe.builder("fishing_luck_curse")
                .displayName("Curse of the Empty Net")
                .ingredients(Ingredient.NAUTILUS_SHELL, Ingredient.PRISMARINE_SHARD, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // === FERTILITY RITUAL ===
        // Blessing of Abundance: Bone Meal + Glowstone Dust + Sugar
        // Requires clear weather
        register(RitualRecipe.builder("fertility_ritual")
                .displayName("Blessing of Abundance")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.GLOWSTONE_DUST, Ingredient.SUGAR)
                .ritualDuration(200)
                .weather("clear")
                .build());

        // === WARDING RITUAL ===
        // Guardian's Ward: Obsidian + Echo Shard + Bone Meal
        register(RitualRecipe.builder("mob_prevention_ritual")
                .displayName("Guardian's Ward")
                .ingredients(Ingredient.OBSIDIAN, Ingredient.ECHO_SHARD, Ingredient.BONE_MEAL)
                .ritualDuration(200)
                .build());

        // === CLEANSING RITUAL ===
        // Rite of Purification: Ghast Tear + Sugar + Bone Meal
        register(RitualRecipe.builder("cleansing_ritual")
                .displayName("Rite of Purification")
                .ingredients(Ingredient.GHAST_TEAR, Ingredient.SUGAR, Ingredient.BONE_MEAL)
                .ritualDuration(150)
                .build());

        // === PROTECTION RITUALS ===
        // Ward of Protection: Obsidian + Amethyst Shard + Glowstone Dust
        register(RitualRecipe.builder("protection_ritual")
                .displayName("Ward of Protection")
                .ingredients(Ingredient.OBSIDIAN, Ingredient.AMETHYST_SHARD, Ingredient.GLOWSTONE_DUST)
                .ritualDuration(200)
                .build());

        // Veil of Obscurity: Echo Shard + Amethyst Shard + Crying Obsidian
        register(RitualRecipe.builder("anti_scrying_protection")
                .displayName("Veil of Obscurity")
                .ingredients(Ingredient.ECHO_SHARD, Ingredient.AMETHYST_SHARD, Ingredient.CRYING_OBSIDIAN)
                .ritualDuration(200)
                .build());

        // === DIVINATION RITUAL ===
        // Mirror of the Soul: Echo Shard + Amethyst Shard + Ender Pearl
        register(RitualRecipe.builder("scrying_ritual")
                .displayName("Mirror of the Soul")
                .ingredients(Ingredient.ECHO_SHARD, Ingredient.AMETHYST_SHARD, Ingredient.ENDER_PEARL)
                .ritualDuration(300)
                .build());

        // === NEW CURSE RITUALS ===
        // Curse of the Muted Tongue: Gunpowder + Fermented Spider Eye + Coal
        register(RitualRecipe.builder("silence_curse")
                .displayName("Curse of the Muted Tongue")
                .ingredients(Ingredient.GUNPOWDER, Ingredient.FERMENTED_SPIDER_EYE, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // Curse of the Shrouded Eye: Fermented Spider Eye + Coal + Nether Quartz
        register(RitualRecipe.builder("blindness_curse")
                .displayName("Curse of the Shrouded Eye")
                .ingredients(Ingredient.FERMENTED_SPIDER_EYE, Ingredient.COAL, Ingredient.QUARTZ)
                .ritualDuration(100)
                .build());

        // Curse of the Iron Boots: Iron Ingot + Sugar + Coal
        register(RitualRecipe.builder("slowness_curse")
                .displayName("Curse of the Iron Boots")
                .ingredients(Ingredient.IRON_INGOT, Ingredient.SUGAR, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // Curse of the Ravenous Maw: Magma Cream + Sugar + Coal
        register(RitualRecipe.builder("hunger_curse")
                .displayName("Curse of the Ravenous Maw")
                .ingredients(Ingredient.MAGMA_CREAM, Ingredient.SUGAR, Ingredient.COAL)
                .ritualDuration(100)
                .build());

        // === NEW FERTILITY RITUALS ===
        // Bloom of the Green Hand: Bone Meal + Glowstone Dust + Nether Quartz
        register(RitualRecipe.builder("growth_ritual")
                .displayName("Bloom of the Green Hand")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.GLOWSTONE_DUST, Ingredient.QUARTZ)
                .ritualDuration(200)
                .build());

        // === NEW CLEANSING RITUALS ===
        // Purification of the Clear Spring: Ghast Tear + Nether Quartz + Bone Meal
        register(RitualRecipe.builder("water_purification_ritual")
                .displayName("Purification of the Clear Spring")
                .ingredients(Ingredient.GHAST_TEAR, Ingredient.QUARTZ, Ingredient.BONE_MEAL)
                .ritualDuration(150)
                .build());

        // === NEW RITUAL SPELLS ===
        // Gathering of Lost Souls: Echo Shard + Nether Quartz + Coal
        register(RitualRecipe.builder("soul_harvest_ritual")
                .displayName("Gathering of Lost Souls")
                .ingredients(Ingredient.ECHO_SHARD, Ingredient.QUARTZ, Ingredient.COAL)
                .ritualDuration(200)
                .build());

        // === NEW CURSE RITUALS (Ritual-based) ===
        // Chains of the Bound Soul: Iron Ingot + Echo Shard + Coal
        register(RitualRecipe.builder("binding_ritual")
                .displayName("Chains of the Bound Soul")
                .ingredients(Ingredient.IRON_INGOT, Ingredient.ECHO_SHARD, Ingredient.COAL)
                .ritualDuration(120)
                .build());

        // Rite of Banishment: Ender Pearl + Echo Shard + Nether Quartz
        register(RitualRecipe.builder("banishment_ritual")
                .displayName("Rite of Banishment")
                .ingredients(Ingredient.ENDER_PEARL, Ingredient.ECHO_SHARD, Ingredient.QUARTZ)
                .ritualDuration(150)
                .build());

        // Calling of the Bound: Ender Pearl + Echo Shard + Amethyst Shard
        register(RitualRecipe.builder("summoning_ritual")
                .displayName("Calling of the Bound")
                .ingredients(Ingredient.ENDER_PEARL, Ingredient.ECHO_SHARD, Ingredient.AMETHYST_SHARD)
                .ritualDuration(200)
                .build());

        // === NEW PROTECTION RITUALS ===
        // Ward of the Immolator: Blaze Powder + Obsidian + Glowstone Dust
        register(RitualRecipe.builder("fire_shield_ritual")
                .displayName("Ward of the Immolator")
                .ingredients(Ingredient.BLAZE_POWDER, Ingredient.OBSIDIAN, Ingredient.GLOWSTONE_DUST)
                .ritualDuration(200)
                .build());

        // Aegis of Deflection: Iron Ingot + Obsidian + Flint
        register(RitualRecipe.builder("projectile_shield_ritual")
                .displayName("Aegis of Deflection")
                .ingredients(Ingredient.IRON_INGOT, Ingredient.OBSIDIAN, Ingredient.FLINT)
                .ritualDuration(200)
                .build());

        // === NEW DIVINATION RITUALS ===
        // Dowsing of the Hidden Way: Echo Shard + Gold Ingot + Amethyst Shard
        register(RitualRecipe.builder("treasure_scrying_ritual")
                .displayName("Dowsing of the Hidden Way")
                .ingredients(Ingredient.ECHO_SHARD, Ingredient.GOLD_INGOT, Ingredient.AMETHYST_SHARD)
                .ritualDuration(300)
                .build());

        // The Witch's Third Eye: Amethyst Shard + Echo Shard + Glowstone Dust
        register(RitualRecipe.builder("aura_sight_ritual")
                .displayName("The Witch's Third Eye")
                .ingredients(Ingredient.AMETHYST_SHARD, Ingredient.ECHO_SHARD, Ingredient.GLOWSTONE_DUST)
                .ritualDuration(300)
                .build());

        // === NEW CURSE RITUALS (Expanded) ===
        // Curse of the Plague: Nether Wart + Spider Eye + Gunpowder
        register(RitualRecipe.builder("plague_curse")
                .displayName("Curse of the Plague")
                .ingredients(Ingredient.NETHER_WART, Ingredient.SPIDER_EYE, Ingredient.GUNPOWDER)
                .ritualDuration(120)
                .build());

        // Curse of Withering: Bone Meal + Coal + Echo Shard
        register(RitualRecipe.builder("withering_curse")
                .displayName("Curse of Withering")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.COAL, Ingredient.ECHO_SHARD)
                .ritualDuration(120)
                .build());

        // Curse of Madness: Nether Quartz + Fermented Spider Eye + Echo Shard
        register(RitualRecipe.builder("confusion_curse")
                .displayName("Curse of Madness")
                .ingredients(Ingredient.QUARTZ, Ingredient.FERMENTED_SPIDER_EYE, Ingredient.ECHO_SHARD)
                .ritualDuration(120)
                .build());

        // === NEW BLESSING RITUALS ===
        // Blessing of the Harvest: Bone Meal + Sugar + Glowstone Dust
        register(RitualRecipe.builder("blessing_of_harvest")
                .displayName("Blessing of the Harvest")
                .ingredients(Ingredient.BONE_MEAL, Ingredient.SUGAR, Ingredient.GLOWSTONE_DUST)
                .ritualDuration(200)
                .build());

        // Blessing of Fortitude: Iron Ingot + Obsidian + Amethyst Shard
        register(RitualRecipe.builder("blessing_of_fortitude")
                .displayName("Blessing of Fortitude")
                .ingredients(Ingredient.IRON_INGOT, Ingredient.OBSIDIAN, Ingredient.AMETHYST_SHARD)
                .ritualDuration(250)
                .build());

        // === NEW DIVINATION RITUALS ===
        // Spirit Walk: Ender Pearl + Echo Shard + Phantom Membrane
        register(RitualRecipe.builder("spirit_walk_ritual")
                .displayName("Spirit Walk")
                .ingredients(Ingredient.ENDER_PEARL, Ingredient.ECHO_SHARD, Ingredient.PHANTOM_MEMBRANE)
                .ritualDuration(200)
                .build());

        // Ritual of Renewal: Ghast Tear + Glowstone Dust + Sugar
        register(RitualRecipe.builder("renewal_ritual")
                .displayName("Ritual of Renewal")
                .ingredients(Ingredient.GHAST_TEAR, Ingredient.GLOWSTONE_DUST, Ingredient.SUGAR)
                .ritualDuration(200)
                .build());

        // The Coven's Eye: Amethyst Shard + Echo Shard + Ender Pearl
        register(RitualRecipe.builder("coven_eye_ritual")
                .displayName("The Coven's Eye")
                .ingredients(Ingredient.AMETHYST_SHARD, Ingredient.ECHO_SHARD, Ingredient.ENDER_PEARL)
                .ritualDuration(250)
                .build());

        // === NEW PROTECTION RITUALS (Expanded) ===
        // Ward of Thorns: Cactus + Iron Ingot + Bone Meal
        register(RitualRecipe.builder("thorn_ward")
                .displayName("Ward of Thorns")
                .ingredients(Ingredient.IRON_INGOT, Ingredient.BONE_MEAL, Ingredient.QUARTZ)
                .ritualDuration(200)
                .build());

        // Ward of Absorption: Gold Ingot + Sugar + Glowstone Dust
        register(RitualRecipe.builder("absorption_ward")
                .displayName("Ward of Absorption")
                .ingredients(Ingredient.GOLD_INGOT, Ingredient.SUGAR, Ingredient.GLOWSTONE_DUST)
                .ritualDuration(200)
                .build());

        // === NEW DIVINATION RITUALS (Expanded) ===
        // Eyes of the Coven: Amethyst Shard + Ender Pearl + Echo Shard
        register(RitualRecipe.builder("player_reveal_ritual")
                .displayName("Eyes of the Coven")
                .ingredients(Ingredient.AMETHYST_SHARD, Ingredient.ENDER_PEARL, Ingredient.ECHO_SHARD)
                .ritualDuration(200)
                .build());

        // === COVEN RITUALS ===
        // Ritual of Shared Power: 2 members required
        // Enhances all nearby coven members with strength and regeneration
        register(RitualRecipe.builder("shared_power_ritual")
                .displayName("Ritual of Shared Power")
                .ingredients(Ingredient.BLAZE_POWDER, Ingredient.GOLD_INGOT, Ingredient.NETHER_WART)
                .ritualDuration(250)
                .covenSize(2)
                .covenRadius(15.0)
                .build());

        // Ritual of the Binding Circle: 3 members required
        // Creates a protective circle that shields all coven members in range
        register(RitualRecipe.builder("binding_circle_ritual")
                .displayName("Ritual of the Binding Circle")
                .ingredients(Ingredient.OBSIDIAN, Ingredient.ECHO_SHARD, Ingredient.AMETHYST_SHARD, Ingredient.QUARTZ)
                .ritualDuration(300)
                .covenSize(3)
                .covenRadius(20.0)
                .build());

        // Ritual of Mass Summons: 2 members required
        // Teleports all online coven members to the cauldron
        register(RitualRecipe.builder("mass_summons_ritual")
                .displayName("Ritual of Mass Summons")
                .ingredients(Ingredient.ENDER_PEARL, Ingredient.ECHO_SHARD, Ingredient.AMETHYST_SHARD)
                .ritualDuration(200)
                .covenSize(2)
                .covenRadius(15.0)
                .build());

        // Ritual of the Dark Harvest: 3 members required
        // A powerful curse that afflicts all enemies near the coven
        register(RitualRecipe.builder("dark_harvest_ritual")
                .displayName("Ritual of the Dark Harvest")
                .ingredients(Ingredient.NETHER_WART, Ingredient.FERMENTED_SPIDER_EYE, Ingredient.GHAST_TEAR, Ingredient.COAL)
                .ritualDuration(350)
                .covenSize(3)
                .covenRadius(25.0)
                .build());

        // Ritual of Storm Calling: 3 members required
        // Calls down lightning on all nearby enemies
        register(RitualRecipe.builder("storm_calling_ritual")
                .displayName("Ritual of Storm Calling")
                .ingredients(Ingredient.BLAZE_POWDER, Ingredient.GUNPOWDER, Ingredient.GHAST_TEAR)
                .ritualDuration(300)
                .covenSize(3)
                .covenRadius(25.0)
                .build());

        // Ritual of Soul Drain: 2 members required
        // Drains life from a target and empowers the coven
        register(RitualRecipe.builder("soul_drain_ritual")
                .displayName("Ritual of Soul Drain")
                .ingredients(Ingredient.ECHO_SHARD, Ingredient.FERMENTED_SPIDER_EYE, Ingredient.COAL)
                .ritualDuration(250)
                .covenSize(2)
                .covenRadius(15.0)
                .build());

        // Ritual of Doom: 3 members required
        // Afflicts all nearby enemies with devastating curses
        register(RitualRecipe.builder("doom_ritual")
                .displayName("Ritual of Doom")
                .ingredients(Ingredient.NETHER_WART, Ingredient.COAL, Ingredient.CRYING_OBSIDIAN)
                .ritualDuration(350)
                .covenSize(3)
                .covenRadius(20.0)
                .build());

        // === HIGH-TIER COVEN RITUALS (4-5+ members) ===
        // Ritual of the Eternal Storm: 4 members required
        // Calls down an endless storm with lightning, wither, and slowness on all enemies
        register(RitualRecipe.builder("eternal_storm_ritual")
                .displayName("Ritual of the Eternal Storm")
                .ingredients(Ingredient.BLAZE_POWDER, Ingredient.GUNPOWDER, Ingredient.GHAST_TEAR, Ingredient.QUARTZ, Ingredient.ECHO_SHARD)
                .ritualDuration(400)
                .covenSize(4)
                .covenRadius(30.0)
                .build());

        // Ritual of the Lich King: 5 members required
        // Summons 8 undead minions and curses all enemies with wither, weakness, and slowness
        register(RitualRecipe.builder("lich_king_ritual")
                .displayName("Ritual of the Lich King")
                .ingredients(Ingredient.ECHO_SHARD, Ingredient.NETHER_WART, Ingredient.BONE_MEAL, Ingredient.COAL, Ingredient.CRYING_OBSIDIAN, Ingredient.GHAST_TEAR)
                .ritualDuration(500)
                .covenSize(5)
                .covenRadius(35.0)
                .build());

        // Ritual of the Blood Moon: 5 members required
        // The ultimate curse - applies every negative effect and massive damage in a huge radius
        register(RitualRecipe.builder("blood_moon_ritual")
                .displayName("Ritual of the Blood Moon")
                .ingredients(Ingredient.NETHER_WART, Ingredient.FERMENTED_SPIDER_EYE, Ingredient.GHAST_TEAR, Ingredient.COAL, Ingredient.DRAGON_BREATH, Ingredient.CRYING_OBSIDIAN)
                .ritualDuration(600)
                .covenSize(5)
                .covenRadius(40.0)
                .build());

        // Ritual of the Eternal Binding: 4 members required
        // Complete immobilization of all enemies with slowness 255 and mining fatigue 255
        register(RitualRecipe.builder("eternal_binding_ritual")
                .displayName("Ritual of the Eternal Binding")
                .ingredients(Ingredient.IRON_INGOT, Ingredient.ECHO_SHARD, Ingredient.AMETHYST_SHARD, Ingredient.ENDER_PEARL, Ingredient.QUARTZ)
                .ritualDuration(400)
                .covenSize(4)
                .covenRadius(25.0)
                .build());

        // Ritual of the World Ender: 5 members required
        // The most devastating ritual - massive damage, all debuffs, halves enemy max health
        register(RitualRecipe.builder("world_ender_ritual")
                .displayName("Ritual of the World Ender")
                .ingredients(Ingredient.DRAGON_BREATH, Ingredient.ECHO_SHARD, Ingredient.NETHER_WART, Ingredient.FERMENTED_SPIDER_EYE, Ingredient.GHAST_TEAR, Ingredient.CRYING_OBSIDIAN, Ingredient.COAL)
                .ritualDuration(700)
                .covenSize(5)
                .covenRadius(50.0)
                .build());
    }

    /**
     * Registers a ritual recipe.
     *
     * @param recipe the recipe to register
     */
    public void register(RitualRecipe recipe) {
        recipes.put(recipe.getSpellId(), recipe);
    }

    /**
     * Gets a recipe by spell ID.
     *
     * @param spellId the spell ID
     * @return the recipe, or null if not found
     */
    public RitualRecipe getBySpellId(String spellId) {
        return recipes.get(spellId);
    }

    /**
     * Finds a recipe that matches the given set of ingredients.
     * Order of ingredients does not matter.
     *
     * @param ingredients the ingredients to match
     * @return the matching recipe, or null if none match
     */
    public RitualRecipe findByIngredients(java.util.List<Ingredient> ingredients) {
        for (RitualRecipe recipe : recipes.values()) {
            if (recipe.matchesIngredients(ingredients)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Gets all registered recipes.
     *
     * @return unmodifiable collection of recipes
     */
    public Collection<RitualRecipe> getAllRecipes() {
        return Collections.unmodifiableCollection(recipes.values());
    }
}
