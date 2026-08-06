package com.witchcraft.spells.divination;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;



public class AuraSightRitual extends Spell {

    public AuraSightRitual(Witchcraft plugin) {
        super(plugin, "aura_sight_ritual", "The Witch's Third Eye",
                SpellCategory.DIVINATION,
                2400, 7, 0.85, 0.1, 0.05,
                "witchcraft.scrying", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                revealAura(caster, target);
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                caster.sendMessage("\u00A7cThe auras blur into confusion...");
            }
            default -> {}
        }
        return result;
    }

    private void revealAura(Player caster, Player target) {
        caster.sendMessage("\u00A75\u00A7l--- Aura Reading ---");
        caster.sendMessage("\u00A77Target: \u00A7f" + target.getName());

        // Health aura
        double healthPercent = (target.getHealth() / target.getMaxHealth()) * 100;
        String healthColor = healthPercent > 75 ? "\u00A7a" : healthPercent > 40 ? "\u00A7e" : "\u00A7c";
        caster.sendMessage("\u00A77Life Force: " + healthColor + String.format("%.0f", healthPercent) + "%");

        // Active effects
        var effects = target.getActivePotionEffects();
        if (effects.isEmpty()) {
            caster.sendMessage("\u00A77Active Aura: \u00A77None");
        } else {
            caster.sendMessage("\u00A77Active Auras: \u00A7f" + effects.size());
            for (PotionEffect effect : effects) {
                caster.sendMessage("  \u00A77- " + effect.getType().getName() +
                        " (lvl " + (effect.getAmplifier() + 1) + ")");
            }
        }

        // Hunger
        int food = target.getFoodLevel();
        String foodColor = food > 14 ? "\u00A7a" : food > 6 ? "\u00A7e" : "\u00A7c";
        caster.sendMessage("\u00A77Satiety: " + foodColor + food + "/20");

        // Experience
        caster.sendMessage("\u00A77Experience: \u00A7f" + target.getLevel() + " levels");

        caster.sendMessage("\u00A75\u00A7l--- End Reading ---");

        playEffects(target.getLocation());
    }

    private void playEffects(Location loc) {
        var world = loc.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.ENCHANT, loc.clone().add(0, 1, 0), 50, 1, 1, 1);
        world.spawnParticle(org.bukkit.Particle.EFFECT, loc.clone().add(0, 1, 0), 30, 0.5, 0.5, 0.5);
        world.playSound(loc, org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.5f, 1.5f);
    }
}
