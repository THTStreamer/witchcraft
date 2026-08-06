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

public class MassTransmutationCovenSpell extends Spell {

    public MassTransmutationCovenSpell(Witchcraft plugin) {
        super(plugin, "mass_transmutation_coven_spell", "Mass Transmutation",
                SpellCategory.CURSE,
                4800, 10, 0.75, 0.15, 0.1,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 30;
                List<Player> enemies = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream()
                        .filter(p -> p.isOnline() && !p.equals(caster))
                        .toList();
                for (Player enemy : enemies) {
                    // Strip all their buffs and give them debuffs
                    enemy.getActivePotionEffects().forEach(effect -> {
                        if (effect.getType().equals(PotionEffectType.REGENERATION) ||
                            effect.getType().equals(PotionEffectType.STRENGTH) ||
                            effect.getType().equals(PotionEffectType.RESISTANCE) ||
                            effect.getType().equals(PotionEffectType.FIRE_RESISTANCE) ||
                            effect.getType().equals(PotionEffectType.SPEED) ||
                            effect.getType().equals(PotionEffectType.HEALTH_BOOST) ||
                            effect.getType().equals(PotionEffectType.ABSORPTION)) {
                            enemy.removePotionEffect(effect.getType());
                        }
                    });
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 800, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 800, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 800, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 1));
                    enemy.damage(8.0);
                    enemy.sendMessage("\u00A75\u00A7cYour body and soul are transmuted against your will!");
                }
                caster.sendMessage("\u00A75\u00A7l\u00A7k!!\u00A7r \u00A75\u00A7lMass Transmutation strips " + enemies.size() + " of their power! \u00A7k!!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.getActivePotionEffects().forEach(effect ->
                        caster.removePotionEffect(effect.getType()));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 2));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 1));
                caster.damage(10.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.WITCH, center.clone().add(0, 2, 0), 200, 5, 3, 5);
        world.spawnParticle(org.bukkit.Particle.ENCHANT, center.clone().add(0, 1, 0), 150, 4, 2, 4);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITCH_AMBIENT, 2.0f, 0.5f);
        world.playSound(center, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2.0f, 0.5f);
    }
}
