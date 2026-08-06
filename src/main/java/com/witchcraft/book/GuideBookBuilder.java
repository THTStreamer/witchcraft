package com.witchcraft.book;

import com.witchcraft.Witchcraft;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating properly formatted Minecraft Written Books.
 */
public class GuideBookBuilder {

    private final Witchcraft plugin;

    /**
     * Maximum characters per line in a Written Book.
     */
    private static final int MAX_LINE_LENGTH = 15;

    /**
     * Maximum visible lines per page.
     */
    private static final int MAX_LINES_PER_PAGE = 14;

    /**
     * Maximum pages in a Written Book.
     */
    private static final int MAX_PAGES = 50;

    public GuideBookBuilder(Witchcraft plugin) {
        this.plugin = plugin;
    }

    /**
     * Builds a guide book item.
     *
     * @return the ItemStack containing the Written Book
     */
    public org.bukkit.inventory.ItemStack buildBook() {
        org.bukkit.inventory.ItemStack book = new org.bukkit.inventory.ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        if (meta == null) return book;

        meta.setTitle("Witchcraft Guide");
        meta.setAuthor("The Old Ones");

        List<String> pages = formatPages(getContent());
        meta.setPages(pages);

        book.setItemMeta(meta);
        return book;
    }

    /**
     * Gets the raw content to be formatted into pages.
     *
     * @return list of raw lines
     */
    private List<String> getContent() {
        List<String> lines = new ArrayList<>();

        // Title page
        lines.add("§5§lWitchcraft Guide");
        lines.add("");
        lines.add("§7An ancient grimoire of");
        lines.add("§7folk magic and rituals.");
        lines.add("");
        lines.add("§7Written by the Old Ones.");
        lines.add("§7Handle with care.");
        lines.add("§0 "); // Page break indicator

        // Overview
        lines.add("§5§lWhat is Witchcraft?");
        lines.add("");
        lines.add("§7Witchcraft is an ancient");
        lines.add("§7magical tradition based on");
        lines.add("§7rituals, incantations, and");
        lines.add("§7the power of nature.");
        lines.add("");
        lines.add("§7Magic requires preparation.");
        lines.add("§7Nothing is instant.");
        lines.add("§7Every ritual has weight.");
        lines.add("§0 ");

        // Ritual Magic
        lines.add("§5§lRitual Magic");
        lines.add("");
        lines.add("§7Rituals are performed at");
        lines.add("§7cauldrons using ingredients.");
        lines.add("");
        lines.add("§7Steps to cast a ritual:");
        lines.add("§71. Fill cauldron with water");
        lines.add("§72. Add required ingredients");
        lines.add("§73. Stand near the cauldron");
        lines.add("§74. Begin the ritual");
        lines.add("§75. Wait for completion");
        lines.add("§0 ");

        // Ingredients
        lines.add("§5§lIngredients");
        lines.add("");
        lines.add("§7Different rituals require");
        lines.add("§7different ingredients.");
        lines.add("");
        lines.add("§7Common ingredients:");
        lines.add("§7- Nether Wart");
        lines.add("§7- Redstone Dust");
        lines.add("§7- Glowstone Dust");
        lines.add("§7- Bone Meal");
        lines.add("§7- Coal");
        lines.add("§0 ");

        // Incantations
        lines.add("§5§lIncantations");
        lines.add("");
        lines.add("§7Some spells are cast by");
        lines.add("§7speaking words of power.");
        lines.add("");
        lines.add("§7Type the incantation in");
        lines.add("§7chat to cast the spell.");
        lines.add("");
        lines.add("§7You must learn each");
        lines.add("§7incantation before use.");
        lines.add("§0 ");

        // Learning
        lines.add("§5§lLearning Spells");
        lines.add("");
        lines.add("§7Incantations are learned");
        lines.add("§7from magical books.");
        lines.add("");
        lines.add("§7Right-click a spell book");
        lines.add("§7to learn the incantation.");
        lines.add("");
        lines.add("§7You cannot relearn");
        lines.add("§7already known spells.");
        lines.add("§0 ");

        // Curses
        lines.add("§5§lCurses");
        lines.add("");
        lines.add("§7Curses are dark spells");
        lines.add("§7cast upon other players.");
        lines.add("");
        lines.add("§7Examples:");
        lines.add("§7- Mining Fatigue");
        lines.add("§7- Bad Luck");
        lines.add("§7- Crop Failure");
        lines.add("§7- Phantom Activity");
        lines.add("§0 ");

        // Protection
        lines.add("§5§lProtection");
        lines.add("");
        lines.add("§7Protection rituals shield");
        lines.add("§7you from curses and");
        lines.add("§7scrying attempts.");
        lines.add("");
        lines.add("§7Place protective wards");
        lines.add("§7to defend yourself.");
        lines.add("");
        lines.add("§7Wards expire over time.");
        lines.add("§0 ");

        // Scrying
        lines.add("§5§lScrying");
        lines.add("");
        lines.add("§7Scrying allows you to");
        lines.add("§7observe other players.");
        lines.add("");
        lines.add("§7You can learn about:");
        lines.add("§7- Their location");
        lines.add("§7- Their health");
        lines.add("§7- Their held item");
        lines.add("§7- The weather");
        lines.add("§0 ");

        // Backfires
        lines.add("§5§lBackfires");
        lines.add("");
        lines.add("§7Failed rituals may");
        lines.add("§7backfire on the caster.");
        lines.add("");
        lines.add("§7Backfire effects:");
        lines.add("§7- Damage");
        lines.add("§7- Negative effects");
        lines.add("§7- Lost ingredients");
        lines.add("");
        lines.add("§7Be careful with magic.");
        lines.add("§0 ");

        // Arcane Exhaustion
        lines.add("§5§lArcane Exhaustion");
        lines.add("");
        lines.add("§7If a protected player");
        lines.add("§7is targeted, the attacker");
        lines.add("§7receives Arcane Exhaustion.");
        lines.add("");
        lines.add("§7While exhausted:");
        lines.add("§7- Cannot cast rituals");
        lines.add("§7- Cannot use incantations");
        lines.add("§7- Cannot learn spells");
        lines.add("");
        lines.add("§7Duration: 3 Minecraft days");
        lines.add("§0 ");

        // Progression
        lines.add("§5§lProgression");
        lines.add("");
        lines.add("§7Discover spell books:");
        lines.add("§7- Dungeons & structures");
        lines.add("§7- Villager trades");
        lines.add("§7- Exploration");
        lines.add("");
        lines.add("§7Experiment with ingredients");
        lines.add("§7to discover new rituals.");
        lines.add("§0 ");

        // Tips
        lines.add("§5§lTips & Advice");
        lines.add("");
        lines.add("§7- Always have protection");
        lines.add("§7- Experiment carefully");
        lines.add("§7- Trade with other witches");
        lines.add("§7- Respect the balance");
        lines.add("§7- The moon affects magic");
        lines.add("§7- Weather matters too");
        lines.add("");
        lines.add("§7May the spirits guide you.");

        return lines;
    }

    /**
     * Formats raw content lines into properly paginated book pages.
     *
     * @param rawLines the raw content lines
     * @return list of formatted pages
     */
    private List<String> formatPages(List<String> rawLines) {
        List<String> pages = new ArrayList<>();
        StringBuilder currentPage = new StringBuilder();
        int lineCount = 0;

        for (String line : rawLines) {
            // Page break indicator
            if (line.equals("§0 ")) {
                if (currentPage.length() > 0) {
                    pages.add(currentPage.toString());
                    currentPage = new StringBuilder();
                    lineCount = 0;
                }
                continue;
            }

            // Check if we need a new page
            if (lineCount >= MAX_LINES_PER_PAGE) {
                pages.add(currentPage.toString());
                currentPage = new StringBuilder();
                lineCount = 0;
            }

            // Handle long lines by wrapping
            List<String> wrappedLines = wrapLine(line);
            for (String wrapped : wrappedLines) {
                if (lineCount >= MAX_LINES_PER_PAGE) {
                    pages.add(currentPage.toString());
                    currentPage = new StringBuilder();
                    lineCount = 0;
                }
                if (lineCount > 0) {
                    currentPage.append("\n");
                }
                currentPage.append(wrapped);
                lineCount++;
            }
        }

        // Add final page if not empty
        if (currentPage.length() > 0) {
            pages.add(currentPage.toString());
        }

        // Enforce page limit
        if (pages.size() > MAX_PAGES) {
            pages = pages.subList(0, MAX_PAGES);
        }

        return pages;
    }

    /**
     * Wraps a line to fit within the maximum line length.
     *
     * @param line the line to wrap
     * @return list of wrapped lines
     */
    private List<String> wrapLine(String line) {
        List<String> wrapped = new ArrayList<>();

        if (line.length() <= MAX_LINE_LENGTH) {
            wrapped.add(line);
            return wrapped;
        }

        // Strip color codes for length calculation
        String stripped = ChatColor.stripColor(line);
        if (stripped.length() <= MAX_LINE_LENGTH) {
            wrapped.add(line);
            return wrapped;
        }

        // Simple word wrapping
        String[] words = line.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            String testStripped = ChatColor.stripColor(testLine);

            if (testStripped.length() > MAX_LINE_LENGTH && !currentLine.isEmpty()) {
                wrapped.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            wrapped.add(currentLine.toString());
        }

        return wrapped;
    }
}
