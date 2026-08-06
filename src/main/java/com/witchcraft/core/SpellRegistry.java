package com.witchcraft.core;

import com.witchcraft.Witchcraft;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Central registry for all spells in the plugin.
 * Loads spell definitions from configuration files.
 */
public class SpellRegistry {

    private final Witchcraft plugin;
    private final Map<String, Spell> spells = new ConcurrentHashMap<>();
    private final Map<String, Spell> incantationSpells = new ConcurrentHashMap<>();

    public SpellRegistry(Witchcraft plugin) {
        this.plugin = plugin;
        loadSpells();
    }

    /**
     * Loads all spells from the spells directory and registers built-in spells.
     */
    private void loadSpells() {
        // Register built-in spells
        registerBuiltInSpells();

        // Load custom spells from configuration
        File spellsDir = new File(plugin.getDataFolder(), "spells");
        if (!spellsDir.exists()) {
            spellsDir.mkdirs();
        }

        File[] spellFiles = spellsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (spellFiles != null) {
            for (File file : spellFiles) {
                try {
                    loadSpellFromFile(file);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to load spell from " + file.getName(), e);
                }
            }
        }

        plugin.getLogger().info("Registered " + spells.size() + " spells.");
    }

    /**
     * Registers all built-in spells.
     */
    private void registerBuiltInSpells() {
        // Curse spells
        registerSpell(new com.witchcraft.spells.curse.MiningFatigueCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.BadLuckCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.SlowHealingCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.WeaknessCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.PhantomActivityCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.CropFailureCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.AnimalBreedingCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.FishingLuckCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.SilenceCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.BlindnessCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.SlownessCurse(plugin));
        registerSpell(new com.witchcraft.spells.curse.HungerCurse(plugin));

        // Ritual spells
        registerSpell(new com.witchcraft.spells.ritual.FertilityRitual(plugin));
        registerSpell(new com.witchcraft.spells.ritual.MobPreventionRitual(plugin));
        registerSpell(new com.witchcraft.spells.ritual.CleansingRitual(plugin));
        registerSpell(new com.witchcraft.spells.ritual.GrowthRitual(plugin));
        registerSpell(new com.witchcraft.spells.ritual.WaterPurificationRitual(plugin));
        registerSpell(new com.witchcraft.spells.ritual.SoulHarvestRitual(plugin));
        registerSpell(new com.witchcraft.spells.ritual.BindingRitual(plugin));
        registerSpell(new com.witchcraft.spells.ritual.BanishmentRitual(plugin));

        // Protection spells
        registerSpell(new com.witchcraft.spells.protection.ProtectionRitual(plugin));
        registerSpell(new com.witchcraft.spells.protection.AntiScryingProtection(plugin));
        registerSpell(new com.witchcraft.spells.protection.FireShieldRitual(plugin));
        registerSpell(new com.witchcraft.spells.protection.ProjectileShieldRitual(plugin));

        // Divination spells
        registerSpell(new com.witchcraft.spells.divination.ScryingRitual(plugin));
        registerSpell(new com.witchcraft.spells.divination.TreasureScryingRitual(plugin));
        registerSpell(new com.witchcraft.spells.divination.AuraSightRitual(plugin));
    }

    /**
     * Registers a spell.
     *
     * @param spell the spell to register
     */
    public void registerSpell(Spell spell) {
        spells.put(spell.getId(), spell);
        if (spell.getIncantation() != null) {
            incantationSpells.put(normalizeIncantation(spell.getIncantation()), spell);
        }
    }

    /**
     * Unregisters a spell.
     *
     * @param spellId the spell ID to unregister
     */
    public void unregisterSpell(String spellId) {
        Spell removed = spells.remove(spellId);
        if (removed != null && removed.getIncantation() != null) {
            incantationSpells.remove(normalizeIncantation(removed.getIncantation()));
        }
    }

    /**
     * Gets a spell by ID.
     *
     * @param spellId the spell ID
     * @return the spell, or null if not found
     */
    public Spell getSpell(String spellId) {
        return spells.get(spellId);
    }

    /**
     * Gets a spell by incantation.
     *
     * @param incantation the incantation to search for
     * @return the spell, or null if not found
     */
    public Spell getSpellByIncantation(String incantation) {
        return incantationSpells.get(normalizeIncantation(incantation));
    }

    /**
     * Gets all registered spells.
     *
     * @return unmodifiable collection of spells
     */
    public Collection<Spell> getAllSpells() {
        return Collections.unmodifiableCollection(spells.values());
    }

    /**
     * Gets all spells in a specific category.
     *
     * @param category the category to filter by
     * @return collection of spells in the category
     */
    public Collection<Spell> getSpellsByCategory(SpellCategory category) {
        return spells.values().stream()
                .filter(s -> s.getCategory() == category)
                .toList();
    }

    /**
     * Gets all spells that can be cast via incantation.
     *
     * @return unmodifiable collection of incantation spells
     */
    public Collection<Spell> getIncantationSpells() {
        return Collections.unmodifiableCollection(incantationSpells.values());
    }

    /**
     * Normalizes an incantation by removing punctuation, extra spaces,
     * and converting to lowercase.
     *
     * @param incantation the raw incantation
     * @return the normalized incantation
     */
    public static String normalizeIncantation(String incantation) {
        return incantation.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Loads a spell definition from a YAML file.
     */
    private void loadSpellFromFile(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("spell");
        if (section == null) return;

        String id = section.getString("id");
        String displayName = section.getString("display-name");
        String categoryName = section.getString("category");
        SpellCategory category = SpellCategory.valueOf(categoryName.toUpperCase());

        // Load ingredients
        var ingredientNames = section.getStringList("ingredients");
        var ingredients = new java.util.ArrayList<Ingredient>();
        for (String name : ingredientNames) {
            Ingredient ing = Ingredient.fromName(name);
            if (ing != null) {
                ingredients.add(ing);
            }
        }

        String incantation = section.getString("incantation");
        long cooldown = section.getLong("cooldown", 600);
        int xpCost = section.getInt("xp-cost", 3);
        double successChance = section.getDouble("success-chance", 0.8);
        double failureChance = section.getDouble("failure-chance", 0.15);
        double backfireChance = section.getDouble("backfire-chance", 0.05);
        String permission = section.getString("permission", "witchcraft.cast");

        // Create a dynamic spell from configuration
        Spell spell = new ConfiguredSpell(plugin, id, displayName, category, ingredients,
                incantation, cooldown, xpCost, successChance, failureChance, backfireChance,
                permission, section);
        registerSpell(spell);
    }

    /**
     * A spell loaded from configuration files.
     */
    private static class ConfiguredSpell extends Spell {

        private final ConfigurationSection config;

        ConfiguredSpell(Witchcraft plugin, String id, String displayName, SpellCategory category,
                        java.util.List<Ingredient> ingredients, String incantation, long cooldown,
                        int xpCost, double successChance, double failureChance, double backfireChance,
                        String permission, ConfigurationSection config) {
            super(plugin, id, displayName, category, ingredients, incantation, cooldown,
                    xpCost, successChance, failureChance, backfireChance, permission);
            this.config = config;
        }

        @Override
        public SpellResult execute(org.bukkit.entity.Player caster, org.bukkit.Location location,
                                   org.bukkit.entity.Player target) {
            // Execute configured effects
            var effects = config.getConfigurationSection("effects");
            if (effects != null) {
                applyEffects(caster, location, target, effects);
            }
            return rollResult();
        }

        private void applyEffects(org.bukkit.entity.Player caster, org.bukkit.Location location,
                                  org.bukkit.entity.Player target, ConfigurationSection effects) {
            // Apply potion effects
            var potions = effects.getConfigurationSection("potions");
            if (potions != null) {
                for (String key : potions.getKeys(false)) {
                    var potionSection = potions.getConfigurationSection(key);
                    if (potionSection != null) {
                        try {
                            var effectType = org.bukkit.potion.PotionEffectType.getByName(
                                    potionSection.getString("type", ""));
                            if (effectType != null) {
                                int amplifier = potionSection.getInt("amplifier", 0);
                                int duration = potionSection.getInt("duration", 200);
                                org.bukkit.entity.LivingEntity entity = target != null ? target : caster;
                                entity.addPotionEffect(new org.bukkit.potion.PotionEffect(
                                        effectType, duration, amplifier));
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            // Apply particles
            var particleType = effects.getString("particle");
            if (particleType != null && location != null) {
                try {
                    var particle = org.bukkit.Particle.valueOf(particleType.toUpperCase());
                    location.getWorld().spawnParticle(particle, location, 50);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
