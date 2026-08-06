package com.witchcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

/**
 * Utility class for creating and reading target papers.
 * A target paper is a piece of paper with a player's name written on it,
 * used to designate the target of player-targeting spells.
 */
public final class TargetPaper {

    /**
     * The PersistentDataContainer key used to store the target UUID.
     */
    public static final String TARGET_UUID_KEY = "witchcraft_target_uuid";

    /**
     * The PersistentDataContainer key used to mark an item as a target paper.
     */
    public static final String TARGET_PAPER_KEY = "witchcraft_target_paper";

    private TargetPaper() {
    }

    /**
     * Creates a target paper for a specific player.
     *
     * @param plugin     the plugin instance
     * @param targetName the target player's name
     * @param targetUUID the target player's UUID
     * @return the target paper ItemStack
     */
    public static ItemStack create(Plugin plugin, String targetName, UUID targetUUID) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) return paper;

        meta.setDisplayName("\u00A77Target: \u00A7c" + targetName);
        meta.setLore(List.of(
                "\u00A78Right-click or throw into cauldron",
                "\u00A78to designate " + targetName + " as target",
                "\u00A77" + targetUUID
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, TARGET_PAPER_KEY), PersistentDataType.BYTE, (byte) 1);
        pdc.set(new NamespacedKey(plugin, TARGET_UUID_KEY), PersistentDataType.STRING, targetUUID.toString());

        paper.setItemMeta(meta);
        return paper;
    }

    /**
     * Checks if an item is a target paper.
     *
     * @param plugin the plugin instance
     * @param item   the item to check
     * @return true if the item is a target paper
     */
    public static boolean isTargetPaper(Plugin plugin, ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(new NamespacedKey(plugin, TARGET_PAPER_KEY), PersistentDataType.BYTE);
    }

    /**
     * Extracts the target UUID from a target paper.
     *
     * @param plugin the plugin instance
     * @param item   the target paper item
     * @return the target UUID, or null if not a valid target paper
     */
    public static UUID getTargetUUID(Plugin plugin, ItemStack item) {
        if (!isTargetPaper(plugin, item)) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String uuidStr = pdc.get(new NamespacedKey(plugin, TARGET_UUID_KEY), PersistentDataType.STRING);
        if (uuidStr == null) return null;

        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Extracts the target player's name from a target paper.
     *
     * @param plugin the plugin instance
     * @param item   the target paper item
     * @return the target name, or null if not a valid target paper
     */
    public static String getTargetName(Plugin plugin, ItemStack item) {
        if (!isTargetPaper(plugin, item)) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) return null;

        // Display name format: "§7Target: §c<name>"
        String name = meta.getDisplayName();
        String prefix = "\u00A77Target: \u00A7c";
        if (name.startsWith(prefix)) {
            return name.substring(prefix.length());
        }
        return null;
    }

    /**
     * Searches a player's inventory for a target paper and returns it.
     *
     * @param plugin the plugin instance
     * @param player the player to search
     * @return the target paper ItemStack, or null if not found
     */
    public static ItemStack findInInventory(Plugin plugin, org.bukkit.entity.Player player) {
        for (var item : player.getInventory().getContents()) {
            if (isTargetPaper(plugin, item)) {
                return item;
            }
        }
        // Also check off-hand
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (isTargetPaper(plugin, offHand)) {
            return offHand;
        }
        return null;
    }

    /**
     * Consumes a target paper from a player's inventory.
     *
     * @param plugin the plugin instance
     * @param player the player whose inventory to search
     * @return true if a target paper was found and consumed
     */
    public static boolean consumeFromInventory(Plugin plugin, org.bukkit.entity.Player player) {
        ItemStack paper = findInInventory(plugin, player);
        if (paper == null) return false;

        if (paper.getAmount() > 1) {
            paper.setAmount(paper.getAmount() - 1);
        } else {
            player.getInventory().removeItem(paper);
        }
        return true;
    }

    /**
     * Searches a list of items for a target paper (used for cauldron ingredients).
     *
     * @param plugin the plugin instance
     * @param items  the items to search
     * @return the target paper ItemStack, or null if not found
     */
    public static ItemStack findInItems(Plugin plugin, Iterable<ItemStack> items) {
        for (ItemStack item : items) {
            if (isTargetPaper(plugin, item)) {
                return item;
            }
        }
        return null;
    }
}
