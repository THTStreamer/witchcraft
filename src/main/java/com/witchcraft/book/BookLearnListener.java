package com.witchcraft.book;

import com.witchcraft.Witchcraft;
import com.witchcraft.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles learning spells/rituals when a player reads a spell book.
 * Both regular and coven books are learnable; for coven only the caster needs to know.
 * Books contain paginated content respecting 15 chars/line, 14 lines/page, 50 pages.
 */
public class BookLearnListener implements Listener {

    private final Witchcraft plugin;

    public BookLearnListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.WRITTEN_BOOK) return;

        var meta = item.getItemMeta();
        if (!(meta instanceof org.bukkit.inventory.meta.BookMeta bookMeta)) return;

        // Check if it's a spell book
        String spellId = null;
        boolean isSpell = false;
        boolean isLore = false;

        var pdc = bookMeta.getPersistentDataContainer();
        NamespacedKey spellKey = new NamespacedKey("witchcraft", SpellBookManager.SPELL_BOOK_KEY);
        NamespacedKey loreKey = new NamespacedKey("witchcraft", SpellBookManager.LORE_BOOK_KEY);

        if (pdc.has(spellKey, PersistentDataType.STRING)) {
            spellId = pdc.get(spellKey, PersistentDataType.STRING);
            isSpell = true;
        } else if (pdc.has(loreKey, PersistentDataType.STRING)) {
            isLore = true;
            // Lore books don't teach, just flavor
            return;
        } else {
            // Fallback: check author The Old Ones to catch old books without PDC
            if (!"The Old Ones".equals(bookMeta.getAuthor())) return;
            // Try to resolve by title lookup
            spellId = resolveByTitle(bookMeta.getTitle());
            if (spellId == null) return;
            isSpell = true;
        }

        if (!isSpell || spellId == null) return;

        // Prevent opening book GUI from spamming? We still allow learning.
        // Learn both ritual and incantation(s)
        final String finalSpellId = spellId;
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        boolean learnedSomething = false;
        boolean alreadyKnownRitual = data.knowsRitual(finalSpellId);
        StringBuilder learnedMsg = new StringBuilder();

        // Ritual knowledge (covers regular and coven rituals)
        if (!alreadyKnownRitual) {
            // Verify this spellId actually has a ritual recipe or is a known spell
            var spell = plugin.getSpellRegistry().getSpell(finalSpellId);
            var recipe = plugin.getRitualManager().getRecipeRegistry().getBySpellId(finalSpellId);
            var covenSpell = plugin.getCovenSpellRegistry().getSpell(finalSpellId);
            // If any of these exist, teach ritual
            if (spell != null || recipe != null || covenSpell != null) {
                data.learnRitual(finalSpellId);
                learnedSomething = true;
                learnedMsg.append("§7Learned ritual: §f").append(finalSpellId);
            }
        }

        // Incantation knowledge: find all incantations that map to this spellId
        var incantations = plugin.getIncantationManager().getAllIncantations().stream()
                .filter(i -> i.getSpellId().equals(finalSpellId))
                .toList();

        for (var inc : incantations) {
            String phrase = inc.getIncantation();
            if (!data.hasLearnedIncantation(phrase)) {
                data.learnIncantation(phrase);
                learnedSomething = true;
                if (learnedMsg.length() > 0) learnedMsg.append("§7, ");
                learnedMsg.append("§7verse: §f\"").append(phrase).append("\"");
            }
        }

        // Coven spell incantation lines: if this spellId is a coven spell, teach its lines as well
        var covenSpell = plugin.getCovenSpellRegistry().getSpell(finalSpellId);
        if (covenSpell != null) {
            // For coven spells, we store ritual knowledge above; also inform about chant lines
            if (!learnedSomething && alreadyKnownRitual) {
                // Already knew, but still show chant lines
                player.sendMessage("§5§lYou already know §f" + covenSpell.getDisplayName() + "§5§l.");
                player.sendMessage("§7Chant lines: §f" + String.join(" §7| §f", covenSpell.getIncantationLines()));
                // Don't return, still show message
            }
        }

        if (learnedSomething) {
            player.sendMessage("§5§lYou have learned from the tome!");
            player.sendMessage(learnedMsg.toString());
            player.getWorld().spawnParticle(org.bukkit.Particle.ENCHANT, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5);
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);
            // Keep book (don't consume) - do not cancel so book GUI still opens
        } else {
            // Already known
            boolean hasInc = incantations.isEmpty() || incantations.stream().allMatch(i -> data.hasLearnedIncantation(i.getIncantation()));
            boolean hasRitual = data.knowsRitual(finalSpellId);
            if (hasInc && hasRitual) {
                player.sendMessage("§7You already know §f" + finalSpellId + "§7.");
            } else if (!incantations.isEmpty() && !hasInc) {
                // Edge: had ritual but not incantation, would have learned above
            }
        }
    }

    private String resolveByTitle(String title) {
        if (title == null) return null;
        var manager = plugin.getSpellBookManager();
        if (manager == null) return null;
        String stripped = org.bukkit.ChatColor.stripColor(title);
        for (var book : manager.getAllBooks()) {
            if (book.getTitle().equalsIgnoreCase(stripped) || org.bukkit.ChatColor.stripColor(book.getTitle()).equalsIgnoreCase(stripped)) {
                return book.getSpellId();
            }
        }
        return null;
    }
}
