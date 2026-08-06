package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SoulDrainRitual extends Spell {

    public SoulDrainRitual(Witchcraft plugin) {
        super(plugin, "soul_drain_ritual", "Soul Drain",
                SpellCategory.CURSE,
                1800, 6, 0.85, 0.1, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                double targetHealth = target.getHealth();
                double healAmount = Math.min(targetHealth * 0.3, 12);
                target.damage(healAmount);
                caster.setHealth(Math.min(caster.getMaxHealth(), caster.getHealth() + healAmount));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 0));
                target.sendMessage("\u00A75\u00A7cYour life force is drained...");
                caster.sendMessage("\u00A7a\u00A7l" + target.getName() + "'s soul empowers you!");
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.damage(6.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.SOUL, center.clone().add(0, 1, 0), 40, 1, 1, 1);
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 1, 0), 20, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_HURT, 0.8f, 0.8f);
    }
}
