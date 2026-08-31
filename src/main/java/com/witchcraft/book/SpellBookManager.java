package com.witchcraft.book;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Manages all spell books in the game. Generates written book items
 * with properly formatted content for each spell, ritual, and coven spell.
 *
 * Written book limits: ~15 chars/line, ~14 lines/page, max 50 pages.
 */
public class SpellBookManager {

    private final Map<String, SpellBookData> spellBooks = new LinkedHashMap<>();

    public SpellBookManager() {
        registerAllBooks();
    }

    public SpellBookData getBook(String spellId) {
        return spellBooks.get(spellId);
    }

    public Collection<SpellBookData> getAllBooks() {
        return spellBooks.values();
    }

    public int getBookCount() {
        return spellBooks.size();
    }

    public static final String SPELL_BOOK_KEY = "witchcraft_spell_book";
    public static final String LORE_BOOK_KEY = "witchcraft_lore_book";

    /**
     * Creates a written book ItemStack for the given spell ID.
     * Stores spellId in PDC for learning, and paginates content to 15 chars/line,
     * 14 lines/page, 50 pages max (same as GuideBookBuilder).
     *
     * @param spellId the spell ID
     * @return the written book item, or null if not found
     */
    public ItemStack createBookItem(String spellId) {
        SpellBookData data = spellBooks.get(spellId);
        if (data == null) return null;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return null;

        meta.setTitle(data.getTitle());
        meta.setAuthor("The Old Ones");
        meta.setPages(paginate(data.getPages()));
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        // Store spellId in PDC for learning
        meta.getPersistentDataContainer().set(
                new NamespacedKey("witchcraft", SPELL_BOOK_KEY),
                PersistentDataType.STRING, data.getSpellId());
        book.setItemMeta(meta);
        return book;
    }

    /**
     * Checks if an ItemStack is a witchcraft spell book.
     */
    public static boolean isSpellBook(ItemStack item) {
        if (item == null || item.getType() != Material.WRITTEN_BOOK) return false;
        var meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(
                new NamespacedKey("witchcraft", SPELL_BOOK_KEY), PersistentDataType.STRING);
    }

    /**
     * Gets the spellId stored in a spell book's PDC.
     */
    public static String getSpellIdFromBook(ItemStack item) {
        if (!isSpellBook(item)) return null;
        var meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                new NamespacedKey("witchcraft", SPELL_BOOK_KEY), PersistentDataType.STRING);
    }

    /**
     * Paginates raw lines into proper book pages respecting Minecraft limits:
     * 15 visible chars/line, 14 lines/page, 50 pages max. Strips color codes for length.
     */
    private List<String> paginate(List<String> rawPages) {
        // If rawPages already looks like paginated pages (each entry is a page with \n),
        // we still run through formatter to ensure limits.
        // Here rawPages are actually lines that may contain \n already; flatten first.
        List<String> rawLines = new ArrayList<>();
        for (String p : rawPages) {
            // Split existing pages that contain \n into lines
            if (p.contains("\n")) {
                rawLines.addAll(Arrays.asList(p.split("\n", -1)));
            } else {
                rawLines.add(p);
            }
        }
        // Re-paginate using guide book logic
        List<String> pages = new ArrayList<>();
        StringBuilder currentPage = new StringBuilder();
        int lineCount = 0;
        for (String line : rawLines) {
            if (line.equals("\u00A70 ") || line.equals("§0 ")) {
                if (currentPage.length() > 0) {
                    pages.add(currentPage.toString());
                    currentPage = new StringBuilder();
                    lineCount = 0;
                }
                continue;
            }
            if (lineCount >= 14) {
                pages.add(currentPage.toString());
                currentPage = new StringBuilder();
                lineCount = 0;
            }
            List<String> wrapped = wrapLine(line, 15);
            for (String w : wrapped) {
                if (lineCount >= 14) {
                    pages.add(currentPage.toString());
                    currentPage = new StringBuilder();
                    lineCount = 0;
                }
                if (lineCount > 0) currentPage.append("\n");
                currentPage.append(w);
                lineCount++;
            }
        }
        if (currentPage.length() > 0) pages.add(currentPage.toString());
        if (pages.size() > 50) pages = pages.subList(0, 50);
        if (pages.isEmpty()) pages.add("");
        return pages;
    }

    private List<String> wrapLine(String line, int maxLen) {
        List<String> wrapped = new ArrayList<>();
        if (line == null || line.isEmpty()) { wrapped.add(line == null ? "" : line); return wrapped; }
        String stripped = org.bukkit.ChatColor.stripColor(line);
        if (stripped.length() <= maxLen) { wrapped.add(line); return wrapped; }
        if (line.length() <= maxLen) { wrapped.add(line); return wrapped; }
        String[] words = line.split(" ");
        StringBuilder cur = new StringBuilder();
        for (String word : words) {
            String test = cur.isEmpty() ? word : cur + " " + word;
            String testStripped = org.bukkit.ChatColor.stripColor(test);
            if (testStripped.length() > maxLen && !cur.isEmpty()) {
                wrapped.add(cur.toString());
                cur = new StringBuilder(word);
            } else {
                if (!cur.isEmpty()) cur.append(" ");
                cur.append(word);
            }
        }
        if (!cur.isEmpty()) wrapped.add(cur.toString());
        return wrapped;
    }

    /**
     * Gets a random spell book ID suitable for loot tables.
     *
     * @return a random spell book ID
     */
    public String getRandomBookId() {
        List<String> ids = new ArrayList<>(spellBooks.keySet());
        return ids.get(new Random().nextInt(ids.size()));
    }

    // ========== BOOK REGISTRATION ==========

    private void registerAllBooks() {
        registerCurseBooks();
        registerRitualBooks();
        registerBlessingBooks();
        registerProtectionBooks();
        registerDivinationBooks();
        registerCovenRitualBooks();
        registerCovenSpellBooks();
        registerNewCurseBooks();
        registerCovenCurseBooks();
    }

    private void reg(String id, String title, String category, List<String> pages, int price) {
        spellBooks.put(id, new SpellBookData(id, title, category, pages, price));
    }

    // ========== CURSE SPELLS ==========

    private void registerCurseBooks() {
        reg("mining_fatigue_curse", "Curse of the Deep Mine",
            "Curse", List.of(
                "\u00A7lCurse of the Deep Mine",
                "",
                "\u00A77A curse that saps the",
                "\u00A77strength of a miner's arms.",
                "",
                "\u00A7lEffect:",
                "Targets suffer Mining Fatigue",
                "for an extended duration,",
                "making excavation nearly",
                "impossible.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Nether Wart",
                "\u00A77- Redstone Dust",
                "\u00A77- Coal",
                "",
                "\u00A77Perform at a cauldron with",
                "\u00A77water. Throw ingredients in",
                "\u00A77the order listed."
            ), 8);

        reg("bad_luck_curse", "Curse of Misfortune",
            "Curse", List.of(
                "\u00A7lCurse of Misfortune",
                "",
                "\u00A77A hex that turnsfortune",
                "\u00A77against the afflicted.",
                "",
                "\u00A7lEffect:",
                "Target receives Bad Luck,",
                "reducing the quality of",
                "loot found for hours.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Spider Eye",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Coal",
                "",
                "\u00A77Best performed during a",
                "\u00A77new moon for potency."
            ), 8);

        reg("slow_healing_curse", "Curse of the Withering",
            "Curse", List.of(
                "\u00A7lCurse of the Withering",
                "",
                "\u00A77Slows the natural healing",
                "\u00A77process of the body.",
                "",
                "\u00A7lEffect:",
                "Target gains Slowness and",
                "Regeneration is weakened.",
                "Natural healing is slowed.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Ghast Tear",
                "\u00A77- Coal"
            ), 8);

        reg("weakness_curse", "Curse of Feebleness",
            "Curse", List.of(
                "\u00A7lCurse of Feebleness",
                "",
                "\u00A77Drains physical power from",
                "\u00A77the target.",
                "",
                "\u00A7lEffect:",
                "Target suffers Weakness,",
                "reducing melee damage.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Sugar",
                "\u00A77- Coal"
            ), 7);

        reg("phantom_activity_curse", "Curse of the Night Watch",
            "Curse", List.of(
                "\u00A7lCurse of the Night Watch",
                "",
                "\u00A77Summons phantoms to haunt",
                "\u00A77the sleepless target.",
                "",
                "\u00A7lEffect:",
                "Target is afflicted with",
                "Insomnia. Phantoms will",
                "attack if they don't sleep.",
                "Requires moon phase 4.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Phantom Membrane",
                "\u00A77- Echo Shard",
                "\u00A77- Coal"
            ), 10);

        reg("crop_failure_curse", "Curse of Barren Fields",
            "Curse", List.of(
                "\u00A7lCurse of Barren Fields",
                "",
                "\u00A77Withers crops in a wide",
                "\u00A77area around the target.",
                "",
                "\u00A7lEffect:",
                "Crops in a large radius",
                "around the target are",
                "destroyed and farmland",
                "is reverted to dirt.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Sugar",
                "\u00A77- Nether Wart"
            ), 9);

        reg("animal_breeding_curse", "Curse of Sterility",
            "Curse", List.of(
                "\u00A7lCurse of Sterility",
                "",
                "\u00A77Prevents animals from",
                "\u00A77breeding in the area.",
                "",
                "\u00A7lEffect:",
                "Nearby animals cannot",
                "breed for several hours.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Spider Eye",
                "\u00A77- Nether Wart"
            ), 8);

        reg("fishing_luck_curse", "Curse of the Empty Net",
            "Curse", List.of(
                "\u00A7lCurse of the Empty Net",
                "",
                "\u00A77Ruins the target's fishing",
                "\u00A77luck for days.",
                "",
                "\u00A7lEffect:",
                "Target can only catch",
                "junk items while fishing.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Nautilus Shell",
                "\u00A77- Prismarine Shard",
                "\u00A77- Coal"
            ), 8);

        reg("silence_curse", "Curse of the Muted Tongue",
            "Curse", List.of(
                "\u00A7lCurse of the Muted Tongue",
                "",
                "\u00A77Prevents the target from",
                "\u00A77speaking incantations.",
                "",
                "\u00A7lEffect:",
                "Target cannot cast",
                "incantations for a period.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Gunpowder",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Coal"
            ), 9);

        reg("blindness_curse", "Curse of the Shrouded Eye",
            "Curse", List.of(
                "\u00A7lCurse of the Shrouded Eye",
                "",
                "\u00A77Plunges the target into",
                "\u00A77darkness.",
                "",
                "\u00A7lEffect:",
                "Target suffers Blindness",
                "for a duration.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Coal",
                "\u00A77- Nether Quartz"
            ), 7);

        reg("slowness_curse", "Curse of the Iron Boots",
            "Curse", List.of(
                "\u00A7lCurse of the Iron Boots",
                "",
                "\u00A77Makes the target's feet",
                "\u00A77heavy as lead.",
                "",
                "\u00A7lEffect:",
                "Target suffers Slowness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Iron Ingot",
                "\u00A77- Sugar",
                "\u00A77- Coal"
            ), 7);

        reg("hunger_curse", "Curse of the Ravenous Maw",
            "Curse", List.of(
                "\u00A7lCurse of the Ravenous Maw",
                "",
                "\u00A77Creates an unquenchable",
                "\u00A77hunger in the target.",
                "",
                "\u00A7lEffect:",
                "Target suffers Hunger.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Magma Cream",
                "\u00A77- Sugar",
                "\u00A77- Coal"
            ), 7);

        reg("plague_curse", "Curse of the Plague",
            "Curse", List.of(
                "\u00A7lCurse of the Plague",
                "",
                "\u00A77A virulent disease that",
                "\u00A77spreads to nearby players.",
                "",
                "\u00A7lEffect:",
                "Target and nearby players",
                "receive Poison and Nausea.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Nether Wart",
                "\u00A77- Spider Eye",
                "\u00A77- Gunpowder"
            ), 9);

        reg("withering_curse", "Curse of Withering",
            "Curse", List.of(
                "\u00A7lCurse of Withering",
                "",
                "\u00A77Withers the target's very",
                "\u00A77essence.",
                "",
                "\u00A7lEffect:",
                "Target suffers Wither",
                "damage over time.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Coal",
                "\u00A77- Echo Shard"
            ), 9);

        reg("confusion_curse", "Curse of Madness",
            "Curse", List.of(
                "\u00A7lCurse of Madness",
                "",
                "\u00A77Fractures the target's",
                "\u00A77perception of reality.",
                "",
                "\u00A7lEffect:",
                "Target suffers Nausea",
                "and disorientation.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Nether Quartz",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Echo Shard"
            ), 8);
    }

    // ========== RITUAL SPELLS ==========

    private void registerRitualBooks() {
        reg("fertility_ritual", "Blessing of Abundance",
            "Ritual", List.of(
                "\u00A7lBlessing of Abundance",
                "",
                "\u00A77A ritual that enriches the",
                "\u00A77soil and boosts growth.",
                "",
                "\u00A7lEffect:",
                "Growth effect on nearby",
                "crops and plants.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Glowstone Dust",
                "\u00A77- Sugar",
                "",
                "\u00A7lWeather: Clear sky required"
            ), 8);

        reg("growth_ritual", "Bloom of the Green Hand",
            "Ritual", List.of(
                "\u00A7lBloom of the Green Hand",
                "",
                "\u00A77Accelerates plant growth",
                "\u00A77in a wide area.",
                "",
                "\u00A7lEffect:",
                "Massive growth boost to",
                "all nearby vegetation.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Glowstone Dust",
                "\u00A77- Nether Quartz"
            ), 8);

        reg("mob_prevention_ritual", "Guardian's Ward",
            "Ritual", List.of(
                "\u00A7lGuardian's Ward",
                "",
                "\u00A77Creates a protective barrier",
                "\u00A77that repels hostile mobs.",
                "",
                "\u00A7lEffect:",
                "Hostile mobs are prevented",
                "from spawning in the area.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Obsidian",
                "\u00A77- Echo Shard",
                "\u00A77- Bone Meal"
            ), 9);

        reg("cleansing_ritual", "Rite of Purification",
            "Ritual", List.of(
                "\u00A7lRite of Purification",
                "",
                "\u00A77Cleanses all negative effects",
                "\u00A77from the caster.",
                "",
                "\u00A7lEffect:",
                "Removes all potion effects",
                "from the caster.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Ghast Tear",
                "\u00A77- Sugar",
                "\u00A77- Bone Meal"
            ), 7);

        reg("water_purification_ritual", "Purification of the Clear Spring",
            "Ritual", List.of(
                "\u00A7lPurification of the Clear Spring",
                "",
                "\u00A77Purifies water sources in",
                "\u00A77a large radius.",
                "",
                "\u00A7lEffect:",
                "Nearby water becomes",
                "purified and cleansed.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Ghast Tear",
                "\u00A77- Nether Quartz",
                "\u00A77- Bone Meal"
            ), 8);

        reg("soul_harvest_ritual", "Gathering of Lost Souls",
            "Ritual", List.of(
                "\u00A7lGathering of Lost Souls",
                "",
                "\u00A77Draws nearby experience",
                "\u00A77orbs to the caster.",
                "",
                "\u00A7lEffect:",
                "Pulls XP orbs toward the",
                "caster and grants bonus.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Nether Quartz",
                "\u00A77- Coal"
            ), 10);

        reg("binding_ritual", "Chains of the Bound Soul",
            "Ritual", List.of(
                "\u00A7lChains of the Bound Soul",
                "",
                "\u00A77Restricts a target's movement",
                "\u00A77with spectral chains.",
                "",
                "\u00A7lEffect:",
                "Target is rooted in place",
                "for a short duration.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Iron Ingot",
                "\u00A77- Echo Shard",
                "\u00A77- Coal"
            ), 9);

        reg("banishment_ritual", "Rite of Banishment",
            "Ritual", List.of(
                "\u00A7lRite of Banishment",
                "",
                "\u00A77Teleports the target away",
                "\u00A77from the caster.",
                "",
                "\u00A7lEffect:",
                "Target is teleported to",
                "a random nearby location.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Ender Pearl",
                "\u00A77- Echo Shard",
                "\u00A77- Nether Quartz"
            ), 10);

        reg("summoning_ritual", "Calling of the Bound",
            "Ritual", List.of(
                "\u00A7lCalling of the Bound",
                "",
                "\u00A77Summons a spectral ally",
                "\u00A77to fight for the caster.",
                "",
                "\u00A7lEffect:",
                "Spawns a temporary ally",
                "mob near the caster.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Ender Pearl",
                "\u00A77- Echo Shard",
                "\u00A77- Amethyst Shard"
            ), 11);

        reg("renewal_ritual", "Ritual of Renewal",
            "Ritual", List.of(
                "\u00A7lRitual of Renewal",
                "",
                "\u00A77Restores health and hunger",
                "\u00A77to the caster.",
                "",
                "\u00A7lEffect:",
                "Heals the caster and",
                "restores saturation.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Ghast Tear",
                "\u00A77- Glowstone Dust",
                "\u00A77- Sugar"
            ), 8);
    }

    // ========== BLESSING SPELLS ==========

    private void registerBlessingBooks() {
        reg("blessing_of_harvest", "Blessing of the Harvest",
            "Blessing", List.of(
                "\u00A7lBlessing of the Harvest",
                "",
                "\u00A77Blesses nearby crops with",
                "\u00A77bountiful yields.",
                "",
                "\u00A7lEffect:",
                "Growth effect on crops",
                "and bonus drops.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Sugar",
                "\u00A77- Glowstone Dust"
            ), 8);

        reg("blessing_of_fortitude", "Blessing of Fortitude",
            "Blessing", List.of(
                "\u00A7lBlessing of Fortitude",
                "",
                "\u00A77Grants the caster resilience",
                "\u00A77against harm.",
                "",
                "\u00A7lEffect:",
                "Resistance and absorption",
                "effects on the caster.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Iron Ingot",
                "\u00A77- Obsidian",
                "\u00A77- Amethyst Shard"
            ), 9);

        reg("spirit_walk_ritual", "Spirit Walk",
            "Ritual", List.of(
                "\u00A7lSpirit Walk",
                "",
                "\u00A77Allows the caster to move",
                "\u00A77through walls briefly.",
                "",
                "\u00A7lEffect:",
                "Grants Spectator-like",
                "phasing for a short time.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Ender Pearl",
                "\u00A77- Echo Shard",
                "\u00A77- Phantom Membrane"
            ), 12);

        reg("coven_eye_ritual", "The Coven's Eye",
            "Ritual", List.of(
                "\u00A7lThe Coven's Eye",
                "",
                "\u00A77Reveals nearby players",
                "\u00A77through walls.",
                "",
                "\u00A7lEffect:",
                "Highlights all players",
                "in a large radius.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Amethyst Shard",
                "\u00A77- Echo Shard",
                "\u00A77- Ender Pearl"
            ), 9);
    }

    // ========== PROTECTION SPELLS ==========

    private void registerProtectionBooks() {
        reg("protection_ritual", "Ward of Protection",
            "Protection", List.of(
                "\u00A7lWard of Protection",
                "",
                "\u00A77Creates a magical shield",
                "\u00A77around the caster.",
                "",
                "\u00A7lEffect:",
                "Grants Resistance for",
                "a moderate duration.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Obsidian",
                "\u00A77- Amethyst Shard",
                "\u00A77- Glowstone Dust"
            ), 8);

        reg("anti_scrying_protection", "Veil of Obscurity",
            "Protection", List.of(
                "\u00A7lVeil of Obscurity",
                "",
                "\u00A77Hides the caster from",
                "\u00A77divination magic.",
                "",
                "\u00A7lEffect:",
                "Blocks scrying attempts",
                "on the caster.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Amethyst Shard",
                "\u00A77- Crying Obsidian"
            ), 9);

        reg("fire_shield_ritual", "Ward of the Immolator",
            "Protection", List.of(
                "\u00A7lWard of the Immolator",
                "",
                "\u00A77Protects against fire and",
                "\u00A77lava damage.",
                "",
                "\u00A7lEffect:",
                "Fire Resistance for",
                "a moderate duration.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Blaze Powder",
                "\u00A77- Obsidian",
                "\u00A77- Glowstone Dust"
            ), 8);

        reg("projectile_shield_ritual", "Aegis of Deflection",
            "Protection", List.of(
                "\u00A7lAegis of Deflection",
                "",
                "\u00A77Deflects incoming projectiles",
                "\u00A77away from the caster.",
                "",
                "\u00A7lEffect:",
                "Projectiles are deflected",
                "or absorbed.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Iron Ingot",
                "\u00A77- Obsidian",
                "\u00A77- Flint"
            ), 9);

        reg("thorn_ward", "Ward of Thorns",
            "Protection", List.of(
                "\u00A7lWard of Thorns",
                "",
                "\u00A77Damages attackers who",
                "\u00A77strike the caster.",
                "",
                "\u00A7lEffect:",
                "Attackers take damage",
                "when they hit the caster.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Iron Ingot",
                "\u00A77- Bone Meal",
                "\u00A77- Nether Quartz"
            ), 8);

        reg("absorption_ward", "Ward of Absorption",
            "Protection", List.of(
                "\u00A7lWard of Absorption",
                "",
                "\u00A77Grants absorption hearts",
                "\u00A77to the caster.",
                "",
                "\u00A7lEffect:",
                "Extra absorption hearts",
                "for a duration.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Gold Ingot",
                "\u00A77- Sugar",
                "\u00A77- Glowstone Dust"
            ), 8);
    }

    // ========== DIVINATION SPELLS ==========

    private void registerDivinationBooks() {
        reg("scrying_ritual", "Mirror of the Soul",
            "Divination", List.of(
                "\u00A7lMirror of the Soul",
                "",
                "\u00A77Reveals information about",
                "\u00A77a targeted player.",
                "",
                "\u00A7lEffect:",
                "Shows target's location,",
                "health, and held item.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Amethyst Shard",
                "\u00A77- Ender Pearl"
            ), 9);

        reg("treasure_scrying_ritual", "Dowsing of the Hidden Way",
            "Divination", List.of(
                "\u00A7lDowsing of the Hidden Way",
                "",
                "\u00A77Reveals nearby valuable",
                "\u00A77ores and structures.",
                "",
                "\u00A7lEffect:",
                "Highlights ores and",
                "hidden structures.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Gold Ingot",
                "\u00A77- Amethyst Shard"
            ), 10);

        reg("aura_sight_ritual", "The Witch's Third Eye",
            "Divination", List.of(
                "\u00A7lThe Witch's Third Eye",
                "",
                "\u00A77Opens the mind to see",
                "\u00A77magical auras.",
                "",
                "\u00A7lEffect:",
                "Highlights nearby players",
                "and reveals their state.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Amethyst Shard",
                "\u00A77- Echo Shard",
                "\u00A77- Glowstone Dust"
            ), 9);

        reg("player_reveal_ritual", "Eyes of the Coven",
            "Divination", List.of(
                "\u00A7lEyes of the Coven",
                "",
                "\u00A77Tracks a specific player",
                "\u00A77across the world.",
                "",
                "\u00A7lEffect:",
                "Reveals target's location",
                "and movements.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Amethyst Shard",
                "\u00A77- Ender Pearl",
                "\u00A77- Echo Shard"
            ), 10);
    }

    // ========== COVEN RITUALS ==========

    private void registerCovenRitualBooks() {
        reg("shared_power_ritual", "Ritual of Shared Power",
            "Coven Ritual", List.of(
                "\u00A7lRitual of Shared Power",
                "",
                "\u00A77Shares magical energy among",
                "\u00A77coven members.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lEffect:",
                "Grants Speed and Haste",
                "to all nearby members.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Blaze Powder",
                "\u00A77- Gold Ingot",
                "\u00A77- Nether Wart"
            ), 12);

        reg("binding_circle_ritual", "Ritual of the Binding Circle",
            "Coven Ritual", List.of(
                "\u00A7lRitual of the Binding Circle",
                "",
                "\u00A77Traps enemies in a circle",
                "\u00A77of binding energy.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 20 blocks",
                "",
                "\u00A7lEffect:",
                "Roots and damages enemies",
                "in the area.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Obsidian",
                "\u00A77- Echo Shard",
                "\u00A77- Amethyst Shard",
                "\u00A77- Nether Quartz"
            ), 14);

        reg("mass_summons_ritual", "Ritual of Mass Summons",
            "Coven Ritual", List.of(
                "\u00A7lRitual of Mass Summons",
                "",
                "\u00A77Summons allies for the",
                "\u00A77entire coven.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lEffect:",
                "Spawns multiple ally mobs.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Ender Pearl",
                "\u00A77- Echo Shard",
                "\u00A77- Amethyst Shard"
            ), 13);

        reg("dark_harvest_ritual", "Ritual of the Dark Harvest",
            "Coven Ritual", List.of(
                "\u00A7lRitual of the Dark Harvest",
                "",
                "\u00A77Drains life from enemies",
                "\u00A77and shares it with the coven.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lEffect:",
                "Damages enemies and heals",
                "all coven members.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Nether Wart",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Ghast Tear",
                "\u00A77- Coal"
            ), 15);

        reg("storm_calling_ritual", "Ritual of Storm Calling",
            "Coven Ritual", List.of(
                "\u00A7lRitual of Storm Calling",
                "",
                "\u00A77Calls down lightning on",
                "\u00A77enemies of the coven.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lEffect:",
                "Lightning strikes enemies",
                "in the area.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Blaze Powder",
                "\u00A77- Gunpowder",
                "\u00A77- Ghast Tear"
            ), 14);

        reg("soul_drain_ritual", "Ritual of Soul Drain",
            "Coven Ritual", List.of(
                "\u00A7lRitual of Soul Drain",
                "",
                "\u00A77Drains experience from",
                "\u00A77enemies to the coven.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lEffect:",
                "Steals XP from enemies",
                "and distributes it.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Coal"
            ), 12);

        reg("doom_ritual", "Ritual of Doom",
            "Coven Ritual", List.of(
                "\u00A7lRitual of Doom",
                "",
                "\u00A77Places a doom curse on",
                "\u00A77enemies of the coven.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 20 blocks",
                "",
                "\u00A7lEffect:",
                "Applies Wither and Weakness",
                "to all enemies.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Nether Wart",
                "\u00A77- Coal",
                "\u00A77- Crying Obsidian"
            ), 13);

        // High-tier coven rituals
        reg("eternal_storm_ritual", "Ritual of the Eternal Storm",
            "Coven Ritual", List.of(
                "\u00A7lRitual of the Eternal Storm",
                "",
                "\u00A77Unleashes an unstoppable",
                "\u00A77storm of destruction.",
                "",
                "\u00A7lRequires: 4+ members",
                "\u00A7lCoven Radius: 30 blocks",
                "",
                "\u00A7lEffect:",
                "Continuous lightning and",
                "damage to all enemies.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Blaze Powder",
                "\u00A77- Gunpowder",
                "\u00A77- Ghast Tear",
                "\u00A77- Nether Quartz",
                "\u00A77- Echo Shard"
            ), 18);

        reg("lich_king_ritual", "Ritual of the Lich King",
            "Coven Ritual", List.of(
                "\u00A7lRitual of the Lich King",
                "",
                "\u00A77Summons an undead army",
                "\u00A77to serve the coven.",
                "",
                "\u00A7lRequires: 5+ members",
                "\u00A7lCoven Radius: 35 blocks",
                "",
                "\u00A7lEffect:",
                "Spawns powerful undead",
                "allies for the coven.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Nether Wart",
                "\u00A77- Bone Meal",
                "\u00A77- Coal",
                "\u00A77- Crying Obsidian",
                "\u00A77- Ghast Tear"
            ), 20);

        reg("blood_moon_ritual", "Ritual of the Blood Moon",
            "Coven Ritual", List.of(
                "\u00A7lRitual of the Blood Moon",
                "",
                "\u00A77Turns the sky red and",
                "\u00A77empowers the coven.",
                "",
                "\u00A7lRequires: 5+ members",
                "\u00A7lCoven Radius: 40 blocks",
                "",
                "\u00A7lEffect:",
                "Grants Strength and",
                "Regeneration to all",
                "coven members.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Nether Wart",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Ghast Tear",
                "\u00A77- Coal",
                "\u00A77- Dragon's Breath",
                "\u00A77- Crying Obsidian"
            ), 22);

        reg("eternal_binding_ritual", "Ritual of the Eternal Binding",
            "Coven Ritual", List.of(
                "\u00A7lRitual of the Eternal Binding",
                "",
                "\u00A77Permanently binds enemies",
                "\u00A77in chains of magic.",
                "",
                "\u00A7lRequires: 4+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lEffect:",
                "Long-duration root and",
                "Weakness on enemies.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Iron Ingot",
                "\u00A77- Echo Shard",
                "\u00A77- Amethyst Shard",
                "\u00A77- Ender Pearl",
                "\u00A77- Nether Quartz"
            ), 18);

        reg("world_ender_ritual", "Ritual of the World Ender",
            "Coven Ritual", List.of(
                "\u00A7lRitual of the World Ender",
                "",
                "\u00A77The ultimate coven ritual.",
                "\u00A77Devastates all in range.",
                "",
                "\u00A7lRequires: 5+ members",
                "\u00A7lCoven Radius: 50 blocks",
                "",
                "\u00A7lEffect:",
                "Massive damage and",
                "destruction to all enemies.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Dragon's Breath",
                "\u00A77- Echo Shard",
                "\u00A77- Nether Wart",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Ghast Tear",
                "\u00A77- Crying Obsidian",
                "\u00A77- Coal"
            ), 25);
    }

    // ========== COVEN SPELLS (incantation-based) ==========

    private void registerCovenSpellBooks() {
        reg("wrath_of_the_coven", "Wrath of the Coven",
            "Coven Spell", List.of(
                "\u00A7lWrath of the Coven",
                "",
                "\u00A77A devastating curse cast",
                "\u00A77by three or more witches.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77ira congregatio spiritus",
                "2. \u00A77furor antiquus invocare",
                "3. \u00A77potentia devastare hostes",
                "",
                "\u00A77Speak lines in order within",
                "\u00A77the timeout period."
            ), 14);

        reg("unity_shield", "Unity Shield",
            "Coven Spell", List.of(
                "\u00A7lUnity Shield",
                "",
                "\u00A77A shared protective barrier.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 10 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77unitas scutum protectionis",
                "2. \u00A77congregatio defensare animas",
                "",
                "\u00A77Speak lines in order.",
                "\u00A77Grants Resistance to all."
            ), 10);

        reg("shared_sight", "Shared Sight",
            "Coven Spell", List.of(
                "\u00A7lShared Sight",
                "",
                "\u00A77Shares vision among the",
                "\u00A77coven members.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 10 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77oculus communis videre",
                "2. \u00A77animae nexus revelare",
                "",
                "\u00A77Reveals all players to the",
                "\u00A77coven."
            ), 10);

        reg("storm_calling", "Storm Calling",
            "Coven Spell", List.of(
                "\u00A7lStorm Calling",
                "",
                "\u00A77Calls a devastating storm.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 20 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77tempestas vocare fulmen",
                "2. \u00A77iratus nubium potestas",
                "3. \u00A77caelum irritare hostes",
                "",
                "\u00A77Strikes enemies with",
                "\u00A77lightning."
            ), 14);

        reg("soul_drain_spell", "Soul Drain",
            "Coven Spell", List.of(
                "\u00A7lSoul Drain",
                "",
                "\u00A77Drains life from enemies.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77anima exhaurire vitam",
                "2. \u00A77vis vitalis subtrahere",
                "",
                "\u00A77Damages enemies and heals",
                "\u00A77coven members."
            ), 12);

        reg("doom_spell", "Ritual of Doom",
            "Coven Spell", List.of(
                "\u00A7lRitual of Doom",
                "",
                "\u00A77Places a terrible doom",
                "\u00A77upon all enemies.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77exitium invocare magnus",
                "2. \u00A77perditio anima consumere",
                "3. \u00A77doom aeternum hostes",
                "",
                "\u00A77Applies Wither to all",
                "\u00A77enemies in range."
            ), 15);

        reg("apocalypse", "Apocalypse",
            "Coven Spell", List.of(
                "\u00A7lApocalypse",
                "",
                "\u00A77The ultimate coven curse.",
                "",
                "\u00A7lRequires: 5+ members",
                "\u00A7lCoven Radius: 35 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77exitium mundi invocare",
                "2. \u00A77apocalypsis venire tenebris",
                "3. \u00A77perpetua damnatio animarum",
                "4. \u00A77cineres mundi consumere",
                "5. \u00A77apocalypse eternum dominare",
                "",
                "\u00A77Unleashes total destruction."
            ), 25);

        reg("armageddon", "Armageddon",
            "Coven Spell", List.of(
                "\u00A7lArmageddon",
                "",
                "\u00A77Rains fire upon the land.",
                "",
                "\u00A7lRequires: 4+ members",
                "\u00A7lCoven Radius: 30 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77ignis aeternus descendat",
                "2. \u00A77fulmen iratus caelum",
                "3. \u00A77armageddon hostes consumere",
                "4. \u00A77cineres hostium flammare",
                "",
                "\u00A77Engulfing fire damage."
            ), 22);

        reg("mass_transmutation", "Mass Transmutation",
            "Coven Spell", List.of(
                "\u00A7lMass Transmutation",
                "",
                "\u00A77Weakens all enemies.",
                "",
                "\u00A7lRequires: 4+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77transmutatio magna animarum",
                "2. \u00A77vis auferre potentiam",
                "3. \u00A77debilitas in perpetuum",
                "4. \u00A77transmutare hostes in nihilum",
                "",
                "\u00A77Applies Weakness and Mining",
                "\u00A77Fatigue to all enemies."
            ), 20);

        reg("soul_harvest_spell", "Soul Harvest",
            "Coven Spell", List.of(
                "\u00A7lSoul Harvest",
                "",
                "\u00A77Harvests souls of enemies.",
                "",
                "\u00A7lRequires: 4+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77anima hostium colligere",
                "2. \u00A77vis vitalis subtrahere",
                "3. \u00A77harvest animarum potestas",
                "4. \u00A77vita furari in perpetuum",
                "",
                "\u00A77Kills low-health enemies",
                "\u00A77and heals the coven."
            ), 22);

        reg("eternal_damnation", "Eternal Damnation",
            "Coven Spell", List.of(
                "\u00A7lEternal Damnation",
                "",
                "\u00A77The darkest coven spell.",
                "",
                "\u00A7lRequires: 5+ members",
                "\u00A7lCoven Radius: 40 blocks",
                "",
                "\u00A7lIncantation:",
                "\u00A77Each member says one line:",
                "",
                "1. \u00A77damnatio aeterna invocare",
                "2. \u00A77perpetua tenebris consumere",
                "3. \u00A77anima in inferno ligare",
                "4. \u00A77maledictio sempiterna descendat",
                "5. \u00A77eternal damnation hostes",
                "",
                "\u00A77Eternal Withers and Nausea."
            ), 28);
    }

    // ========== NEW SINGULAR CURSES (10) ==========

    private void registerNewCurseBooks() {
        reg("levitation_curse", "Curse of Levitation",
            "Curse", List.of(
                "\u00A7lCurse of Levitation",
                "",
                "\u00A77Lifts the victim helplessly",
                "\u00A77into the air.",
                "",
                "\u00A7lEffect:",
                "Levitation and Nausea for",
                "a short, dizzying flight.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Feather",
                "\u00A77- Phantom Membrane",
                "\u00A77- Echo Shard",
                "",
                "\u00A7lIncantation:",
                "\u00A77levitas corpus ascendere"
            ), 9);
        reg("fragility_curse", "Curse of Fragility",
            "Curse", List.of(
                "\u00A7lCurse of Fragility",
                "",
                "\u00A77Makes bones brittle as",
                "\u00A77thin glass.",
                "",
                "\u00A7lEffect:",
                "Weakness, Mining Fatigue,",
                "and Slowness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Iron Ingot",
                "\u00A77- Nether Quartz",
                "",
                "\u00A7lIncantation:",
                "\u00A77ossa fragilis rumpere"
            ), 8);
        reg("darkness_curse", "Curse of Darkness",
            "Curse", List.of(
                "\u00A7lCurse of Darkness",
                "",
                "\u00A77Engulfs the victim in",
                "\u00A77impenetrable dark.",
                "",
                "\u00A7lEffect:",
                "Darkness, Blindness, and",
                "Weakness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Crying Obsidian",
                "\u00A77- Coal",
                "",
                "\u00A7lIncantation:",
                "\u00A77tenebris aeterna obscurare"
            ), 9);
        reg("hollow_vein_curse", "Curse of the Hollow Vein",
            "Curse", List.of(
                "\u00A7lCurse of the Hollow Vein",
                "",
                "\u00A77Drains blood and vitality.",
                "",
                "\u00A7lEffect:",
                "Poison, Hunger, Weakness,",
                "and Slowness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Ghast Tear",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Prismarine Shard",
                "",
                "\u00A7lIncantation:",
                "\u00A77sanguis vacuus exhaurire"
            ), 10);
        reg("encumbrance_curse", "Curse of Encumbrance",
            "Curse", List.of(
                "\u00A7lCurse of Encumbrance",
                "",
                "\u00A77Crushes the victim under",
                "\u00A77phantom weight.",
                "",
                "\u00A7lEffect:",
                "Slowness III and Mining",
                "Fatigue II, Weakness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Obsidian",
                "\u00A77- Iron Ingot",
                "\u00A77- Gunpowder",
                "",
                "\u00A7lIncantation:",
                "\u00A77pondus gravis opprimere"
            ), 9);
        reg("vertigo_curse", "Curse of Vertigo",
            "Curse", List.of(
                "\u00A7lCurse of Vertigo",
                "",
                "\u00A77Spins the world into",
                "\u00A77violent chaos.",
                "",
                "\u00A7lEffect:",
                "Nausea, Levitation,",
                "Blindness, Slowness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Feather",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Glowstone Dust",
                "",
                "\u00A7lIncantation:",
                "\u00A77vertigo gyrus confundere"
            ), 8);
        reg("frost_curse", "Curse of Frost",
            "Curse", List.of(
                "\u00A7lCurse of Frost",
                "",
                "\u00A77Biting cold creeps over",
                "\u00A77the victim's limbs.",
                "",
                "\u00A7lEffect:",
                "Slowness, Mining Fatigue,",
                "Weakness, and freezing.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Prismarine Shard",
                "\u00A77- Prismarine Crystals",
                "\u00A77- Coal",
                "",
                "\u00A7lIncantation:",
                "\u00A77gelu frigus mordere"
            ), 8);
        reg("banshee_curse", "Curse of the Banshee",
            "Curse", List.of(
                "\u00A7lCurse of the Banshee",
                "",
                "\u00A77A ghostly wail shatters",
                "\u00A77senses.",
                "",
                "\u00A7lEffect:",
                "Nausea, Darkness,",
                "Blindness, Weakness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Ghast Tear",
                "\u00A77- Fermented Spider Eye",
                "",
                "\u00A7lIncantation:",
                "\u00A77ululatus spiritus clamare"
            ), 10);
        reg("decay_curse", "Curse of Decay",
            "Curse", List.of(
                "\u00A7lCurse of Decay",
                "",
                "\u00A77Flesh rots from within.",
                "",
                "\u00A7lEffect:",
                "Wither, Poison, and",
                "Hunger.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Spider Eye",
                "\u00A77- Bone Meal",
                "\u00A77- Prismarine Crystals",
                "",
                "\u00A7lIncantation:",
                "\u00A77putredo tabes consumere"
            ), 9);
        reg("eclipse_curse", "Curse of the Eclipse",
            "Curse", List.of(
                "\u00A7lCurse of the Eclipse",
                "",
                "\u00A77Blots the sun from the sky.",
                "",
                "\u00A7lEffect:",
                "Blindness, Darkness,",
                "Wither, Slowness.",
                "",
                "\u00A7lRitual Ingredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Nether Quartz",
                "\u00A77- Crying Obsidian",
                "",
                "\u00A7lIncantation:",
                "\u00A77sol obscura tenebris"
            ), 10);
    }

    // ========== NEW COVEN CURSES (10) ==========

    private void registerCovenCurseBooks() {
        reg("writhing_roots_curse", "Curse of Writhing Roots",
            "Coven Ritual", List.of(
                "\u00A7lCurse of Writhing Roots",
                "",
                "\u00A77Roots writhe and bind",
                "\u00A77all enemies.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 20 blocks",
                "",
                "\u00A7lEffect:",
                "Slowness, Weakness, and",
                "Mining Fatigue on all",
                "enemies.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Bone Meal",
                "\u00A77- Echo Shard",
                "\u00A77- Obsidian",
                "\u00A77- Amethyst Shard",
                "",
                "\u00A7lChant:",
                "\u00A77radices torquere ligare",
                "\u00A77terra constringere hostes",
                "\u00A77vinea strangulare inimicos"
            ), 14);
        reg("sinking_mire_curse", "Curse of the Sinking Mire",
            "Coven Ritual", List.of(
                "\u00A7lCurse of the Sinking Mire",
                "",
                "\u00A77The mire drags victims",
                "\u00A77down into suffocation.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lEffect:",
                "Slowness, Mining Fatigue,",
                "and Hunger.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Prismarine Shard",
                "\u00A77- Prismarine Crystals",
                "\u00A77- Spider Eye",
                "\u00A77- Coal",
                "",
                "\u00A7lChant:",
                "\u00A77palus vorare profundus",
                "\u00A77lutum trahere deorsum"
            ), 12);
        reg("withered_fields_curse", "Curse of Withered Fields",
            "Coven Ritual", List.of(
                "\u00A7lCurse of Withered Fields",
                "",
                "\u00A77Fields wither and die",
                "\u00A77around all enemies.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lEffect:",
                "Wither, Blindness, and",
                "Nausea.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Nether Wart",
                "\u00A77- Bone Meal",
                "\u00A77- Ghast Tear",
                "\u00A77- Echo Shard",
                "",
                "\u00A7lChant:",
                "\u00A77ager marcescere arefacere",
                "\u00A77messis perdere infelix",
                "\u00A77terra sterilis maledicta"
            ), 13);
        reg("howling_void_curse", "Curse of the Howling Void",
            "Coven Ritual", List.of(
                "\u00A7lCurse of the Howling Void",
                "",
                "\u00A77The void howls and tears",
                "\u00A77at souls.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 25 blocks",
                "",
                "\u00A7lEffect:",
                "Levitation, Blindness,",
                "Nausea, Weakness.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Phantom Membrane",
                "\u00A77- Echo Shard",
                "\u00A77- Ghast Tear",
                "\u00A77- Nether Quartz",
                "",
                "\u00A7lChant:",
                "\u00A77inanis ululare vorax",
                "\u00A77vacuum clamare tenebris",
                "\u00A77abyssus devorare animas"
            ), 14);
        reg("brittle_earth_curse", "Curse of Brittle Earth",
            "Coven Ritual", List.of(
                "\u00A7lCurse of Brittle Earth",
                "",
                "\u00A77Earth crumbles beneath",
                "\u00A77enemy feet.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lEffect:",
                "Mining Fatigue, Slowness,",
                "and Weakness.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Obsidian",
                "\u00A77- Crying Obsidian",
                "\u00A77- Iron Ingot",
                "\u00A77- Coal",
                "",
                "\u00A7lChant:",
                "\u00A77terra fragilis rumpere",
                "\u00A77solum infirmus fatiscere"
            ), 12);
        reg("pallid_plague_curse", "Curse of the Pallid Plague",
            "Coven Ritual", List.of(
                "\u00A7lCurse of the Pallid Plague",
                "",
                "\u00A77Pallid plague festers and",
                "\u00A77spreads.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 20 blocks",
                "",
                "\u00A7lEffect:",
                "Poison, Hunger, Wither,",
                "and Weakness.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Nether Wart",
                "\u00A77- Spider Eye",
                "\u00A77- Ghast Tear",
                "\u00A77- Fermented Spider Eye",
                "",
                "\u00A7lChant:",
                "\u00A77pallidus pestis serpere",
                "\u00A77morbus luridus consumere",
                "\u00A77contagium expandere late"
            ), 14);
        reg("starless_night_curse", "Curse of the Starless Night",
            "Coven Ritual", List.of(
                "\u00A7lCurse of the Starless Night",
                "",
                "\u00A77Stars vanish from the sky.",
                "",
                "\u00A7lRequires: 3+ members",
                "\u00A7lCoven Radius: 20 blocks",
                "",
                "\u00A7lEffect:",
                "Darkness, Blindness, and",
                "Slowness.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Crying Obsidian",
                "\u00A77- Amethyst Shard",
                "\u00A77- Nether Quartz",
                "",
                "\u00A7lChant:",
                "\u00A77nox sine astris obscura",
                "\u00A77caelum vacuus tenebris",
                "\u00A77sidera extinguere perpetuo"
            ), 13);
        reg("crushing_weight_curse", "Curse of Crushing Weight",
            "Coven Ritual", List.of(
                "\u00A7lCurse of Crushing Weight",
                "",
                "\u00A77Immense weight crushes",
                "\u00A77all enemies.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lEffect:",
                "Slowness III, Mining",
                "Fatigue III, Weakness.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Iron Ingot",
                "\u00A77- Gold Ingot",
                "\u00A77- Obsidian",
                "\u00A77- Gunpowder",
                "",
                "\u00A7lChant:",
                "\u00A77pondus immensum opprimere",
                "\u00A77gravitas conculcare hostes"
            ), 12);
        reg("fetid_bog_curse", "Curse of the Fetid Bog",
            "Coven Ritual", List.of(
                "\u00A7lCurse of the Fetid Bog",
                "",
                "\u00A77Fetid bog seeps into",
                "\u00A77enemy lungs.",
                "",
                "\u00A7lRequires: 2+ members",
                "\u00A7lCoven Radius: 15 blocks",
                "",
                "\u00A7lEffect:",
                "Poison, Slowness, Hunger,",
                "and Mining Fatigue.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Spider Eye",
                "\u00A77- Fermented Spider Eye",
                "\u00A77- Ghast Tear",
                "\u00A77- Prismarine Shard",
                "",
                "\u00A7lChant:",
                "\u00A77palus foetida venenare",
                "\u00A77uligo tabida inficere"
            ), 12);
        reg("eternal_night_curse", "Curse of Eternal Night",
            "Coven Ritual", List.of(
                "\u00A7lCurse of Eternal Night",
                "",
                "\u00A77Eternal night descends,",
                "\u00A77never-ending.",
                "",
                "\u00A7lRequires: 4+ members",
                "\u00A7lCoven Radius: 30 blocks",
                "",
                "\u00A7lEffect:",
                "Darkness, Wither, Nausea,",
                "Blindness, Slowness.",
                "",
                "\u00A7lIngredients:",
                "\u00A77- Echo Shard",
                "\u00A77- Crying Obsidian",
                "\u00A77- Dragon's Breath",
                "\u00A77- Ghast Tear",
                "",
                "\u00A7lChant:",
                "\u00A77nox aeterna descendat",
                "\u00A77tenebrae perpetua regnare",
                "\u00A77sol extinguere in aeternum",
                "\u00A77caligo infinita dominare"
            ), 18);
    }
}
