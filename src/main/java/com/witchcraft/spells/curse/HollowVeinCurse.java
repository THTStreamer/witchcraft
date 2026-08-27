package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HollowVeinCurse extends Spell {

    public HollowVeinCurse(Witchcraft plugin) {
        super(plugin, "hollow_vein_curse", "Curse of the Hollow Vein",
                SpellCategory.CURSE,
                1400, 6, 0.8, 0.15, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 400, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 0));
                target.sendMessage("\u00A75\u00A7cYour blood runs cold and hollow...");
                caster.sendMessage("\u00A7aThe Hollow Vein drains " + target.getName());
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.DAMAGE_INDICATOR, center.clone().add(0, 1, 0), 20, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.ENTITY_GENERIC_HURT, 1.0f, 0.5f);
    }
}
