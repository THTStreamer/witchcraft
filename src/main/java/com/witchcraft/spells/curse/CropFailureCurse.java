package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Curse spell that causes crop failure around a target's base.
 */
public class CropFailureCurse extends Spell {

    public CropFailureCurse(Witchcraft plugin) {
        super(plugin, "crop_failure_curse", "Curse of Barren Fields",
                SpellCategory.CURSE,
                List.of(Ingredient.BONE_MEAL, Ingredient.SUGAR, Ingredient.NETHER_WART),
                "agricultura maledictio sterile",
                600, 3, 0.8, 0.15, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                target.sendMessage(plugin.getConfigManager().getMessage("curse.received"));
                caster.sendMessage(plugin.getConfigManager().getMessage("curse.applied",
                        "%target%", target.getName()));

                Location targetLoc = target.getLocation();
                int radius = 10;

                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + z * z <= radius * radius) {
                            var block = targetLoc.getWorld().getBlockAt(
                                    targetLoc.getBlockX() + x,
                                    targetLoc.getBlockY(),
                                    targetLoc.getBlockZ() + z);

                            if (isCrop(block.getType())) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {
            }
        }
        return result;
    }

    private boolean isCrop(Material material) {
        return switch (material) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART,
                    SWEET_BERRY_BUSH, COCOA -> true;
            default -> false;
        };
    }
}
