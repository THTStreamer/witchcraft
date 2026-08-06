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

public class EternalStormRitual extends Spell {

    public EternalStormRitual(Witchcraft plugin) {
        super(plugin, "eternal_storm_ritual", "Ritual of the Eternal Storm",
                SpellCategory.CURSE,
                3600, 10, 0.75, 0.15, 0.1,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                caster.getWorld().setStorm(true);
                caster.getWorld().setThundering(true);
                caster.getWorld().setTime(13000);

                int radius = 30;
                List<Player> enemies;
                if (target != null) {
                    enemies = List.of(target);
                } else {
                    enemies = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                            .stream()
                            .filter(p -> p.isOnline() && !p.equals(caster))
                            .toList();
                }
                for (Player enemy : enemies) {
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation());
                    enemy.damage(10.0);
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
                    enemy.sendMessage("\u00A75\u00A7cThe eternal storm rages around you!");
                }
                caster.sendMessage("\u00A75\u00A7l\u00A7k!!\u00A7r \u00A75\u00A7lThe Eternal Storm awakens! \u00A7k!!");
                caster.sendMessage("\u00A75\u00A7l" + enemies.size() + " souls ravaged by the storm!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.getWorld().strikeLightningEffect(caster.getLocation());
                caster.damage(12.0);
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 2));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 1));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.END_ROD, center.clone().add(0, 3, 0), 200, 5, 3, 5);
        world.spawnParticle(org.bukkit.Particle.SOUL_FIRE_FLAME, center.clone().add(0, 2, 0), 100, 4, 2, 4);
        world.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, center.clone().add(0, 1, 0), 150, 5, 3, 5);
        world.playSound(center, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 4.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 0.5f);
    }
}
