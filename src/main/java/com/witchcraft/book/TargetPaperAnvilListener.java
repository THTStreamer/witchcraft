package com.witchcraft.book;

import com.witchcraft.Witchcraft;
import com.witchcraft.util.TargetPaper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class TargetPaperAnvilListener implements Listener {

    private static final int TARGET_PAPER_COST = 30;

    private final Witchcraft plugin;

    private final Set<Integer> targetPaperAnvils = new HashSet<>();

    public TargetPaperAnvilListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstSlot = inventory.getItem(0);
        ItemStack result = event.getResult();

        if (firstSlot == null || firstSlot.getType() != Material.PAPER) return;
        if (result == null) return;

        String renameText = inventory.getRenameText();
        if (renameText == null || renameText.isBlank()) return;

        String trimmedName = renameText.trim();

        Player target = Bukkit.getPlayer(trimmedName);
        if (target == null) return;

        Player holder = null;
        if (inventory.getHolder() instanceof Player) {
            holder = (Player) inventory.getHolder();
        }
        if (holder != null && holder.getUniqueId().equals(target.getUniqueId())) return;

        ItemStack targetPaper = TargetPaper.create(plugin, target.getName(), target.getUniqueId());

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
        targetPaperAnvils.add(System.identityHashCode(inventory));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inventory = event.getInventory();
        if (!(inventory instanceof AnvilInventory anvil)) return;
        if (event.getRawSlot() != 2) return;
        if (event.getClick() == ClickType.UNKNOWN) return;

        int anvilId = System.identityHashCode(anvil);
        if (!targetPaperAnvils.contains(anvilId)) return;

        targetPaperAnvils.remove(anvilId);

        if (player.getLevel() < TARGET_PAPER_COST) {
            event.setCancelled(true);
            player.sendMessage("\u00A7cYou need " + TARGET_PAPER_COST +
                    " levels to create a target paper. You have " + player.getLevel() + ".");
            return;
        }

        player.setLevel(player.getLevel() - TARGET_PAPER_COST);
        player.sendMessage("\u00A77Target paper created! \u00A7c-" + TARGET_PAPER_COST + " levels");
    }
}
