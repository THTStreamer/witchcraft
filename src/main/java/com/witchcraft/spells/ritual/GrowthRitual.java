package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.List;

public class GrowthRitual extends Spell {

    public GrowthRitual(Witchcraft plugin) {
        super(plugin, "growth_ritual", "Bloom of the Green Hand",
                SpellCategory.FERTILITY,
                List.of(Ingredient.BONE_MEAL, Ingredient.GLOWSTONE_DUST, Ingredient.QUARTZ),
                "viridis manus florescere",
                1200, 4, 0.85, 0.1, 0.05,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 8;
                int grown = 0;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + z * z <= radius * radius) {
                            Block block = location.getWorld().getBlockAt(
                                    location.getBlockX() + x,
                                    location.getBlockY(),
                                    location.getBlockZ() + z);
                            if (isGrowable(block.getType()) && block.getBlockData() instanceof Ageable age) {
                                if (age.getAge() < age.getMaximumAge()) {
                                    age.setAge(age.getMaximumAge());
                                    block.setBlockData(age);
                                    grown++;
                                }
                            }
                        }
                    }
                }
                caster.sendMessage("\u00A7aGrew \u00A7f" + grown + " \u00A7acrops.");
                playEffects(caster.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                caster.damage(2.0);
            }
            default -> {}
        }
        return result;
    }

    private boolean isGrowable(Material mat) {
        return switch (mat) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART -> true;
            default -> false;
        };
    }

    private void playEffects(Location loc) {
        var world = loc.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, loc, 60, 2, 1, 2);
        world.playSound(loc, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }
}
