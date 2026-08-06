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

public class SharedPowerRitual extends Spell {

    public SharedPowerRitual(Witchcraft plugin) {
        super(plugin, "shared_power_ritual", "Ritual of Shared Power",
                SpellCategory.FERTILITY,
                1800, 6, 0.9, 0.08, 0.02,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 15;
                List<Player> nearby = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream().filter(p -> p.isOnline()).toList();
                for (Player player : nearby) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 6000, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6000, 0));
                    player.sendMessage("\u00A75\u00A7lThe power of the coven flows through you!");
                }
                playEffects(caster.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.damage(6.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING, center.clone().add(0, 1, 0), 80, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
    }
}
