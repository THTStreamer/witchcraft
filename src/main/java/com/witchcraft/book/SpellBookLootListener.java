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
 * Injects spell books and lore books into chest loot.
 * Spell books have weighted rarity: common 3%, rare powerful 0.3%.
 * Lore books have 1.5% chance.
 */
public class SpellBookLootListener implements Listener {

    private static final int MAX_BOOKS_PER_CHEST = 1;

    private final Witchcraft plugin;
    private final Random random = new Random();

    public SpellBookLootListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        LootTable table = event.getLootTable();
        String tableName = table.getKey().toString();

        if (!isDungeonLoot(tableName)) return;

        List<ItemStack> existingItems = event.getLoot();
        boolean hasSpellBook = false;
        boolean hasLoreBook = false;
        for (ItemStack item : existingItems) {
            if (item.getType() == org.bukkit.Material.WRITTEN_BOOK) {
                var meta = item.getItemMeta();
                if (meta instanceof org.bukkit.inventory.meta.BookMeta bm && bm.getAuthor() != null && bm.getAuthor().equals("The Old Ones")) {
                    // Check PDC to distinguish spell vs lore
                    var pdc = bm.getPersistentDataContainer();
                    if (pdc.has(new org.bukkit.NamespacedKey("witchcraft", SpellBookManager.SPELL_BOOK_KEY), org.bukkit.persistence.PersistentDataType.STRING)) {
                        hasSpellBook = true;
                    }
                    if (pdc.has(new org.bukkit.NamespacedKey("witchcraft", SpellBookManager.LORE_BOOK_KEY), org.bukkit.persistence.PersistentDataType.STRING)) {
                        hasLoreBook = true;
                    }
                    if (hasSpellBook && hasLoreBook) return;
                }
            }
        }

        // Try spell book (weighted by power)
        if (!hasSpellBook) {
            SpellBookManager bookManager = plugin.getSpellBookManager();
            if (bookManager != null) {
                String weightedId = getWeightedRandomSpellId(bookManager);
                SpellBookData data = bookManager.getBook(weightedId);
                if (data != null) {
                    double chance = getChanceForPrice(data.getPrice());
                    if (random.nextDouble() < chance) {
                        ItemStack bookItem = bookManager.createBookItem(weightedId);
                        if (bookItem != null) {
                            List<ItemStack> loot = new ArrayList<>(existingItems);
                            loot.add(bookItem);
                            event.setLoot(loot);
                            existingItems = loot; // update for lore check
                        }
                    }
                }
            }
        }

        // Try lore book (1.2% chance per eligible chest)
        if (!hasLoreBook) {
            LoreBookManager loreManager = plugin.getLoreBookManager();
            if (loreManager != null && random.nextDouble() < 0.012) {
                String loreId = loreManager.getRandomLoreId();
                ItemStack loreItem = loreManager.createLoreBookItem(loreId);
                if (loreItem != null) {
                    List<ItemStack> loot = new ArrayList<>(event.getLoot());
                    // Avoid adding lore if spell already added and we want max 2 books per chest
                    // Allow both spell + lore in same chest (max 2)
                    loot.add(loreItem);
                    event.setLoot(loot);
                }
            }
        }
    }

    /**
     * Weighted random: powerful (high price) less likely.
     * Weight = 30 - price*1.0, clamped min 1. Rare books pick less.
     */
    private String getWeightedRandomSpellId(SpellBookManager manager) {
        var all = new ArrayList<>(manager.getAllBooks());
        if (all.isEmpty()) return manager.getRandomBookId();
        // Build weighted list
        double totalWeight = 0;
        List<Double> weights = new ArrayList<>();
        for (var b : all) {
            double w = Math.max(1, 30 - b.getPrice() * 1.2); // price 7->21.6, price 25->1
            // Extremely high tier 22-25 should be ~1-3 weight
            if (b.getPrice() >= 19) w = 1 + random.nextDouble(); // 1-2
            else if (b.getPrice() >= 15) w = 2 + random.nextDouble()*2; // 2-4
            weights.add(w);
            totalWeight += w;
        }
        double r = random.nextDouble() * totalWeight;
        double cum = 0;
        for (int i = 0; i < all.size(); i++) {
            cum += weights.get(i);
            if (r < cum) return all.get(i).getSpellId();
        }
        return all.get(all.size()-1).getSpellId();
    }

    private double getChanceForPrice(int price) {
        if (price >= 19) return 0.003; // 0.3% powerful
        if (price >= 15) return 0.005;
        if (price >= 11) return 0.008;
        if (price >= 9) return 0.015;
        return 0.03; // 3% common
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
                tableName.contains("trail_ruins") ||
                tableName.contains("village");
    }
}
