package com.witchcraft.incantation;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellResult;
import com.witchcraft.util.TargetPaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Listens for chat messages to detect incantations.
 * When a player-targeting spell is cast via incantation, a target paper
 * must be held in the player's main hand or off hand.
 */
public class IncantationListener implements Listener {

    private final Witchcraft plugin;

    public IncantationListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        UUID playerId = player.getUniqueId();

        // Check if player is exhausted
        if (plugin.getArcaneExhaustion().isExhausted(playerId)) {
            return;
        }

        // Check if player has permission
        if (!player.hasPermission("witchcraft.cast")) {
            return;
        }

        // Try to match an incantation
        Incantation incantation = plugin.getIncantationManager().matchIncantation(message);
        if (incantation == null) {
            return;
        }

        // Check if player has learned this incantation
        if (!plugin.getIncantationManager().hasLearned(playerId, incantation.getId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("incantation.unknown"));
            return;
        }

        // Check cooldown
        if (plugin.getIncantationManager().getCooldowns().isOnCooldown(playerId, incantation.getSpellId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("incantation.on-cooldown"));
            return;
        }

        // Prevent the message from being broadcast
        event.setCancelled(true);

        // Get the associated spell
        Spell spell = plugin.getSpellRegistry().getSpell(incantation.getSpellId());
        if (spell == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("incantation.unknown"));
            return;
        }

        // Check XP cost
        if (player.getLevel() < spell.getXpCost()) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.insufficient-xp"));
            return;
        }

        // Cast the spell via incantation (must run on main thread)
        player.getServer().getScheduler().runTask(plugin, () -> {
            Player target = null;

            // If spell requires a target, look for target paper in inventory
            if (spell.requiresTarget()) {
                ItemStack targetPaper = TargetPaper.findInInventory(plugin, player);
                if (targetPaper == null) {
                    player.sendMessage(plugin.getConfigManager().getMessage("incantation.target-required"));
                    return;
                }

                UUID targetUUID = TargetPaper.getTargetUUID(plugin, targetPaper);
                if (targetUUID == null) {
                    player.sendMessage(plugin.getConfigManager().getMessage("incantation.invalid-target"));
                    return;
                }

                target = Bukkit.getPlayer(targetUUID);
                if (target == null || !target.isOnline()) {
                    player.sendMessage(plugin.getConfigManager().getMessage("incantation.target-offline"));
                    return;
                }

                // Don't allow targeting yourself
                if (targetUUID.equals(player.getUniqueId())) {
                    player.sendMessage(plugin.getConfigManager().getMessage("incantation.cannot-target-self"));
                    return;
                }

                // Consume the target paper
                TargetPaper.consumeFromInventory(plugin, player);
            }

            SpellResult result = spell.execute(player, player.getLocation(), target);

            // Set cooldown
            plugin.getIncantationManager().getCooldowns().setCooldown(
                    playerId, spell.getId(), spell.getCooldownTicks());

            // Consume XP
            player.setLevel(player.getLevel() - spell.getXpCost());

            // Play effects based on result
            switch (result) {
                case SUCCESS -> {
                    player.sendMessage(plugin.getConfigManager().getMessage("incantation.cast-success"));
                    playCastEffects(player, "success");
                }
                case FAILURE -> {
                    player.sendMessage(plugin.getConfigManager().getMessage("incantation.cast-fail"));
                    playCastEffects(player, "failure");
                }
                case BACKFIRE -> {
                    player.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                    playCastEffects(player, "backfire");
                }
                default -> {
                }
            }
        });
    }

    /**
     * Plays visual/sound effects for incantation casting.
     */
    private void playCastEffects(Player player, String type) {
        var world = player.getWorld();
        var location = player.getLocation();

        switch (type) {
            case "success" -> {
                world.spawnParticle(org.bukkit.Particle.ENCHANT, location.add(0, 1, 0),
                        30, 0.5, 0.5, 0.5);
                world.playSound(location, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE,
                        0.5f, 1.2f);
            }
            case "failure" -> {
                world.spawnParticle(org.bukkit.Particle.SMOKE, location.add(0, 1, 0),
                        15, 0.3, 0.3, 0.3);
                world.playSound(location, org.bukkit.Sound.BLOCK_LAVA_POP,
                        0.3f, 0.8f);
            }
            case "backfire" -> {
                world.spawnParticle(org.bukkit.Particle.FLAME, location.add(0, 1, 0),
                        20, 0.5, 0.5, 0.5);
                world.spawnParticle(org.bukkit.Particle.SMOKE, location.add(0, 1, 0),
                        20, 0.5, 0.5, 0.5);
                world.playSound(location, org.bukkit.Sound.ENTITY_WITHER_HURT,
                        0.5f, 0.5f);
            }
        }
    }
}
