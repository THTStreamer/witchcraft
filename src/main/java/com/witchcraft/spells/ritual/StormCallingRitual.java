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

public class StormCallingRitual extends Spell {

    public StormCallingRitual(Witchcraft plugin) {
        super(plugin, "storm_calling_ritual", "Storm Calling",
                SpellCategory.CURSE,
                2400, 7, 0.8, 0.12, 0.08,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                caster.getWorld().setStorm(true);
                caster.getWorld().setThundering(true);
                int radius = 25;
                List<Player> enemies = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream()
                        .filter(p -> p.isOnline() && !p.equals(caster))
                        .toList();
                for (Player enemy : enemies) {
                    enemy.getWorld().strikeLightningEffect(enemy.getLocation());
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                    enemy.damage(6.0);
                    enemy.sendMessage("\u00A75\u00A7cLightning strikes from the sky!");
                }
                caster.sendMessage("\u00A75\u00A7lThe storm answers the coven's call!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.getWorld().strikeLightningEffect(caster.getLocation());
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
        world.spawnParticle(org.bukkit.Particle.END_ROD, center.clone().add(0, 3, 0), 50, 3, 3, 3);
        world.playSound(center, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 0.5f);
    }
}
