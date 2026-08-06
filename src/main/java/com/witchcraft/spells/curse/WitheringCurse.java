package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WitheringCurse extends Spell {

    public WitheringCurse(Witchcraft plugin) {
        super(plugin, "withering_curse", "Curse of Withering",
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
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 1));
                target.sendMessage("\u00A75\u00A7cYour body begins to wither...");
                caster.sendMessage("\u00A7aThe Withering has been bestowed upon " + target.getName());
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 1, 0), 40, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 0.5f, 0.8f);
    }
}
