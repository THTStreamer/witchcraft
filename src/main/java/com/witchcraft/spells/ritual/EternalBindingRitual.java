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

public class EternalBindingRitual extends Spell {

    public EternalBindingRitual(Witchcraft plugin) {
        super(plugin, "eternal_binding_ritual", "Ritual of the Eternal Binding",
                SpellCategory.CURSE,
                3600, 10, 0.75, 0.15, 0.1,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 25;
                List<Player> enemies = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream()
                        .filter(p -> p.isOnline() && !p.equals(caster))
                        .toList();
                for (Player enemy : enemies) {
                    // Complete immobilization
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 800, 255));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 800, 255));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 800, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 800, 3));
                    enemy.damage(5.0);
                    enemy.sendMessage("\u00A75\u00A7cChains of ancient magic bind you in place!");
                }
                caster.sendMessage("\u00A75\u00A7l\u00A7k!!\u00A7r \u00A75\u00A7lThe Eternal Binding traps " + enemies.size() + " souls! \u00A7k!!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 255));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 255));
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
        world.spawnParticle(org.bukkit.Particle.ENCHANT, center.clone().add(0, 1, 0), 200, 5, 2, 5);
        world.spawnParticle(org.bukkit.Particle.PORTAL, center.clone().add(0, 2, 0), 150, 4, 2, 4);
        world.playSound(center, org.bukkit.Sound.BLOCK_ANVIL_LAND, 2.0f, 0.3f);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f, 0.5f);
    }
}
