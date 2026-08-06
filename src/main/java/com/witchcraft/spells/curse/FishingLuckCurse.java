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
 * Curse spell that reduces fishing luck for the target.
 */
public class FishingLuckCurse extends Spell {

    public FishingLuckCurse(Witchcraft plugin) {
        super(plugin, "fishing_luck_curse", "Curse of the Empty Net",
                SpellCategory.CURSE,
                List.of(Ingredient.NAUTILUS_SHELL, Ingredient.PRISMARINE_SHARD, Ingredient.COAL),
                "piscis fortuna careat",
                600, 3, 0.8, 0.15, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                // Apply luck reduction (using unluck effect)
                target.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK,
                        6000, 3)); // 5 minutes, level 4
                target.sendMessage(plugin.getConfigManager().getMessage("curse.received"));
                caster.sendMessage(plugin.getConfigManager().getMessage("curse.applied",
                        "%target%", target.getName()));
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK,
                        3000, 2));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {
            }
        }
        return result;
    }
}
