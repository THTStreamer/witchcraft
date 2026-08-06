package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SummoningRitual extends Spell {

    public SummoningRitual(Witchcraft plugin) {
        super(plugin, "summoning_ritual", "Calling of the Bound",
                SpellCategory.DIVINATION,
                2400, 6, 0.85, 0.1, 0.05,
                "witchcraft.scrying", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                Location summonTo = caster.getLocation().clone().add(0, 0.5, 0);
                target.teleport(summonTo);
                target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
                target.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-success"));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-success"));
                playEffects(caster.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                caster.damage(4.0);
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.PORTAL, center.clone().add(0, 1, 0), 60, 1, 1, 1);
        world.spawnParticle(org.bukkit.Particle.REVERSE_PORTAL, center.clone().add(0, 1, 0), 30, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        world.playSound(center, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.8f, 1.2f);
    }
}
