package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class WaterPurificationRitual extends Spell {

    public WaterPurificationRitual(Witchcraft plugin) {
        super(plugin, "water_purification_ritual", "Purification of the Clear Spring",
                SpellCategory.CLEANSING,
                List.of(Ingredient.GHAST_TEAR, Ingredient.QUARTZ, Ingredient.BONE_MEAL),
                "aqua purificatio fons clarus",
                1200, 4, 0.85, 0.1, 0.05,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 6;
                int purified = 0;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + z * z <= radius * radius) {
                            Block block = location.getWorld().getBlockAt(
                                    location.getBlockX() + x,
                                    location.getBlockY(),
                                    location.getBlockZ() + z);
                            if (block.getType() == Material.WATER) {
                                block.setType(Material.AIR);
                                purified++;
                            }
                        }
                    }
                }
                caster.sendMessage("\u00A7aPurified \u00A7f" + purified + " \u00A7awater blocks.");
                playEffects(caster.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                location.getWorld().createExplosion(location, 1.0f);
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location loc) {
        var world = loc.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING, loc, 40, 1, 1, 1);
        world.playSound(loc, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }
}
