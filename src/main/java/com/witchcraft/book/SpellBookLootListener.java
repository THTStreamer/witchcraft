package com.witchcraft.book;

import com.witchcraft.Witchcraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Injects spell books into dungeon chest loot.
 * 3% chance per chest, max 1 book per loot cycle.
 */
public class SpellBookLootListener implements Listener {

    private static final double BOOK_CHANCE = 0.03;
    private static final int MAX_BOOKS_PER_CHEST = 1;

    private final Witchcraft plugin;
    private final Random random = new Random();

    public SpellBookLootListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        // Only inject into chest-type loot (dungeons, mineshafts, etc.)
        LootTable table = event.getLootTable();
        String tableName = table.getKey().toString();

        // Only apply to dungeon/structure loot tables
        if (!isDungeonLoot(tableName)) return;

        // Check if we already added a book (prevent duplicates)
        List<ItemStack> existingItems = event.getLoot();
        for (ItemStack item : existingItems) {
            if (item.getType() == org.bukkit.Material.WRITTEN_BOOK) {
                org.bukkit.inventory.meta.BookMeta meta =
                    (org.bukkit.inventory.meta.BookMeta) item.getItemMeta();
                if (meta != null && meta.getAuthor() != null &&
                    meta.getAuthor().equals("The Old Ones")) {
                    return; // Already has a spell book
                }
            }
        }

        // Roll for book inclusion
        if (random.nextDouble() > BOOK_CHANCE) return;

        // Get a random spell book
        SpellBookManager bookManager = plugin.getSpellBookManager();
        if (bookManager == null) return;

        String bookId = bookManager.getRandomBookId();
        ItemStack bookItem = bookManager.createBookItem(bookId);
        if (bookItem == null) return;

        // Add the book to the loot
        List<ItemStack> loot = new ArrayList<>(existingItems);
        loot.add(bookItem);
        event.setLoot(loot);
    }

    /**
     * Checks if a loot table key is a dungeon-type loot table.
     */
    private boolean isDungeonLoot(String tableName) {
        return tableName.contains("chests/") ||
               tableName.contains("dungeon") ||
               tableName.contains("stronghold") ||
               tableName.contains("pyramid") ||
               tableName.contains("jungle") ||
               tableName.contains("shipwreck") ||
               tableName.contains("buried_treasure") ||
               tableName.contains("mineshaft") ||
               tableName.contains("pillager_outpost") ||
               tableName.contains("desert_pyramid") ||
               tableName.contains("igloo") ||
               tableName.contains("nether_bridge") ||
               tableName.contains("bastion") ||
               tableName.contains("end_city") ||
               tableName.contains("ancient_city") ||
               tableName.contains("trail_ruins");
    }
}
