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

public class EternalDamnationCovenSpell extends Spell {

    public EternalDamnationCovenSpell(Witchcraft plugin) {
        super(plugin, "eternal_damnation_coven_spell", "Eternal Damnation",
                SpellCategory.CURSE,
                9000, 20, 0.6, 0.22, 0.18,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                var world = caster.getWorld();
                world.setStorm(true);
                world.setThundering(true);

                int radius = 45;
                List<Player> enemies;
                if (target != null) {
                    enemies = List.of(target);
                } else {
                    enemies = world.getNearbyPlayers(caster.getLocation(), radius)
                            .stream()
                            .filter(p -> p.isOnline() && !p.equals(caster))
                            .toList();
                }
                for (Player enemy : enemies) {
                    // The ultimate punishment - every negative effect
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1600, 5));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1600, 5));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1600, 5));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1600, 5));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1600, 5));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 800, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 1600, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 1600, 5));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1600, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1));
                    enemy.damage(30.0);
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation());
                    enemy.sendMessage("\u00A74\u00A7l\u00A7k!!!!!!!!\u00A7r \u00A74\u00A7lYOU ARE ETERNALLY DAMNED! \u00A7k!!!!!!!!");
                }
                // Supreme buffs for the coven
                caster.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 1600, 3));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1600, 2));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1600, 3));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1600, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1600, 1));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1600, 3));

                caster.sendMessage("\u00A74\u00A7l\u00A7k!!!!!!!!\u00A7r \u00A74\u00A7lETERNAL DAMNATION IS UPON THEM \u00A7k!!!!!!!!");
                caster.sendMessage("\u00A74\u00A7l" + enemies.size() + " souls damned for eternity!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 800, 5));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 800, 5));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 3));
                caster.damage(30.0);
                caster.getWorld().strikeLightningEffect(caster.getLocation());
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.EXPLOSION_EMITTER, center.clone().add(0, 3, 0), 100, 8, 5, 8);
        world.spawnParticle(org.bukkit.Particle.DRAGON_BREATH, center.clone().add(0, 4, 0), 400, 8, 5, 8);
        world.spawnParticle(org.bukkit.Particle.FLAME, center.clone().add(0, 2, 0), 500, 8, 5, 8);
        world.spawnParticle(org.bukkit.Particle.SOUL_FIRE_FLAME, center.clone().add(0, 5, 0), 300, 8, 5, 8);
        world.spawnParticle(org.bukkit.Particle.SOUL, center.clone().add(0, 3, 0), 200, 6, 4, 6);
        world.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, center.clone().add(0, 1, 0), 400, 8, 5, 8);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_DEATH, 4.0f, 0.2f);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 4.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 4.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 4.0f, 0.3f);
    }
}
