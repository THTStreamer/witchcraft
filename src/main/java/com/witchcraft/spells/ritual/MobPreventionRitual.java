package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import com.witchcraft.data.WardData;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Warding ritual that prevents hostile mobs from spawning in a chunk.
 */
public class MobPreventionRitual extends Spell {

    public MobPreventionRitual(Witchcraft plugin) {
        super(plugin, "mob_prevention_ritual", "Guardian's Ward",
                SpellCategory.WARDING,
                List.of(Ingredient.OBSIDIAN, Ingredient.ECHO_SHARD, Ingredient.BONE_MEAL),
                "custos praesidio locus",
                1800, 5, 0.85, 0.1, 0.05,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                Chunk chunk = caster.getLocation().getChunk();
                long duration = 36000L; // 30 minutes

                WardData ward = new WardData(
                        UUID.randomUUID(),
                        caster.getUniqueId(),
                        caster.getWorld().getName(),
                        chunk.getX(),
                        chunk.getZ(),
                        System.currentTimeMillis() + (duration * 50),
                        getId()
                );

                plugin.getDataManager().saveWard(ward);

                caster.sendMessage(plugin.getConfigManager().getMessage("mob-prevention.success"));

                // Play effects
                playWardEffects(caster.getLocation(), chunk);
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                // Spawn hostile mobs around caster
                spawnHostiles(caster.getLocation());
            }
            default -> {
            }
        }
        return result;
    }

    /**
     * Plays visual effects for warding ritual.
     */
    private void playWardEffects(Location center, Chunk chunk) {
        var world = center.getWorld();
        if (world == null) return;

        // Spawn enchant particles around chunk border
        Location chunkCenter = new Location(world,
                chunk.getX() * 16 + 8,
                center.getY(),
                chunk.getZ() * 16 + 8);

        world.spawnParticle(org.bukkit.Particle.ENCHANT, chunkCenter,
                200, 8, 2, 8);

        // Play sound
        world.playSound(center, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE,
                0.5f, 1.2f);
    }

    /**
     * Spawns hostile mobs around a location for backfire.
     */
    private void spawnHostiles(Location center) {
        var world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < 5; i++) {
            Location spawnLoc = center.clone().add(
                    (Math.random() - 0.5) * 20,
                    0,
                    (Math.random() - 0.5) * 20);
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            var entity = world.spawnEntity(spawnLoc,
                    org.bukkit.entity.EntityType.ZOMBIE);
            entity.setCustomName("\u00A74Ward Breaker");
        }
    }
}
