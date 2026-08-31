package com.witchcraft.book;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Manages lore books - non-spell books that give world lore.
 * Uses same pagination as GuideBookBuilder (15 chars/line, 14 lines/page, 50 pages).
 */
public class LoreBookManager {

    private final Map<String, SpellBookData> loreBooks = new LinkedHashMap<>();

    public LoreBookManager() {
        registerAllLore();
    }

    public SpellBookData getLoreBook(String id) {
        return loreBooks.get(id);
    }

    public Collection<SpellBookData> getAllLoreBooks() {
        return loreBooks.values();
    }

    public int getLoreCount() {
        return loreBooks.size();
    }

    public String getRandomLoreId() {
        List<String> ids = new ArrayList<>(loreBooks.keySet());
        return ids.get(new Random().nextInt(ids.size()));
    }

    public ItemStack createLoreBookItem(String loreId) {
        SpellBookData data = loreBooks.get(loreId);
        if (data == null) return null;
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return null;
        meta.setTitle(data.getTitle());
        meta.setAuthor("The Old Ones");
        meta.setPages(paginate(data.getPages()));
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        meta.getPersistentDataContainer().set(
                new NamespacedKey("witchcraft", SpellBookManager.LORE_BOOK_KEY),
                PersistentDataType.STRING, data.getSpellId());
        book.setItemMeta(meta);
        return book;
    }

    public static boolean isLoreBook(ItemStack item) {
        if (item == null || item.getType() != Material.WRITTEN_BOOK) return false;
        var meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(
                new NamespacedKey("witchcraft", SpellBookManager.LORE_BOOK_KEY), PersistentDataType.STRING);
    }

    private void reg(String id, String title, List<String> pages) {
        // Use same paginate raw lines into pages
        loreBooks.put(id, new SpellBookData(id, title, "Lore", pages, 6));
    }

    private void registerAllLore() {
        reg("lore_first_coven", "The First Coven", List.of(
                "\u00A75\u00A7lThe First Coven",
                "",
                "\u00A77Long before kingdoms",
                "\u00A77rose, three sisters",
                "\u00A77gathered beneath a",
                "\u00A77blood moon.",
                "",
                "\u00A77They spoke no common",
                "\u00A77tongue, but the wind",
                "\u00A77carried their words:",
                "\u00A77\"We bind ourselves to",
                "\u00A77the unseen.\"",
                "\u00A70 ",
                "\u00A77Their cauldron was not",
                "\u00A77iron, but hollowed",
                "\u00A77obsidian, filled with",
                "\u00A77water from the first",
                "\u00A77spring.",
                "",
                "\u00A77Into it they cast",
                "\u00A77nether wart, bone",
                "\u00A77meal, and a single",
                "\u00A77echo shard humming",
                "\u00A77with memory.",
                "\u00A70 ",
                "\u00A77When the brew boiled,",
                "\u00A77the sky cracked. The",
                "\u00A77sisters saw not the",
                "\u00A77future, but every",
                "\u00A77witch who would ever",
                "\u00A77stir a cauldron after",
                "\u00A77them.",
                "",
                "\u00A77They became the Old",
                "\u00A77Ones. Their pact",
                "\u00A77remains in every",
                "\u00A77water cauldron you",
                "\u00A77fill.",
                "\u00A70 ",
                "\u00A77To join a coven is to",
                "\u00A77remember that first",
                "\u00A77night. Two may lead as",
                "\u00A77Priest or Priestess,",
                "\u00A77Council guides,",
                "\u00A77Initiates learn. The",
                "\u00A77circle is never closed.",
                "",
                "\u00A77Seek the others, and",
                "\u00A77the cauldron will",
                "\u00A77answer."
        ));

        reg("lore_witching_hour", "The Witching Hour", List.of(
                "\u00A75\u00A7lThe Witching Hour",
                "",
                "\u00A77Not all nights are",
                "\u00A77equal. The moon is a",
                "\u00A77clock for those who",
                "\u00A77know how to read it.",
                "",
                "\u00A77New moon: curses of",
                "\u00A77misfortune bite deeper.",
                "\u00A77Full moon: phantoms",
                "\u00A77remember your name.",
                "\u00A70 ",
                "\u00A77Midnight to 3 a.m. is",
                "\u00A77when the veil thins.",
                "\u00A77Rituals cast then burn",
                "\u00A77brighter, backfire",
                "\u00A77harsher.",
                "",
                "\u00A77Clear skies favor",
                "\u00A77blessings. Storms favor",
                "\u00A77wrath. The cauldron",
                "\u00A77knows the weather",
                "\u00A77before you do.",
                "\u00A70 ",
                "\u00A77If a ritual demands",
                "\u00A77moon phase 4 or clear",
                "\u00A77weather and you ignore",
                "\u00A77it, the magic will",
                "\u00A77ignore you - or worse,",
                "\u00A77turn inward for three",
                "\u00A77days of silence.",
                "",
                "\u00A77The Old Ones did not",
                "\u00A77invent time. They",
                "\u00A77listened to it.",
                "\u00A70 ",
                "\u00A77Watch the sky. Let the",
                "\u00A77cauldron wait until",
                "\u00A77the moment is right.",
                "\u00A77Patience is a kind of",
                "\u00A77power."
        ));

        reg("lore_black_cauldron", "The Black Cauldron", List.of(
                "\u00A75\u00A7lThe Black Cauldron",
                "",
                "\u00A77All magic begins with",
                "\u00A77water. Not holy water,",
                "\u00A77not spring water - any",
                "\u00A77water held still in",
                "\u00A77iron.",
                "",
                "\u00A77Fill the cauldron. The",
                "\u00A77surface must be calm.",
                "\u00A77Throw ingredients one",
                "\u00A77by one. Order does not",
                "\u00A77matter, intent does.",
                "\u00A70 ",
                "\u00A77When the last herb",
                "\u00A77sinks, step back three",
                "\u00A77blocks. Stay close, but",
                "\u00A77do not crowd the",
                "\u00A77brewing air.",
                "",
                "\u00A77You will see it: a",
                "\u00A77slow swirl of violet",
                "\u00A77and smoke, a faint",
                "\u00A77chime of amethyst. That",
                "\u00A77is the cauldron",
                "\u00A77breathing.",
                "\u00A70 ",
                "\u00A77If you walk too far, the",
                "\u00A77breath stops. If you",
                "\u00A77lack levels, it never",
                "\u00A77starts. If you lack",
                "\u00A77knowledge, the water",
                "\u00A77remains water.",
                "",
                "\u00A77Knowledge is the true",
                "\u00A77ingredient. Books teach",
                "\u00A77what herbs cannot.",
                "\u00A70 ",
                "\u00A77Target papers - paper",
                "\u00A77renamed at an anvil",
                "\u00A77with a player's name,",
                "\u00A77costing 30 levels - tie",
                "\u00A77a curse to a soul. Add",
                "\u00A77it last, and the",
                "\u00A77cauldron knows whom to",
                "\u00A77seek."
        ));

        reg("lore_old_ones", "Whispers of the Old Ones", List.of(
                "\u00A75\u00A7lWhispers of the",
                "\u00A75\u00A7lOld Ones",
                "",
                "\u00A77They left no statues,",
                "\u00A77only books. Each book",
                "\u00A77smells faintly of smoke",
                "\u00A77and rosemary.",
                "",
                "\u00A77Open one. The words",
                "\u00A77shift: ritual verses",
                "\u00A77you have not yet",
                "\u00A77earned will blur. Verses",
                "\u00A77you have earned will",
                "\u00A77glow.",
                "\u00A70 ",
                "\u00A77A book may teach a",
                "\u00A77single curse, or a",
                "\u00A77coven's shared chant.",
                "\u00A77For coven magic, only",
                "\u00A77the caster must have",
                "\u00A77read. The others lend",
                "\u00A77voice, not memory.",
                "",
                "\u00A77But the words must be",
                "\u00A77exact. One wrong",
                "\u00A77syllable and the coven",
                "\u00A77stands in silence.",
                "\u00A70 ",
                "\u00A77The Old Ones write in",
                "\u00A77ink that never fades",
                "\u00A77because they write in",
                "\u00A77consequence. To read is",
                "\u00A77to accept the price.",
                "",
                "\u00A77Keep their books dry.",
                "\u00A77Keep their names",
                "\u00A77spoken correctly.",
                "\u00A70 ",
                "\u00A77When you know a spell,",
                "\u00A77you will feel it behind",
                "\u00A77your teeth, waiting for",
                "\u00A77the right breath."
        ));

        reg("lore_price_of_power", "The Price of Power", List.of(
                "\u00A75\u00A7lThe Price of Power",
                "",
                "\u00A77Magic fails more often",
                "\u00A77than it succeeds for",
                "\u00A77the impatient.",
                "",
                "\u00A77Every spell rolls its",
                "\u00A77own fate: success,",
                "\u00A77simple failure, or",
                "\u00A77backfire. Backfire",
                "\u00A77wounds the caster with",
                "\u00A77its own intent.",
                "\u00A70 ",
                "\u00A77A miner cursed to",
                "\u00A77fatigue may find the",
                "\u00A77caster's own arms turn",
                "\u00A77to lead. A plague sent",
                "\u00A77outward returns as",
                "\u00A77fever.",
                "",
                "\u00A77There is a heavier price",
                "\u00A77for covens who overreach.",
                "\u00A77Call for four when only",
                "\u00A77two stand near, and the",
                "\u00A77spirits take your voice",
                "\u00A77for three full days.",
                "\u00A70 ",
                "\u00A77During Arcane Exhaustion",
                "\u00A77you cannot cast, cannot",
                "\u00A77chant, cannot learn. The",
                "\u00A77cauldron will not answer",
                "\u00A77you. Even protected",
                "\u00A77victims reflect your",
                "\u00A77malice back as",
                "\u00A77exhaustion.",
                "",
                "\u00A77The Old Ones called this",
                "\u00A77balance, not punishment.",
                "\u00A70 ",
                "\u00A77Levels, cooldowns,",
                "\u00A77distance - these are not",
                "\u00A77restrictions. They are",
                "\u00A77ritual.",
                "\u00A77Respect them, and the",
                "\u00A77backlash stays distant."
        ));

        reg("lore_forbidden_verses", "The Forbidden Verses", List.of(
                "\u00A75\u00A7lThe Forbidden Verses",
                "",
                "\u00A77Incantations are not",
                "\u00A77shouts. They are precise,",
                "\u00A77low sentences spoken",
                "\u00A77into chat.",
                "",
                "\u00A77Example: \"tenebris",
                "\u00A77ferrum obstaculum\" must",
                "\u00A77be typed exactly. Capitals",
                "\u00A77and punctuation do not",
                "\u00A77matter, but order and",
                "\u00A77spelling do.",
                "\u00A70 ",
                "\u00A77Hold a target paper to",
                "\u00A77aim. Without it, curses",
                "\u00A77may strike the nearest",
                "\u00A77foe or fizzle. With it,",
                "\u00A77the paper crumbles as",
                "\u00A77the words leave you.",
                "",
                "\u00A77Coven chants are longer.",
                "\u00A77One line per witch, in",
                "\u00A77order, within seconds.",
                "\u00A77If the first witch says",
                "\u00A77\"ira congregatio\", the",
                "\u00A77second must answer with",
                "\u00A77the next line.",
                "\u00A70 ",
                "\u00A77The verses were never",
                "\u00A77meant to be shouted",
                "\u00A77across a battlefield.",
                "\u00A77Whisper them. The magic",
                "\u00A77listens closer when you",
                "\u00A77do.",
                "",
                "\u00A77To see what you know,",
                "\u00A77use /grimoire. To learn,",
                "\u00A77read. To cast, speak",
                "\u00A77true."
        ));

        reg("lore_binding_circle", "The Binding Circle", List.of(
                "\u00A75\u00A7lThe Binding Circle",
                "",
                "\u00A77A circle is not drawn",
                "\u00A77in chalk. It is drawn",
                "\u00A77in bodies.",
                "",
                "\u00A77Two witches can share",
                "\u00A77power. Three can bind.",
                "\u00A77Five can unmake a sky.",
                "\u00A77The cauldron measures",
                "\u00A77how many stand within its",
                "\u00A77radius, not how many",
                "\u00A77promised to come.",
                "\u00A70 ",
                "\u00A77Ranks matter: Priest",
                "\u00A77and Priestess lead - up",
                "\u00A77to two, any mix. Council",
                "\u00A77holds wisdom. Initiates",
                "\u00A77hold future.",
                "",
                "\u00A77Only leaders may invite,",
                "\u00A77claim land, or set rank.",
                "\u00A77But any initiate who",
                "\u00A77reads the right book can",
                "\u00A77cast for the whole coven.",
                "\u00A70 ",
                "\u00A77Claimed chunks (up to",
                "\u00A77four) remember your",
                "\u00A77coven. Inside them, only",
                "\u00A77members may break, place,",
                "\u00A77or open containers. The",
                "\u00A77land itself refuses",
                "\u00A77strangers.",
                "",
                "\u00A77Explosions there break",
                "\u00A77nothing. The circle holds",
                "\u00A77even against creepers.",
                "\u00A70 ",
                "\u00A77To leave is to be",
                "\u00A77unbound. To disband is",
                "\u00A77to scatter. Choose with",
                "\u00A77care - the cauldron",
                "\u00A77remembers who stood",
                "\u00A77together."
        ));

        reg("lore_whispering_woods", "The Whispering Woods", List.of(
                "\u00A75\u00A7lThe Whispering Woods",
                "",
                "\u00A77Beyond villages, where",
                "\u00A77oak roots tangle over",
                "\u00A77stone, you may find a",
                "\u00A77chest half-swallowed by",
                "\u00A77moss. Inside: a book",
                "\u00A77warm to the touch.",
                "",
                "\u00A77Not every chest holds",
                "\u00A77magic. Common curses -",
                "\u00A77those of slow feet or",
                "\u00A77hollow veins - appear",
                "\u00A77more often. World-enders",
                "\u00A77hide at 0.3% for a",
                "\u00A77reason.",
                "\u00A70 ",
                "\u00A77Villagers who chose the",
                "\u00A77librarian's robe sometimes",
                "\u00A77trade what they should",
                "\u00A77not. Bring emeralds and",
                "\u00A77paper. They will ask 7 to",
                "\u00A7728, depending on the",
                "\u00A77weight of the words.",
                "",
                "\u00A77You may also find",
                "\u00A77nothing but lore: stories",
                "\u00A77of the Old Ones, warnings",
                "\u00A77about frost and eclipse.",
                "\u00A77Read them. Lore does not",
                "\u00A77teach a spell, but it",
                "\u00A77teaches when not to cast.",
                "\u00A70 ",
                "\u00A77Magic in Minecraft is not",
                "\u00A77separate from the world.",
                "\u00A77Crops, moon, weather,",
                "\u00A77trade, exploration - all",
                "\u00A77are threads. Pull gently.",
                "",
                "\u00A77The woods are still",
                "\u00A77whispering. Listen with",
                "\u00A77water in your cauldron",
                "\u00A77and words ready on your",
                "\u00A77tongue."
        ));
    }

    private List<String> paginate(List<String> rawPages) {
        List<String> rawLines = new ArrayList<>();
        for (String p : rawPages) {
            if (p.contains("\n")) rawLines.addAll(Arrays.asList(p.split("\n", -1)));
            else rawLines.add(p);
        }
        List<String> pages = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int lineCount = 0;
        for (String line : rawLines) {
            if (line.equals("\u00A70 ") || line.equals("§0 ")) {
                if (cur.length() > 0) { pages.add(cur.toString()); cur = new StringBuilder(); lineCount = 0; }
                continue;
            }
            if (lineCount >= 14) { pages.add(cur.toString()); cur = new StringBuilder(); lineCount = 0; }
            List<String> wrapped = wrapLine(line, 15);
            for (String w : wrapped) {
                if (lineCount >= 14) { pages.add(cur.toString()); cur = new StringBuilder(); lineCount = 0; }
                if (lineCount > 0) cur.append("\n");
                cur.append(w);
                lineCount++;
            }
        }
        if (cur.length() > 0) pages.add(cur.toString());
        if (pages.size() > 50) pages = pages.subList(0, 50);
        if (pages.isEmpty()) pages.add("");
        return pages;
    }

    private List<String> wrapLine(String line, int maxLen) {
        List<String> wrapped = new ArrayList<>();
        if (line == null || line.isEmpty()) { wrapped.add(line == null ? "" : line); return wrapped; }
        String stripped = org.bukkit.ChatColor.stripColor(line);
        if (stripped.length() <= maxLen) { wrapped.add(line); return wrapped; }
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
}
