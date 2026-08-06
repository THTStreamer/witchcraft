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

public class BlindnessCurse extends Spell {

    public BlindnessCurse(Witchcraft plugin) {
        super(plugin, "blindness_curse", "Curse of the Shrouded Eye",
                SpellCategory.CURSE,
                List.of(Ingredient.FERMENTED_SPIDER_EYE, Ingredient.COAL, Ingredient.QUARTZ),
                "oculus tenebris velare",
                600, 3, 0.8, 0.15, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                        6000, 1));
                target.sendMessage(plugin.getConfigManager().getMessage("curse.received"));
                caster.sendMessage(plugin.getConfigManager().getMessage("curse.applied",
                        "%target%", target.getName()));
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3000, 1));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }
}
