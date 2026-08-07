package com.witchcraft.coven;

import com.witchcraft.Witchcraft;
import com.witchcraft.data.CovenData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

/**
 * Protects chunks claimed by covens.
 * Only coven members can build, break, or alter terrain in claimed chunks.
 */
public class CovenChunkListener implements Listener {

    private final Witchcraft plugin;

    public CovenChunkListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String chunkKey = CovenManager.getChunkKey(event.getBlock().getLocation());

        CovenData coven = plugin.getCovenManager().getCovenForChunk(chunkKey);
        if (coven == null) return;

        if (!coven.isMember(player.getUniqueId())) {
            player.sendMessage("\u00A7cThis land is claimed by coven \u00A7e" +
                    coven.getName() + "\u00A7c. You cannot build here.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String chunkKey = CovenManager.getChunkKey(event.getBlock().getLocation());

        CovenData coven = plugin.getCovenManager().getCovenForChunk(chunkKey);
        if (coven == null) return;

        if (!coven.isMember(player.getUniqueId())) {
            player.sendMessage("\u00A7cThis land is claimed by coven \u00A7e" +
                    coven.getName() + "\u00A7c. You cannot build here.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&
            event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) return;

        String chunkKey = CovenManager.getChunkKey(event.getClickedBlock().getLocation());

        CovenData coven = plugin.getCovenManager().getCovenForChunk(chunkKey);
        if (coven == null) return;

        if (!coven.isMember(player.getUniqueId())) {
            // Allow basic interaction but block container access
            org.bukkit.Material type = event.getClickedBlock().getType();
            if (type.name().contains("CHEST") || type.name().contains("SHULKER_BOX") ||
                type.name().contains("BARREL") || type.name().contains("FURNACE") ||
                type.name().contains("BLAST_FURNACE") || type.name().contains("SMOKER") ||
                type.name().contains("BREWING") || type.name().contains("LECTERN") ||
                type.name().contains("BEACON") || type.name().contains("ANVIL") ||
                type.name().contains("GRINDSTONE") || type.name().contains("STONECUTTER") ||
                type.name().contains("LOOM") || type.name().contains("SMITHING")) {
                player.sendMessage("\u00A7cThis container belongs to coven \u00A7e" +
                        coven.getName() + "\u00A7c.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        String chunkKey = CovenManager.getChunkKey(event.getBlock().getLocation());

        CovenData coven = plugin.getCovenManager().getCovenForChunk(chunkKey);
        if (coven == null) return;

        // Remove blocks in claimed chunks from the explosion
        event.blockList().removeIf(block -> {
            String blockChunkKey = CovenManager.getChunkKey(block.getLocation());
            return plugin.getCovenManager().isChunkClaimed(blockChunkKey);
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        // Remove blocks in claimed chunks from the explosion
        event.blockList().removeIf(block -> {
            String blockChunkKey = CovenManager.getChunkKey(block.getLocation());
            return plugin.getCovenManager().isChunkClaimed(blockChunkKey);
        });
    }
}
