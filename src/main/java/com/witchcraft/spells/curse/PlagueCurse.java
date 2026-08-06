package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlagueCurse extends Spell {

    public PlagueCurse(Witchcraft plugin) {
        super(plugin, "plague_curse", "Curse of the Plague",
                SpellCategory.CURSE,
                1200, 5, 0.85, 0.1, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 0));
                target.sendMessage("\u00A75\u00A7cA sickly pallor washes over you...");
                caster.sendMessage("\u00A7aThe Plague has been bestowed upon " + target.getName());
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 400, 1));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.ITEM_SLIME, center.clone().add(0, 1, 0), 30, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.ENTITY_SPIDER_AMBIENT, 0.5f, 0.5f);
    }
}
