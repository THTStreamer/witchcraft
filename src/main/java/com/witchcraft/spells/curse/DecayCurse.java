package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DecayCurse extends Spell {

    public DecayCurse(Witchcraft plugin) {
        super(plugin, "decay_curse", "Curse of Decay",
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
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 400, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 1));
                target.sendMessage("\u00A75\u00A7cYour flesh begins to rot and decay...");
                caster.sendMessage("\u00A7aDecay bestowed upon " + target.getName());
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
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
        world.playSound(center, org.bukkit.Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 0.5f);
    }
}
