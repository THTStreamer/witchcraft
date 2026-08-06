package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class BindingRitual extends Spell {

    public BindingRitual(Witchcraft plugin) {
        super(plugin, "binding_ritual", "Chains of the Bound Soul",
                SpellCategory.CURSE,
                List.of(Ingredient.IRON_INGOT, Ingredient.ECHO_SHARD, Ingredient.COAL),
                "catena anima ligare",
                600, 4, 0.85, 0.1, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                target.setWalkSpeed(0.15f);
                target.sendMessage(plugin.getConfigManager().getMessage("curse.received"));
                caster.sendMessage(plugin.getConfigManager().getMessage("curse.applied",
                        "%target%", target.getName()));
                caster.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (target.isOnline()) target.setWalkSpeed(0.2f);
                }, 6000L);
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.setWalkSpeed(0.15f);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                caster.getServer().getScheduler().runTaskLater(plugin, () -> {
                    caster.setWalkSpeed(0.2f);
                }, 3000L);
            }
            default -> {}
        }
        return result;
    }
}
