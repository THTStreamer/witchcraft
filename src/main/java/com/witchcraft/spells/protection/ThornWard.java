package com.witchcraft.spells.protection;

import com.witchcraft.Witchcraft;
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

public class ThornWard extends Spell {

    private final Map<UUID, Long> wardedPlayers = new ConcurrentHashMap<>();

    public ThornWard(Witchcraft plugin) {
        super(plugin, "thorn_ward", "Ward of Thorns",
                SpellCategory.PROTECTION,
                1800, 5, 0.9, 0.08, 0.02,
                "witchcraft.protection");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        Player wardTarget = target != null ? target : caster;
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                long duration = 60000L;
                wardedPlayers.put(wardTarget.getUniqueId(),
                        System.currentTimeMillis() + (duration * 50));
                wardTarget.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, (int) duration, 0));
                wardTarget.sendMessage("\u00A7a\u00A7lThorns surround you! Attackers will be hurt.");
                if (target != null) {
                    caster.sendMessage("\u00A7aThorn Ward placed on " + target.getName());
                }
                playEffects(wardTarget.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                wardedPlayers.remove(caster.getUniqueId());
                caster.damage(4.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    public boolean isWarded(UUID playerId) {
        Long expiry = wardedPlayers.get(playerId);
        if (expiry == null) return false;
        if (System.currentTimeMillis() >= expiry) {
            wardedPlayers.remove(playerId);
            return false;
        }
        return true;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.ENCHANT, center.clone().add(0, 1, 0), 50, 1, 1, 1);
        world.spawnParticle(org.bukkit.Particle.CRIT, center.clone().add(0, 1, 0), 30, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.8f);
    }
}
