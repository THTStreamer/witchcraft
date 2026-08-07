package com.witchcraft.book;

import com.witchcraft.Witchcraft;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Adds spell book trades to librarian villagers.
 * Each librarian has a chance to offer one spell book trade.
 */
public class SpellBookTradeListener implements Listener {

    private static final double TRADE_CHANCE = 0.15;
    private final Witchcraft plugin;
    private final Random random = new Random();

    public SpellBookTradeListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVillagerSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager villager)) return;

        // Only affect librarians
        if (villager.getProfession() != Villager.Profession.LIBRARIAN) return;

        // Check if this villager already has a spell book trade
        for (MerchantRecipe recipe : villager.getRecipes()) {
            if (isSpellBookTrade(recipe)) return;
        }

        // Roll for adding a trade
        if (random.nextDouble() > TRADE_CHANCE) return;

        // Get a random spell book
        SpellBookManager bookManager = plugin.getSpellBookManager();
        if (bookManager == null) return;

        String bookId = bookManager.getRandomBookId();
        SpellBookData bookData = bookManager.getBook(bookId);
        if (bookData == null) return;

        ItemStack bookItem = bookManager.createBookItem(bookId);
        if (bookItem == null) return;

        // Create the trade recipe
        // Price scales with book complexity (more expensive for powerful spells)
        int emeraldCost = bookData.getPrice();

        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());

        // Create the recipe (item to buy, max uses, price)
        MerchantRecipe trade = new MerchantRecipe(bookItem, 1);
        trade.addIngredient(new ItemStack(Material.EMERALD, emeraldCost));
        // Add paper as second ingredient for librarian flavor
        trade.addIngredient(new ItemStack(Material.PAPER, 1));
        trade.setUses(0);
        trade.setMaxUses(3 + random.nextInt(4)); // 3-6 uses before restock

        recipes.add(trade);
        villager.setRecipes(recipes);
    }

    /**
     * Checks if a merchant recipe is a spell book trade.
     */
    private boolean isSpellBookTrade(MerchantRecipe recipe) {
        ItemStack result = recipe.getResult();
        if (result.getType() != Material.WRITTEN_BOOK) return false;

        BookMeta meta = (BookMeta) result.getItemMeta();
        return meta != null && meta.getAuthor() != null &&
               meta.getAuthor().equals("The Old Ones");
    }
}
