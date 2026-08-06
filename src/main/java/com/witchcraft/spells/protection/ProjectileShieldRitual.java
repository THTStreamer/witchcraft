package com.witchcraft.spells.protection;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectileShieldRitual extends Spell {

    private final Map<UUID, Long> shieldedPlayers = new ConcurrentHashMap<>();

    public ProjectileShieldRitual(Witchcraft plugin) {
        super(plugin, "projectile_shield_ritual", "Aegis of Deflection",
                SpellCategory.PROTECTION,
                List.of(Ingredient.IRON_INGOT, Ingredient.OBSIDIAN, Ingredient.FLINT),
                "aegis deflexio projicere",
                1800, 5, 0.9, 0.08, 0.02,
                "witchcraft.protection");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        Player shieldTarget = target != null ? target : caster;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                long duration = 36000L;
                shieldedPlayers.put(shieldTarget.getUniqueId(),
                        System.currentTimeMillis() + (duration * 50));
                shieldTarget.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,
                        (int) duration, 1));
                shieldTarget.sendMessage(plugin.getConfigManager().getMessage("protection.applied"));
                if (target != null) {
                    caster.sendMessage(plugin.getConfigManager().getMessage("protection.applied-to-other",
                            "%target%", target.getName()));
                }
                playEffects(shieldTarget.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                shieldedPlayers.remove(caster.getUniqueId());
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    public boolean isShielded(UUID playerId) {
        Long expiry = shieldedPlayers.get(playerId);
        if (expiry == null) return false;
        if (System.currentTimeMillis() >= expiry) {
            shieldedPlayers.remove(playerId);
            return false;
        }
        return true;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.ENCHANT, center.clone().add(0, 1, 0), 40, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);
    }
}
