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

public class SpiritWalkRitual extends Spell {

    public SpiritWalkRitual(Witchcraft plugin) {
        super(plugin, "spirit_walk_ritual", "Spirit Walk",
                SpellCategory.DIVINATION,
                1200, 4, 0.9, 0.08, 0.02,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 15;
                List<Player> nearby = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream().filter(Player::isOnline).toList();
                for (Player player : nearby) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 6000, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0));
                    player.sendMessage("\u00A75\u00A7lYour spirit transcends the mortal realm!");
                }
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 2));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.SOUL, center.clone().add(0, 1, 0), 60, 2, 2, 2);
        world.spawnParticle(org.bukkit.Particle.REVERSE_PORTAL, center.clone().add(0, 1, 0), 30, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.PARTICLE_SOUL_ESCAPE, 1.0f, 1.2f);
    }
}
