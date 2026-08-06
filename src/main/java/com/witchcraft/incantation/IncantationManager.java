package com.witchcraft.incantation;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCooldown;
import com.witchcraft.data.PlayerData;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all incantations and their learning/casting.
 */
public class IncantationManager {

    private final Witchcraft plugin;
    private final Map<String, Incantation> incantations = new ConcurrentHashMap<>();
    private final SpellCooldown cooldowns = new SpellCooldown();

    public IncantationManager(Witchcraft plugin) {
        this.plugin = plugin;
        registerDefaultIncantations();
    }

    /**
     * Registers the default incantations.
     */
    private void registerDefaultIncantations() {
        // Curse incantations
        registerIncantation(new Incantation(
                "curse_mining",
                "tenebris ferrum obstaculum",
                "mining_fatigue_curse",
                "Curse of the Deep Mine",
                "Afflicts a target with mining fatigue",
                "deep mine curse", "ferrum tenebris"
        ));

        registerIncantation(new Incantation(
                "curse_luck",
                "fortuna inversa cadat",
                "bad_luck_curse",
                "Curse of Misfortune",
                "Brings bad luck to the target",
                "misfortune curse", "fortuna cadat"
        ));

        registerIncantation(new Incantation(
                "curse_healing",
                "vita sanatio lento",
                "slow_healing_curse",
                "Curse of the Withering",
                "Slows healing on the target",
                "withering curse", "vita lento"
        ));

        registerIncantation(new Incantation(
                "curse_weakness",
                "vis deficiat invalidus",
                "weakness_curse",
                "Curse of Feebleness",
                "Weakens the target",
                "feebleness curse", "vis invalidus"
        ));

        registerIncantation(new Incantation(
                "curse_phantom",
                "umbra vigilare noctis",
                "phantom_activity_curse",
                "Curse of the Night Watch",
                "Increases phantom activity around the target",
                "night watch curse", "umbra noctis"
        ));

        registerIncantation(new Incantation(
                "curse_crops",
                "agricultura maledictio sterile",
                "crop_failure_curse",
                "Curse of Barren Fields",
                "Causes crop failure around the target",
                "barren fields curse", "agricultura sterile"
        ));

        registerIncantation(new Incantation(
                "curse_breeding",
                "procreatio negare infructuosa",
                "animal_breeding_curse",
                "Curse of Sterility",
                "Prevents animals from breeding near the target",
                "sterility curse", "procreatio negare"
        ));

        registerIncantation(new Incantation(
                "curse_fishing",
                "piscis fortuna careat",
                "fishing_luck_curse",
                "Curse of the Empty Net",
                "Reduces fishing luck for the target",
                "empty net curse", "piscis careat"
        ));

        // Protection incantations
        registerIncantation(new Incantation(
                "protection",
                "custodio sanctum aegis",
                "protection_ritual",
                "Ward of Protection",
                "Creates a protective ward against curses",
                "ward protection", "aegis sanctum"
        ));

        registerIncantation(new Incantation(
                "anti_scrying",
                "obscurus ne videar",
                "anti_scrying_protection",
                "Veil of Obscurity",
                "Blocks scrying attempts",
                "veil obscurity", "ne videar"
        ));

        // Divination incantations
        registerIncantation(new Incantation(
                "scrying",
                "speculum anima videre",
                "scrying_ritual",
                "Mirror of the Soul",
                "Allows scrying on another player",
                "mirror soul", "anima videre"
        ));

        // Fertility incantations
        registerIncantation(new Incantation(
                "fertility",
                "terra foecunditas abundet",
                "fertility_ritual",
            "Blessing of Abundance",
                "Blesses the land with fertility",
                "abundance blessing", "foecunditas abundet"
        ));

        // Warding incantations
        registerIncantation(new Incantation(
                "warding",
                "custos praesidio locus",
                "mob_prevention_ritual",
                "Guardian's Ward",
                "Creates a ward against hostile mobs",
                "guardian ward", "custos praesidio"
        ));

        // Cleansing incantations
        registerIncantation(new Incantation(
                "cleansing",
                "purgatio maledictio liberare",
                "cleansing_ritual",
                "Rite of Purification",
                "Removes curses from a player",
                "purification rite", "purgatio liberare"
        ));

        // === NEW CURSE INCANTATIONS ===
        registerIncantation(new Incantation(
                "curse_silence",
                "mutus lingua sileat",
                "silence_curse",
                "Curse of the Muted Tongue",
                "Prevents a target from casting spells",
                "muted tongue curse", "lingua sileat"
        ));

        registerIncantation(new Incantation(
                "curse_blindness",
                "oculus caligo tenebris",
                "blindness_curse",
                "Curse of the Shrouded Eye",
                "Blinds the target",
                "shrouded eye curse", "caligo tenebris"
        ));

        registerIncantation(new Incantation(
                "curse_slowness",
                "gradus lentus ferrum",
                "slowness_curse",
                "Curse of the Iron Boots",
                "Slows the target",
                "iron boots curse", "gradus lentus"
        ));

        registerIncantation(new Incantation(
                "curse_hunger",
                "esuries fames devorat",
                "hunger_curse",
                "Curse of the Ravenous Maw",
                "Makes the target endlessly hungry",
                "ravenous maw curse", "esuries devorat"
        ));

        // === NEW FERTILITY INCANTATIONS ===
        registerIncantation(new Incantation(
                "growth",
                "germen floreant viridis",
                "growth_ritual",
                "Bloom of the Green Hand",
                "Accelerates crop growth in the area",
                "green hand bloom", "germen viridis"
        ));

        // === NEW CLEANSING INCANTATIONS ===
        registerIncantation(new Incantation(
                "water_purification",
                "aqua purificatio fons",
                "water_purification_ritual",
                "Purification of the Clear Spring",
                "Cleanses nearby water sources",
                "clear spring purification", "aqua fons"
        ));

        // === NEW RITUAL INCANTATIONS ===
        registerIncantation(new Incantation(
                "soul_harvest",
                "anima colligere umbrarum",
                "soul_harvest_ritual",
                "Gathering of Lost Souls",
                "Collects ambient soul energy",
                "lost souls gathering", "anima umbrarum"
        ));

        registerIncantation(new Incantation(
                "binding",
                "catena anima vincire",
                "binding_ritual",
                "Chains of the Bound Soul",
                "Binds a target's spirit to a location",
                "bound soul chains", "catena vincire"
        ));

        registerIncantation(new Incantation(
                "banishment",
                "exilium repellere portam",
                "banishment_ritual",
                "Rite of Banishment",
                "Banishes a target to the void",
                "banishment rite", "exilium repellere"
        ));

        // === NEW PROTECTION INCANTATIONS ===
        registerIncantation(new Incantation(
                "fire_shield",
                "ignis scutum immolare",
                "fire_shield_ritual",
                "Ward of the Immolator",
                "Grants fire resistance through ritual",
                "immolator ward", "ignis immolare"
        ));

        registerIncantation(new Incantation(
                "projectile_shield",
                "aegis deflexio projicere",
                "projectile_shield_ritual",
                "Aegis of Deflection",
                "Protects against projectile attacks",
                "deflection aegis", "aegis projicere"
        ));

        // === NEW DIVINATION INCANTATIONS ===
        registerIncantation(new Incantation(
                "treasure_scrying",
                "thesaurus videre via abscondita",
                "treasure_scrying_ritual",
                "Dowsing of the Hidden Way",
                "Reveals nearby hidden structures",
                "hidden way dowsing", "thesaurus abscondita"
        ));

        registerIncantation(new Incantation(
                "aura_sight",
                "oculus tertius aura videre",
                "aura_sight_ritual",
                "The Witch's Third Eye",
                "Reveals a target's inner aura and status",
                "third eye sight", "oculus aura"
        ));
    }

    /**
     * Registers an incantation.
     *
     * @param incantation the incantation to register
     */
    public void registerIncantation(Incantation incantation) {
        incantations.put(incantation.getId(), incantation);
    }

    /**
     * Gets an incantation by ID.
     *
     * @param id the incantation ID
     * @return the incantation, or null if not found
     */
    public Incantation getIncantation(String id) {
        return incantations.get(id);
    }

    /**
     * Gets all registered incantations.
     *
     * @return collection of incantations
     */
    public Collection<Incantation> getAllIncantations() {
        return incantations.values();
    }

    /**
     * Attempts to match chat input to an incantation.
     *
     * @param input the chat input
     * @return the matching incantation, or null if not found
     */
    public Incantation matchIncantation(String input) {
        for (Incantation incantation : incantations.values()) {
            if (incantation.matches(input)) {
                return incantation;
            }
        }
        return null;
    }

    /**
     * Gets the spell cooldown manager for incantations.
     *
     * @return the cooldown manager
     */
    public SpellCooldown getCooldowns() {
        return cooldowns;
    }

    /**
     * Learns an incantation for a player.
     *
     * @param playerId      the player's UUID
     * @param incantationId the incantation ID
     * @return true if successfully learned
     */
    public boolean learnIncantation(java.util.UUID playerId, String incantationId) {
        PlayerData data = plugin.getDataManager().getPlayerData(playerId);
        Incantation incantation = incantations.get(incantationId);
        if (incantation == null) return false;

        if (data.hasLearnedIncantation(incantation.getIncantation())) {
            return false; // Already learned
        }

        data.learnIncantation(incantation.getIncantation());
        return true;
    }

    /**
     * Checks if a player has learned a specific incantation.
     *
     * @param playerId      the player's UUID
     * @param incantationId the incantation ID
     * @return true if the player has learned it
     */
    public boolean hasLearned(java.util.UUID playerId, String incantationId) {
        Incantation incantation = incantations.get(incantationId);
        if (incantation == null) return false;

        PlayerData data = plugin.getDataManager().getPlayerData(playerId);
        return data.hasLearnedIncantation(incantation.getIncantation());
    }
}
