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

public class ArmageddonCovenSpell extends Spell {

    public ArmageddonCovenSpell(Witchcraft plugin) {
        super(plugin, "armageddon_coven_spell", "Armageddon",
                SpellCategory.CURSE,
                6000, 12, 0.72, 0.18, 0.1,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                var world = caster.getWorld();
                int radius = 35;
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
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation());
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation().clone().add(2, 0, 0));
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation().clone().add(-2, 0, 0));
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation().clone().add(0, 0, 2));
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation().clone().add(0, 0, -2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 800, 3));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 2));
                    enemy.damage(25.0);
                    enemy.sendMessage("\u00A74\u00A7l\u00A7k!!\u00A7r \u00A74\u00A7lArmageddon rains fire upon you! \u00A7k!!");
                }
                caster.sendMessage("\u00A74\u00A7l\u00A7k!!!!\u00A7r \u00A74\u00A7lARMAGEDDON UNLEASHED \u00A7k!!!!");
                caster.sendMessage("\u00A74\u00A7l" + enemies.size() + " souls smitten by divine fire!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.getWorld().strikeLightningEffect(caster.getLocation());
                caster.getWorld().strikeLightningEffect(caster.getLocation().clone().add(3, 0, 0));
                caster.getWorld().strikeLightningEffect(caster.getLocation().clone().add(-3, 0, 0));
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
        world.spawnParticle(org.bukkit.Particle.FLAME, center.clone().add(0, 2, 0), 400, 6, 4, 6);
        world.spawnParticle(org.bukkit.Particle.LAVA, center.clone().add(0, 1, 0), 100, 4, 2, 4);
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 3, 0), 250, 6, 4, 6);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 4.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 4.0f, 0.5f);
    }
}
