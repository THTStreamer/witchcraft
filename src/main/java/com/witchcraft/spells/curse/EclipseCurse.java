package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EclipseCurse extends Spell {

    public EclipseCurse(Witchcraft plugin) {
        super(plugin, "eclipse_curse", "Curse of the Eclipse",
                SpellCategory.CURSE,
                1400, 6, 0.85, 0.1, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 600, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 1));
                target.sendMessage("\u00A75\u00A7cThe sun is blotted from your sky...");
                caster.sendMessage("\u00A7aEclipse bestowed upon " + target.getName());
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 300, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 1, 0), 50, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 1.0f, 0.5f);
    }
}
