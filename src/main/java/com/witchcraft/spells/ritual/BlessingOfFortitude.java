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

public class BlessingOfFortitude extends Spell {

    public BlessingOfFortitude(Witchcraft plugin) {
        super(plugin, "blessing_of_fortitude", "Blessing of Fortitude",
                SpellCategory.PROTECTION,
                1800, 5, 0.9, 0.08, 0.02,
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
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 6000, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6000, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 6000, 0));
                    player.sendMessage("\u00A7a\u00A7lFortitude courses through your veins!");
                }
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 1));
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
        world.spawnParticle(org.bukkit.Particle.ENCHANT, center.clone().add(0, 1, 0), 40, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.2f);
    }
}
