package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



/**
 * Cleansing ritual that removes negative effects from a player.
 */
public class CleansingRitual extends Spell {

    public CleansingRitual(Witchcraft plugin) {
        super(plugin, "cleansing_ritual", "Rite of Purification",
                SpellCategory.CLEANSING,
                900, 4, 0.9, 0.08, 0.02,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        Player cleanseTarget = target != null ? target : caster;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                // Remove all negative effects
                for (PotionEffect effect : cleanseTarget.getActivePotionEffects()) {
                    if (isNegativeEffect(effect.getType())) {
                        cleanseTarget.removePotionEffect(effect.getType());
                    }
                }

                // Clear exhaustion if target
                plugin.getArcaneExhaustion().removeExhaustion(cleanseTarget.getUniqueId());

                cleanseTarget.sendMessage(plugin.getConfigManager().getMessage("fertility.success"));
                if (target != null) {
                    caster.sendMessage(plugin.getConfigManager().getMessage("fertility.success"));
                }

                // Play effects
                playCleansingEffects(cleanseTarget.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                // Apply all negative effects to caster
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 200, 1));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {
            }
        }
        return result;
    }

    private boolean isNegativeEffect(PotionEffectType type) {
        return type.equals(PotionEffectType.WITHER) ||
                type.equals(PotionEffectType.WEAKNESS) ||
                type.equals(PotionEffectType.MINING_FATIGUE) ||
                type.equals(PotionEffectType.UNLUCK) ||
                type.equals(PotionEffectType.BLINDNESS) ||
                type.equals(PotionEffectType.POISON) ||
                type.equals(PotionEffectType.NAUSEA) ||
                type.equals(PotionEffectType.SLOWNESS) ||
                type.equals(PotionEffectType.HUNGER);
    }

    private void playCleansingEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;

        world.spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING,
                center.add(0, 1, 0), 50, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                1.0f, 1.5f);
    }
}
