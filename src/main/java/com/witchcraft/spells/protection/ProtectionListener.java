package com.witchcraft.spells.protection;

import com.witchcraft.Witchcraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listens for protection-related events and handles anti-scrying reflections.
 */
public class ProtectionListener implements Listener {

    private final Witchcraft plugin;

    public ProtectionListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpellCast(com.witchcraft.api.events.WitchSpellCastEvent event) {
        Player caster = event.getPlayer();
        Player target = event.getTarget();

        if (target == null) return;

        AntiScryingProtection antiScrying = getAntiScryingProtection();
        if (antiScrying != null && antiScrying.isProtected(target.getUniqueId())) {
            event.setCancelled(true);

            caster.sendMessage(plugin.getConfigManager().getMessage("anti-scrying.backlash"));
            target.sendMessage(plugin.getConfigManager().getMessage("anti-scrying.protected-player-notified"));

            plugin.getArcaneExhaustion().applyExhaustion(caster.getUniqueId());

            playReflectionEffects(caster.getLocation());
        }
    }

    private AntiScryingProtection getAntiScryingProtection() {
        var spell = plugin.getSpellRegistry().getSpell("anti_scrying_protection");
        if (spell instanceof AntiScryingProtection protection) {
            return protection;
        }
        return null;
    }

    private void playReflectionEffects(org.bukkit.Location center) {
        var world = center.getWorld();
        if (world == null) return;

        world.spawnParticle(org.bukkit.Particle.FLAME,
                center.add(0, 1, 0), 50, 1, 1, 1);
        world.spawnParticle(org.bukkit.Particle.SMOKE,
                center.add(0, 1, 0), 30, 0.5, 0.5, 0.5);
        world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_HURT,
                1.0f, 0.5f);
        world.playSound(center, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                0.5f, 0.5f);
    }
}
