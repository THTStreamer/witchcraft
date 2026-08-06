package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class LichKingRitual extends Spell {

    public LichKingRitual(Witchcraft plugin) {
        super(plugin, "lich_king_ritual", "Ritual of the Lich King",
                SpellCategory.CURSE,
                4800, 12, 0.7, 0.18, 0.12,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 35;
                var world = caster.getWorld();

                // Summon undead minions
                for (int i = 0; i < 8; i++) {
                    double angle = (2 * Math.PI / 8) * i;
                    Location spawnLoc = caster.getLocation().clone().add(
                            Math.cos(angle) * 5, 0, Math.sin(angle) * 5);
                    int roll = java.util.concurrent.ThreadLocalRandom.current().nextInt(3);
                    switch (roll) {
                        case 0 -> world.spawn(spawnLoc, Zombie.class);
                        case 1 -> world.spawn(spawnLoc, Skeleton.class);
                        case 2 -> world.spawn(spawnLoc, WitherSkeleton.class);
                    }
                }

                // Afflict all enemies
                List<Player> enemies = world.getNearbyPlayers(caster.getLocation(), radius)
                        .stream()
                        .filter(p -> p.isOnline() && !p.equals(caster))
                        .toList();
                for (Player enemy : enemies) {
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 800, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 800, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 800, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 800, 2));
                    enemy.damage(8.0);
                    enemy.sendMessage("\u00A75\u00A7cThe Lich King's army rises around you!");
                }
                caster.sendMessage("\u00A75\u00A7l\u00A7k!!\u00A7r \u00A75\u00A7lThe Lich King commands his undead horde! \u00A7k!!");
                caster.sendMessage("\u00A75\u00A7l" + enemies.size() + " souls claimed, 8 undead summoned!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 3));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
                caster.damage(14.0);
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
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 1, 0), 200, 5, 3, 5);
        world.spawnParticle(org.bukkit.Particle.ENCHANT, center.clone().add(0, 3, 0), 150, 5, 3, 5);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 4.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 0.5f);
        world.playSound(center, org.bukkit.Sound.ENTITY_ZOMBIE_AMBIENT, 3.0f, 0.5f);
    }
}
