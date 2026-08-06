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

public class CovenEyeRitual extends Spell {

    public CovenEyeRitual(Witchcraft plugin) {
        super(plugin, "coven_eye_ritual", "The Coven's Eye",
                SpellCategory.DIVINATION,
                1200, 4, 0.9, 0.08, 0.02,
                "witchcraft.scrying");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 50;
                List<Player> nearby = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream().filter(Player::isOnline).toList();
                for (Player player : nearby) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0));
                }
                caster.sendMessage("\u00A75\u00A7l" + nearby.size() +
                        " players within " + radius + " blocks are now glowing!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.PORTAL, center.clone().add(0, 2, 0), 30, 3, 3, 3);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.5f);
    }
}
