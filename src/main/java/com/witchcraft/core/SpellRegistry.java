package com.witchcraft.core;

import com.witchcraft.Witchcraft;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Central registry for all spells in the plugin.
 * Spells define effects only. Rituals (cauldron + ingredients) and
 * incantations (spoken words) are separate systems that trigger spells.
 */
public class SpellRegistry {

    private final Witchcraft plugin;
    private final Map<String, Spell> spells = new ConcurrentHashMap<>();

    public SpellRegistry(Witchcraft plugin) {
        this.plugin = plugin;
        loadSpells();
    }

    private void loadSpells() {
        registerBuiltInSpells();

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
        registerSpell(new com.witchcraft.spells.ritual.SummoningRitual(plugin));

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

    public void registerSpell(Spell spell) {
        spells.put(spell.getId(), spell);
    }

    public void unregisterSpell(String spellId) {
        spells.remove(spellId);
    }

    public Spell getSpell(String spellId) {
        return spells.get(spellId);
    }

    public Collection<Spell> getAllSpells() {
        return Collections.unmodifiableCollection(spells.values());
    }

    public Collection<Spell> getSpellsByCategory(SpellCategory category) {
        return spells.values().stream()
                .filter(s -> s.getCategory() == category)
                .toList();
    }

    private void loadSpellFromFile(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("spell");
        if (section == null) return;

        String id = section.getString("id");
        String displayName = section.getString("display-name");
        String categoryName = section.getString("category");
        SpellCategory category = SpellCategory.valueOf(categoryName.toUpperCase());

        long cooldown = section.getLong("cooldown", 600);
        int xpCost = section.getInt("xp-cost", 3);
        double successChance = section.getDouble("success-chance", 0.8);
        double failureChance = section.getDouble("failure-chance", 0.15);
        double backfireChance = section.getDouble("backfire-chance", 0.05);
        String permission = section.getString("permission", "witchcraft.cast");

        Spell spell = new ConfiguredSpell(plugin, id, displayName, category, cooldown,
                xpCost, successChance, failureChance, backfireChance, permission, section);
        registerSpell(spell);
    }

    /**
     * A spell loaded from configuration files.
     */
    private static class ConfiguredSpell extends Spell {

        private final ConfigurationSection config;

        ConfiguredSpell(Witchcraft plugin, String id, String displayName, SpellCategory category,
                        long cooldown, int xpCost, double successChance, double failureChance,
                        double backfireChance, String permission, ConfigurationSection config) {
            super(plugin, id, displayName, category, cooldown, xpCost, successChance,
                    failureChance, backfireChance, permission);
            this.config = config;
        }

        @Override
        public SpellResult execute(org.bukkit.entity.Player caster, org.bukkit.Location location,
                                   org.bukkit.entity.Player target) {
            var effects = config.getConfigurationSection("effects");
            if (effects != null) {
                applyEffects(caster, location, target, effects);
            }
            return rollResult();
        }

        private void applyEffects(org.bukkit.entity.Player caster, org.bukkit.Location location,
                                  org.bukkit.entity.Player target, ConfigurationSection effects) {
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
