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

public class BloodMoonRitual extends Spell {

    public BloodMoonRitual(Witchcraft plugin) {
        super(plugin, "blood_moon_ritual", "Ritual of the Blood Moon",
                SpellCategory.CURSE,
                6000, 15, 0.65, 0.2, 0.15,
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
                world.setTime(18000);

                int radius = 40;
                List<Player> enemies = world.getNearbyPlayers(caster.getLocation(), radius)
                        .stream()
                        .filter(p -> p.isOnline() && !p.equals(caster))
                        .toList();
                for (Player enemy : enemies) {
                    // Every negative effect in the game
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1000, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1000, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1000, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1000, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 600, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 1000, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1000, 2));
                    enemy.damage(15.0);
                    enemy.sendMessage("\u00A74\u00A7l\u00A7k!!\u00A7r \u00A74\u00A7lThe Blood Moon claims your soul! \u00A7k!!");
                }
                caster.sendMessage("\u00A74\u00A7l\u00A7k!!!!!!!!\u00A7r \u00A74\u00A7lTHE BLOOD MOON RISES \u00A7k!!!!!!!!");
                caster.sendMessage("\u00A74\u00A7l" + enemies.size() + " souls harvested under the Blood Moon!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 3));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 3));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1));
                caster.damage(20.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.FLAME, center.clone().add(0, 2, 0), 300, 6, 4, 6);
        world.spawnParticle(org.bukkit.Particle.SOUL_FIRE_FLAME, center.clone().add(0, 3, 0), 200, 6, 4, 6);
        world.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, center.clone().add(0, 1, 0), 250, 6, 4, 6);
        world.spawnParticle(org.bukkit.Particle.DRAGON_BREATH, center.clone().add(0, 4, 0), 150, 5, 3, 5);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 4.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 4.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 4.0f, 0.3f);
    }
}
