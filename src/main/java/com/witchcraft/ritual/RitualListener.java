package com.witchcraft.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.util.TargetPaper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for cauldron interactions to trigger rituals.
 *
 * Ritual flow:
 * 1. Player fills cauldron with water bucket
 * 2. Player right-clicks cauldron while holding valid ingredients
 * 3. Ingredients are consumed and tracked on the cauldron
 * 4. Player optionally adds a target paper for player-targeting spells
 * 5. When all required ingredients (and target paper if needed) are added, the ritual begins charging
 * 6. After the charge duration, the spell executes
 */
public class RitualListener implements Listener {

    private final Witchcraft plugin;

    public RitualListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles water bucket emptying into cauldrons.
     */
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getBucket() != Material.WATER_BUCKET) return;

        Block block = event.getBlock();
        Block cauldron = block.getRelative(0, 0, 0);
        if (cauldron.getType() == Material.CAULDRON) {
            // Cauldron will be filled by vanilla mechanics
        }
    }

    /**
     * Handles player right-clicking cauldrons with ingredients or target papers.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        // Only interact with water cauldrons
        if (block.getType() != Material.WATER_CAULDRON) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Air hand = show status
        if (item.getType() == Material.AIR) {
            showCauldronStatus(player, block);
            return;
        }

        // Check if the item is a target paper
        if (item.getType() == Material.PAPER && TargetPaper.isTargetPaper(plugin, item)) {
            event.setCancelled(true);
            plugin.getRitualManager().addTargetPaper(player, block, item);
            return;
        }

        // Check if the item is a valid ingredient
        Ingredient ingredient = Ingredient.fromMaterial(item.getType());
        if (ingredient == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.ingredient-failed"));
            return;
        }

        // Cancel the default cauldron interaction
        event.setCancelled(true);

        // Delegate to RitualManager
        plugin.getRitualManager().addIngredient(player, block, ingredient);
    }

    /**
     * Handles cauldron level changes.
     */
    @EventHandler
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        if (event.getNewLevel() < event.getOldLevel()) {
            var location = event.getBlock().getLocation();
        }
    }

    /**
     * Shows the current status of a cauldron ritual.
     */
    private void showCauldronStatus(Player player, Block block) {
        player.sendMessage("\u00A75\u00A7l--- Cauldron Status ---");
        player.sendMessage("\u00A77Cauldron: \u00A7fFilled with water");
        player.sendMessage("\u00A77Add ingredients to begin a ritual.");
        player.sendMessage("\u00A77For player-targeting spells, add a");
        player.sendMessage("\u00A77target paper (paper named at an anvil).");
        player.sendMessage("\u00A75\u00A7l--- End Status ---");
    }
}
