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

public class BindingCircleRitual extends Spell {

    public BindingCircleRitual(Witchcraft plugin) {
        super(plugin, "binding_circle_ritual", "Ritual of the Binding Circle",
                SpellCategory.PROTECTION,
                2400, 7, 0.85, 0.1, 0.05,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 20;
                List<Player> nearby = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream().filter(p -> p.isOnline()).toList();
                for (Player player : nearby) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 6000, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
                    player.sendMessage("\u00A75\u00A7lThe binding circle protects you!");
                }
                playEffects(caster.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 1));
                caster.damage(4.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.ENCHANT, center.clone().add(0, 0.5, 0), 100, 3, 0.5, 3);
        world.spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING, center.clone().add(0, 1, 0), 60, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
    }
}
