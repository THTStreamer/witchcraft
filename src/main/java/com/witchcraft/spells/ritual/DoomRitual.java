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

public class DoomRitual extends Spell {

    public DoomRitual(Witchcraft plugin) {
        super(plugin, "doom_ritual", "Ritual of Doom",
                SpellCategory.CURSE,
                2400, 8, 0.8, 0.12, 0.08,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 20;
                List<Player> enemies = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream()
                        .filter(p -> p.isOnline() && !p.equals(caster))
                        .toList();
                for (Player enemy : enemies) {
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 600, 2));
                    enemy.sendMessage("\u00A75\u00A7cDoom befalls you!");
                }
                caster.sendMessage("\u00A75\u00A7l" + enemies.size() + " souls claimed by doom!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 2));
                caster.damage(8.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 1, 0), 100, 3, 2, 3);
        world.spawnParticle(org.bukkit.Particle.FLAME, center.clone().add(0, 1, 0), 50, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 2.0f, 0.5f);
    }
}
