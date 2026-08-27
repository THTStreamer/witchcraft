package com.witchcraft.coven;

import com.witchcraft.Witchcraft;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for all coven spells.
 */
public class CovenSpellRegistry {

    private final Witchcraft plugin;
    private final Map<String, CovenSpell> spells = new ConcurrentHashMap<>();

    public CovenSpellRegistry(Witchcraft plugin) {
        this.plugin = plugin;
        registerDefaultSpells();
    }

    private void registerDefaultSpells() {
        // Wrath of the Coven - requires 3 members, deals damage in an area
        register(new CovenSpell(plugin,
                "wrath_of_the_coven",
                "Wrath of the Coven",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 15.0,
                java.util.List.of(
                        "ira congregatio spiritus",
                        "furor antiquus invocare",
                        "potentia devastare hostes"
                ),
                new com.witchcraft.spells.ritual.BanishmentRitual(plugin),
                600));

        // Unity Shield - requires 2 members, grants protection to all nearby
        register(new CovenSpell(plugin,
                "unity_shield",
                "Unity Shield",
                com.witchcraft.core.SpellCategory.PROTECTION,
                2, 10.0,
                java.util.List.of(
                        "unitas scutum protectionis",
                        "congregatio defensare animas"
                ),
                new com.witchcraft.spells.protection.ProtectionRitual(plugin),
                400));

        // Shared Sight - requires 2 members, all members see what one sees
        register(new CovenSpell(plugin,
                "shared_sight",
                "Shared Sight",
                com.witchcraft.core.SpellCategory.DIVINATION,
                2, 10.0,
                java.util.List.of(
                        "oculus communis videre",
                        "animae nexus revelare"
                ),
                new com.witchcraft.spells.divination.ScryingRitual(plugin),
                400));

        // Storm Calling - requires 3 members, calls lightning on enemies
        register(new CovenSpell(plugin,
                "storm_calling_ritual",
                "Storm Calling",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 20.0,
                java.util.List.of(
                        "tempestas vocare fulmen",
                        "iratus nubium potestas",
                        "caelum irritare hostes"
                ),
                new com.witchcraft.spells.ritual.StormCallingRitual(plugin),
                600));

        // Soul Drain - requires 2 members, drains life from target
        register(new CovenSpell(plugin,
                "soul_drain_ritual",
                "Soul Drain",
                com.witchcraft.core.SpellCategory.CURSE,
                2, 15.0,
                java.util.List.of(
                        "anima exhaurire vitam",
                        "vis vitalis subtrahere"
                ),
                new com.witchcraft.spells.ritual.SoulDrainRitual(plugin),
                400));

        // Doom - requires 3 members, devastating area curse
        register(new CovenSpell(plugin,
                "doom_ritual",
                "Ritual of Doom",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 25.0,
                java.util.List.of(
                        "exitium invocare magnus",
                        "perditio anima consumere",
                        "doom aeternum hostes"
                ),
                new com.witchcraft.spells.ritual.DoomRitual(plugin),
                800));

        // === HIGH-TIER COVEN SPELLS (4-5+ members) ===

        // Apocalypse - requires 5 members, devastating area annihilation
        register(new CovenSpell(plugin,
                "apocalypse_coven_spell",
                "Apocalypse",
                com.witchcraft.core.SpellCategory.CURSE,
                5, 35.0,
                java.util.List.of(
                        "exitium mundi invocare",
                        "apocalypsis venire tenebris",
                        "perpetua damnatio animarum",
                        "cineres mundi consumere",
                        "apocalypse eternum dominare"
                ),
                new com.witchcraft.spells.ritual.ApocalypseCovenSpell(plugin),
                1200));

        // Armageddon - requires 4 members, fire and lightning devastation
        register(new CovenSpell(plugin,
                "armageddon_coven_spell",
                "Armageddon",
                com.witchcraft.core.SpellCategory.CURSE,
                4, 30.0,
                java.util.List.of(
                        "ignis aeternus descendat",
                        "fulmen iratus caelum",
                        "armageddon hostes consumere",
                        "cineres hostium flammare"
                ),
                new com.witchcraft.spells.ritual.ArmageddonCovenSpell(plugin),
                1000));

        // Mass Transmutation - requires 4 members, strips all buffs and weakens
        register(new CovenSpell(plugin,
                "mass_transmutation_coven_spell",
                "Mass Transmutation",
                com.witchcraft.core.SpellCategory.CURSE,
                4, 25.0,
                java.util.List.of(
                        "transmutatio magna animarum",
                        "vis auferre potentiam",
                        "debilitas in perpetuum",
                        "transmutare hostes in nihilum"
                ),
                new com.witchcraft.spells.ritual.MassTransmutationCovenSpell(plugin),
                800));

        // Soul Harvest - requires 4 members, drains life from all enemies
        register(new CovenSpell(plugin,
                "soul_harvest_coven_spell",
                "Soul Harvest",
                com.witchcraft.core.SpellCategory.CURSE,
                4, 25.0,
                java.util.List.of(
                        "anima hostium colligere",
                        "vis vitalis subtrahere",
                        "harvest animarum potestas",
                        "vita furari in perpetuum"
                ),
                new com.witchcraft.spells.ritual.SoulHarvestCovenSpell(plugin),
                800));

        // Eternal Damnation - requires 5 members, the ultimate curse
        register(new CovenSpell(plugin,
                "eternal_damnation_coven_spell",
                "Eternal Damnation",
                com.witchcraft.core.SpellCategory.CURSE,
                5, 40.0,
                java.util.List.of(
                        "damnatio aeterna invocare",
                        "perpetua tenebris consumere",
                        "anima in inferno ligare",
                        "maledictio sempiterna descendat",
                        "eternal damnation hostes"
                ),
                new com.witchcraft.spells.ritual.EternalDamnationCovenSpell(plugin),
                1600));

        // === NEW COVEN CURSES (10) ===
        register(new CovenSpell(plugin,
                "writhing_roots_curse",
                "Curse of Writhing Roots",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 20.0,
                java.util.List.of(
                        "radices torquere ligare",
                        "terra constringere hostes",
                        "vinea strangulare inimicos"
                ),
                new com.witchcraft.spells.ritual.WrithingRootsCurse(plugin),
                600));
        register(new CovenSpell(plugin,
                "sinking_mire_curse",
                "Curse of the Sinking Mire",
                com.witchcraft.core.SpellCategory.CURSE,
                2, 15.0,
                java.util.List.of(
                        "palus vorare profundus",
                        "lutum trahere deorsum"
                ),
                new com.witchcraft.spells.ritual.SinkingMireCurse(plugin),
                400));
        register(new CovenSpell(plugin,
                "withered_fields_curse",
                "Curse of Withered Fields",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 25.0,
                java.util.List.of(
                        "ager marcescere arefacere",
                        "messis perdere infelix",
                        "terra sterilis maledicta"
                ),
                new com.witchcraft.spells.ritual.WitheredFieldsCurse(plugin),
                600));
        register(new CovenSpell(plugin,
                "howling_void_curse",
                "Curse of the Howling Void",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 25.0,
                java.util.List.of(
                        "inanis ululare vorax",
                        "vacuum clamare tenebris",
                        "abyssus devorare animas"
                ),
                new com.witchcraft.spells.ritual.HowlingVoidCurse(plugin),
                600));
        register(new CovenSpell(plugin,
                "brittle_earth_curse",
                "Curse of Brittle Earth",
                com.witchcraft.core.SpellCategory.CURSE,
                2, 15.0,
                java.util.List.of(
                        "terra fragilis rumpere",
                        "solum infirmus fatiscere"
                ),
                new com.witchcraft.spells.ritual.BrittleEarthCurse(plugin),
                400));
        register(new CovenSpell(plugin,
                "pallid_plague_curse",
                "Curse of the Pallid Plague",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 20.0,
                java.util.List.of(
                        "pallidus pestis serpere",
                        "morbus luridus consumere",
                        "contagium expandere late"
                ),
                new com.witchcraft.spells.ritual.PallidPlagueCurse(plugin),
                600));
        register(new CovenSpell(plugin,
                "starless_night_curse",
                "Curse of the Starless Night",
                com.witchcraft.core.SpellCategory.CURSE,
                3, 20.0,
                java.util.List.of(
                        "nox sine astris obscura",
                        "caelum vacuus tenebris",
                        "sidera extinguere perpetuo"
                ),
                new com.witchcraft.spells.ritual.StarlessNightCurse(plugin),
                600));
        register(new CovenSpell(plugin,
                "crushing_weight_curse",
                "Curse of Crushing Weight",
                com.witchcraft.core.SpellCategory.CURSE,
                2, 15.0,
                java.util.List.of(
                        "pondus immensum opprimere",
                        "gravitas conculcare hostes"
                ),
                new com.witchcraft.spells.ritual.CrushingWeightCurse(plugin),
                400));
        register(new CovenSpell(plugin,
                "fetid_bog_curse",
                "Curse of the Fetid Bog",
                com.witchcraft.core.SpellCategory.CURSE,
                2, 15.0,
                java.util.List.of(
                        "palus foetida venenare",
                        "uligo tabida inficere"
                ),
                new com.witchcraft.spells.ritual.FetidBogCurse(plugin),
                400));
        register(new CovenSpell(plugin,
                "eternal_night_curse",
                "Curse of Eternal Night",
                com.witchcraft.core.SpellCategory.CURSE,
                4, 30.0,
                java.util.List.of(
                        "nox aeterna descendat",
                        "tenebrae perpetua regnare",
                        "sol extinguere in aeternum",
                        "caligo infinita dominare"
                ),
                new com.witchcraft.spells.ritual.EternalNightCurse(plugin),
                800));
    }

    public void register(CovenSpell spell) {
        spells.put(spell.getId(), spell);
    }

    public CovenSpell getSpell(String id) {
        return spells.get(id);
    }

    public Collection<CovenSpell> getAllSpells() {
        return java.util.Collections.unmodifiableCollection(spells.values());
    }

    /**
     * Checks if any coven spell matches the given message.
     *
     * @param message the chat message
     * @return the matching coven spell, or null
     */
    public CovenSpell findByMessage(String message) {
        String normalized = message.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();

        for (CovenSpell spell : spells.values()) {
            for (String line : spell.getIncantationLines()) {
                String normalizedLine = line.toLowerCase()
                        .replaceAll("[^a-z0-9\\s]", "")
                        .replaceAll("\\s+", " ")
                        .trim();
                if (normalized.equals(normalizedLine)) {
                    return spell;
                }
            }
        }
        return null;
    }
}
