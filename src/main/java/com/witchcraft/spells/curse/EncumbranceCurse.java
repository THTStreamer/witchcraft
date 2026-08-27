package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EncumbranceCurse extends Spell {

    public EncumbranceCurse(Witchcraft plugin) {
        super(plugin, "encumbrance_curse", "Curse of Encumbrance",
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
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 2));
                target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 2));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 1));
                target.sendMessage("\u00A75\u00A7cAn unbearable weight crushes you...");
                caster.sendMessage("\u00A7aEncumbrance bestowed upon " + target.getName());
                playEffects(target.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.CRIT, center.clone().add(0, 1, 0), 30, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.BLOCK_ANVIL_LAND, 0.8f, 0.5f);
    }
}
