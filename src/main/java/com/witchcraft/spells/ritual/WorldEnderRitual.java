package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class WorldEnderRitual extends Spell {

    public WorldEnderRitual(Witchcraft plugin) {
        super(plugin, "world_ender_ritual", "Ritual of the World Ender",
                SpellCategory.CURSE,
                7200, 20, 0.6, 0.22, 0.18,
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
                world.setTime(16000);

                int radius = 50;
                List<Player> enemies = world.getNearbyPlayers(caster.getLocation(), radius)
                        .stream()
                        .filter(p -> p.isOnline() && !p.equals(caster))
                        .toList();
                for (Player enemy : enemies) {
                    // Devastating damage and effects
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1200, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1200, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 1200, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 1200, 4));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1200, 3));

                    // Halve their max health temporarily
                    var healthAttr = enemy.getAttribute(Attribute.MAX_HEALTH);
                    if (healthAttr != null) {
                        healthAttr.setBaseValue(healthAttr.getBaseValue() / 2);
                    }

                    enemy.damage(20.0);
                    enemy.sendMessage("\u00A74\u00A7l\u00A7k!!!!!!!!\u00A7r \u00A74\u00A7lTHE WORLD ENDS AROUND YOU! \u00A7k!!!!!!!!");
                }

                // Buff all coven members
                caster.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 1200, 2));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 1));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1200, 2));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));

                caster.sendMessage("\u00A74\u00A7l\u00A7k!!!!!!!!\u00A7r \u00A74\u00A7lTHE RITUAL OF THE WORLD ENDER IS COMPLETE \u00A7k!!!!!!!!");
                caster.sendMessage("\u00A74\u00A7l" + enemies.size() + " souls erased from existence!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 4));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 4));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 2));
                var healthAttr = caster.getAttribute(Attribute.MAX_HEALTH);
                if (healthAttr != null) {
                    healthAttr.setBaseValue(healthAttr.getBaseValue() / 2);
                }
                caster.damage(25.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.EXPLOSION_EMITTER, center.clone().add(0, 3, 0), 50, 8, 5, 8);
        world.spawnParticle(org.bukkit.Particle.DRAGON_BREATH, center.clone().add(0, 4, 0), 300, 8, 5, 8);
        world.spawnParticle(org.bukkit.Particle.FLAME, center.clone().add(0, 2, 0), 400, 8, 5, 8);
        world.spawnParticle(org.bukkit.Particle.SOUL_FIRE_FLAME, center.clone().add(0, 5, 0), 200, 6, 4, 6);
        world.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, center.clone().add(0, 1, 0), 300, 8, 5, 8);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 4.0f, 0.2f);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_DEATH, 4.0f, 0.5f);
        world.playSound(center, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 4.0f, 0.3f);
    }
}
