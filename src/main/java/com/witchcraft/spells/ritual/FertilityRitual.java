package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import com.witchcraft.data.FertilityRegion;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

/**
 * Blessing ritual that enhances growth and fertility in an area.
 */
public class FertilityRitual extends Spell {

    public FertilityRitual(Witchcraft plugin) {
        super(plugin, "fertility_ritual", "Blessing of Abundance",
                SpellCategory.FERTILITY,
                List.of(Ingredient.BONE_MEAL, Ingredient.GLOWSTONE_DUST, Ingredient.SUGAR),
                "terra foecunditas abundet",
                1200, 5, 0.85, 0.1, 0.05,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 16;
                long duration = 72000L;

                FertilityRegion region = new FertilityRegion(
                        UUID.randomUUID(),
                        caster.getUniqueId(),
                        caster.getWorld().getName(),
                        caster.getLocation().getBlockX(),
                        caster.getLocation().getBlockZ(),
                        radius,
                        System.currentTimeMillis() + (duration * 50),
                        getId()
                );

                plugin.getDataManager().saveFertilityRegion(region);
                applyFertilityEffects(caster.getLocation(), radius);
                caster.sendMessage(plugin.getConfigManager().getMessage("fertility.success"));
                playFertilityEffects(caster.getLocation(), radius);
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                applyWiltingEffect(caster.getLocation());
            }
            default -> {
            }
        }
        return result;
    }

    private void applyFertilityEffects(Location center, int radius) {
        var world = center.getWorld();
        if (world == null) return;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    var block = world.getBlockAt(
                            center.getBlockX() + x,
                            center.getBlockY(),
                            center.getBlockZ() + z);

                    if (isCrop(block.getType())) {
                        block.applyBoneMeal(org.bukkit.block.BlockFace.UP);
                    }
                }
            }
        }

        world.getNearbyPlayers(center, radius).forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 0));
        });
    }

    private void applyWiltingEffect(Location center) {
        var world = center.getWorld();
        if (world == null) return;

        int radius = 5;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    var block = world.getBlockAt(
                            center.getBlockX() + x,
                            center.getBlockY(),
                            center.getBlockZ() + z);

                    if (isCrop(block.getType())) {
                        block.setType(org.bukkit.Material.AIR);
                    }
                }
            }
        }
    }

    private void playFertilityEffects(Location center, int radius) {
        var world = center.getWorld();
        if (world == null) return;

        world.spawnParticle(Particle.HAPPY_VILLAGER, center,
                100, radius / 2.0, 2, radius / 2.0);
        world.playSound(center, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                1.0f, 1.5f);
        world.playSound(center, org.bukkit.Sound.BLOCK_GRASS_BREAK,
                0.5f, 1.0f);
    }

    private boolean isCrop(org.bukkit.Material material) {
        return switch (material) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART -> true;
            default -> false;
        };
    }
}
