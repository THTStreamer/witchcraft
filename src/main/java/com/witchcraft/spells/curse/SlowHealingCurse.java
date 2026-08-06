package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Curse spell that applies Slow Healing to a target.
 */
public class SlowHealingCurse extends Spell {

    public SlowHealingCurse(Witchcraft plugin) {
        super(plugin, "slow_healing_curse", "Curse of the Withering",
                SpellCategory.CURSE,
                List.of(Ingredient.BONE_MEAL, Ingredient.GHAST_TEAR, Ingredient.COAL),
                "vita sanatio lento",
                600, 3, 0.8, 0.15, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                // Apply healing reduction effect
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,
                        6000, 1)); // 5 minutes, wither effect
                target.sendMessage(plugin.getConfigManager().getMessage("curse.received"));
                caster.sendMessage(plugin.getConfigManager().getMessage("curse.applied",
                        "%target%", target.getName()));
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,
                        3000, 1));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {
            }
        }
        return result;
    }
}
