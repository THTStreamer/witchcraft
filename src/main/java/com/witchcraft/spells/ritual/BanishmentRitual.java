package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class BanishmentRitual extends Spell {

    public BanishmentRitual(Witchcraft plugin) {
        super(plugin, "banishment_ritual", "Rite of Banishment",
                SpellCategory.CURSE,
                List.of(Ingredient.ENDER_PEARL, Ingredient.ECHO_SHARD, Ingredient.QUARTZ),
                "exilium proiectio removere",
                1200, 5, 0.85, 0.1, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                double angle = Math.random() * 2 * Math.PI;
                double distance = 100 + Math.random() * 200;
                double newX = target.getLocation().getX() + Math.cos(angle) * distance;
                double newZ = target.getLocation().getZ() + Math.sin(angle) * distance;
                Location newLoc = new Location(target.getWorld(), newX,
                        target.getWorld().getHighestBlockYAt((int) newX, (int) newZ) + 1, newZ);
                target.teleport(newLoc);
                target.sendMessage(plugin.getConfigManager().getMessage("curse.received"));
                caster.sendMessage(plugin.getConfigManager().getMessage("curse.applied",
                        "%target%", target.getName()));
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                double angle = Math.random() * 2 * Math.PI;
                double distance = 150;
                double newX = caster.getLocation().getX() + Math.cos(angle) * distance;
                double newZ = caster.getLocation().getZ() + Math.sin(angle) * distance;
                Location newLoc = new Location(caster.getWorld(), newX,
                        caster.getWorld().getHighestBlockYAt((int) newX, (int) newZ) + 1, newZ);
                caster.teleport(newLoc);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }
}
