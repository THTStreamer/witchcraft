package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;



/**
 * Curse spell that prevents animals from breeding near the target.
 */
public class AnimalBreedingCurse extends Spell {

    public AnimalBreedingCurse(Witchcraft plugin) {
        super(plugin, "animal_breeding_curse", "Curse of Sterility",
                SpellCategory.CURSE,
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

                // Apply breeding cooldown to nearby animals
                Location targetLoc = target.getLocation();
                int radius = 15;

                target.getWorld().getNearbyEntities(targetLoc, radius, radius, radius)
                        .stream()
                        .filter(e -> e instanceof org.bukkit.entity.Animals)
                        .forEach(animal -> {
                            // Set baby age to prevent breeding
                            if (animal instanceof org.bukkit.entity.Ageable ageable) {
                                ageable.setBaby();
                                // Prevent aging for a period
                                animal.setCustomName("\u00A74Sterile");
                                animal.setCustomNameVisible(false);
                            }
                        });
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
}
