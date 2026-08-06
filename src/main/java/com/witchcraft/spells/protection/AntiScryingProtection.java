package com.witchcraft.spells.protection;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Anti-scrying protection that blocks divination attempts.
 */
public class AntiScryingProtection extends Spell {

    private final Map<UUID, Long> protectedPlayers = new ConcurrentHashMap<>();

    public AntiScryingProtection(Witchcraft plugin) {
        super(plugin, "anti_scrying_protection", "Veil of Obscurity",
                SpellCategory.PROTECTION,
                List.of(Ingredient.ECHO_SHARD, Ingredient.AMETHYST_SHARD, Ingredient.CRYING_OBSIDIAN),
                "obscurus ne videar",
                1800, 5, 0.9, 0.08, 0.02,
                "witchcraft.protection");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        Player protectTarget = target != null ? target : caster;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                // Apply anti-scrying protection
                long duration = 72000L; // 1 hour in ticks
                protectedPlayers.put(protectTarget.getUniqueId(),
                        System.currentTimeMillis() + (duration * 50));

                protectTarget.sendMessage(plugin.getConfigManager().getMessage("protection.applied"));
                if (target != null) {
                    caster.sendMessage(plugin.getConfigManager().getMessage("protection.applied-to-other",
                            "%target%", target.getName()));
                }

                // Play effects
                playVeilEffects(protectTarget.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                // Remove all protections from caster
                protectedPlayers.remove(caster.getUniqueId());
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {
            }
        }
        return result;
    }

    /**
     * Checks if a player is protected from scrying.
     *
     * @param playerId the player's UUID
     * @return true if the player is protected
     */
    public boolean isProtected(UUID playerId) {
        Long expiry = protectedPlayers.get(playerId);
        if (expiry == null) return false;

        if (System.currentTimeMillis() >= expiry) {
            protectedPlayers.remove(playerId);
            return false;
        }
        return true;
    }

    /**
     * Removes anti-scrying protection from a player.
     *
     * @param playerId the player's UUID
     */
    public void removeProtection(UUID playerId) {
        protectedPlayers.remove(playerId);
    }

    private void playVeilEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;

        world.spawnParticle(org.bukkit.Particle.SMOKE,
                center.add(0, 1, 0), 50, 1, 1, 1);
        world.spawnParticle(org.bukkit.Particle.CAMPFIRE_COSY_SMOKE,
                center.add(0, 1, 0), 20, 0.5, 0.5, 0.5);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH,
                0.5f, 1.5f);
    }
}
