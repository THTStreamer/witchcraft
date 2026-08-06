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

public class SoulHarvestCovenSpell extends Spell {

    public SoulHarvestCovenSpell(Witchcraft plugin) {
        super(plugin, "soul_harvest_coven_spell", "Soul Harvest",
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
                List<Player> enemies;
                if (target != null) {
                    enemies = List.of(target);
                } else {
                    enemies = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                            .stream()
                            .filter(p -> p.isOnline() && !p.equals(caster))
                            .toList();
                }
                double totalHealed = 0;
                for (Player enemy : enemies) {
                    double damage = Math.min(enemy.getHealth() * 0.4, 16);
                    enemy.damage(damage);
                    totalHealed += damage;
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 800, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 800, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 2));
                    enemy.sendMessage("\u00A75\u00A7cYour life force is ripped from your body!");
                }
                // Heal caster with stolen life
                double healPerCaster = totalHealed;
                caster.setHealth(Math.min(caster.getMaxHealth(), caster.getHealth() + healPerCaster));
                caster.sendMessage("\u00A75\u00A7l\u00A7k!!\u00A7r \u00A75\u00A7lSoul Harvest drains " + enemies.size() + " souls! \u00A7k!!");
                caster.sendMessage("\u00A75\u00A7lYou absorb " + String.format("%.1f", totalHealed) + " hearts of life force!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.damage(12.0);
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 2));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.SOUL, center.clone().add(0, 2, 0), 200, 5, 3, 5);
        world.spawnParticle(org.bukkit.Particle.SOUL_FIRE_FLAME, center.clone().add(0, 1, 0), 100, 4, 2, 4);
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 3, 0), 150, 5, 3, 5);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_HURT, 2.0f, 0.5f);
        world.playSound(center, org.bukkit.Sound.ENTITY_EVOKER_PREPARE_SUMMON, 2.0f, 0.5f);
    }
}
