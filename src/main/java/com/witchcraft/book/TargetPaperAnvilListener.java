package com.witchcraft.book;

import com.witchcraft.Witchcraft;
import com.witchcraft.util.TargetPaper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Detects when paper is renamed to a player's name at an anvil
 * and automatically converts it to a target paper.
 */
public class TargetPaperAnvilListener implements Listener {

    private final Witchcraft plugin;

    public TargetPaperAnvilListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstSlot = inventory.getItem(0);
        ItemStack result = event.getResult();

        // Must be paper in first slot
        if (firstSlot == null || firstSlot.getType() != Material.PAPER) return;

        // Must have a result (the renamed item)
        if (result == null) return;

        // Get the rename text from the anvil's rename text field
        String renameText = inventory.getRenameText();
        if (renameText == null || renameText.isBlank()) return;

        String trimmedName = renameText.trim();

        // Check if the renamed name matches an online player
        Player target = Bukkit.getPlayer(trimmedName);
        if (target == null) return;

        // Don't allow targeting yourself
        Player holder = null;
        if (inventory.getHolder() instanceof Player) {
            holder = (Player) inventory.getHolder();
        }
        if (holder != null && holder.getUniqueId().equals(target.getUniqueId())) return;

        // Create a target paper and set it as the result
        ItemStack targetPaper = TargetPaper.create(plugin, target.getName(), target.getUniqueId());

        // Preserve the enchantment glow if the original had one
        if (firstSlot.hasItemMeta()) {
            ItemMeta meta = firstSlot.getItemMeta();
            if (meta != null && meta.hasEnchants()) {
                ItemMeta targetMeta = targetPaper.getItemMeta();
                if (targetMeta != null) {
                    for (var ench : meta.getEnchants().entrySet()) {
                        targetMeta.addEnchant(ench.getKey(), ench.getValue(), true);
                    }
                    targetPaper.setItemMeta(targetMeta);
                }
            }
        }

        event.setResult(targetPaper);
    }
}
