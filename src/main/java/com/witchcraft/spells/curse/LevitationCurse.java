package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevitationCurse extends Spell {

    public LevitationCurse(Witchcraft plugin) {
        super(plugin, "levitation_curse", "Curse of Levitation",
                SpellCategory.CURSE,
                1200, 5, 0.8, 0.15, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 400, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
                target.sendMessage("\u00A75\u00A7cYou feel weightless... lifted against your will!");
                caster.sendMessage("\u00A7aLevitation bestowed upon " + target.getName());
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.CLOUD, center.clone().add(0, 1, 0), 30, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.ENTITY_PHANTOM_FLAP, 1.0f, 0.5f);
    }
}
