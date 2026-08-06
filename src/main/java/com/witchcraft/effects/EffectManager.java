package com.witchcraft.effects;

import com.witchcraft.Witchcraft;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Manages visual and sound effects for spells.
 */
public class EffectManager {

    private final Witchcraft plugin;

    public EffectManager(Witchcraft plugin) {
        this.plugin = plugin;
    }

    /**
     * Plays success effects at a location.
     *
     * @param location the location
     */
    public void playSuccessEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        int density = (int) (30 * plugin.getConfigManager().getConfig()
                .getDouble("effects.particle-density", 1.0));

        world.spawnParticle(Particle.TOTEM_OF_UNDYING, location, density * 2, 1, 1, 1);
        world.spawnParticle(Particle.ENCHANT, location, density, 0.5, 0.5, 0.5);
        world.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        world.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.5f);
    }

    /**
     * Plays failure effects at a location.
     *
     * @param location the location
     */
    public void playFailureEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        int density = (int) (20 * plugin.getConfigManager().getConfig()
                .getDouble("effects.particle-density", 1.0));

        world.spawnParticle(Particle.SMOKE, location, density, 0.5, 0.5, 0.5);
        world.spawnParticle(Particle.LARGE_SMOKE, location, density / 2, 0.3, 0.3, 0.3);
        world.playSound(location, Sound.ENTITY_VILLAGER_NO, 1.0f, 0.5f);
        world.playSound(location, Sound.BLOCK_LAVA_POP, 0.5f, 0.5f);
    }

    /**
     * Plays backfire effects at a location.
     *
     * @param location the location
     */
    public void playBackfireEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        int density = (int) (40 * plugin.getConfigManager().getConfig()
                .getDouble("effects.particle-density", 1.0));

        world.spawnParticle(Particle.FLAME, location, density * 2, 0.5, 0.5, 0.5);
        world.spawnParticle(Particle.SMOKE, location, density, 0.5, 0.5, 0.5);
        world.playSound(location, Sound.ENTITY_WITHER_HURT, 1.0f, 0.5f);
        world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 0.5f);
    }

    /**
     * Plays protection effects at a location.
     *
     * @param location the location
     */
    public void playProtectionEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        int density = (int) (40 * plugin.getConfigManager().getConfig()
                .getDouble("effects.particle-density", 1.0));

        world.spawnParticle(Particle.ENCHANT, location, density, 1, 1, 1);
        world.spawnParticle(Particle.TOTEM_OF_UNDYING, location, density / 2, 0.5, 0.5, 0.5);
        world.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);
    }

    /**
     * Plays scrying effects at a location.
     *
     * @param location the location
     */
    public void playScryingEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        int density = (int) (30 * plugin.getConfigManager().getConfig()
                .getDouble("effects.particle-density", 1.0));

        world.spawnParticle(Particle.ENCHANT, location, density, 0.5, 0.5, 0.5);
        world.spawnParticle(Particle.REVERSE_PORTAL, location, density / 2, 0.3, 0.3, 0.3);
        world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.5f);
    }

    /**
     * Plays ward activation effects.
     *
     * @param location the location
     */
    public void playWardEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        int density = (int) (50 * plugin.getConfigManager().getConfig()
                .getDouble("effects.particle-density", 1.0));

        world.spawnParticle(Particle.ENCHANT, location, density, 1, 1, 1);
        world.spawnParticle(Particle.EFFECT, location, density / 2, 0.5, 0.5, 0.5);
        world.playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 0.5f, 1.2f);
    }

    /**
     * Plays ambient ritual effects.
     *
     * @param player the player
     */
    public void playAmbientEffects(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();

        // Subtle soul particles
        world.spawnParticle(Particle.SOUL, location.add(0, 1, 0),
                5, 0.3, 0.3, 0.3);
    }
}
