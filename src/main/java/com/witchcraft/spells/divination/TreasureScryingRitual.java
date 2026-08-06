package com.witchcraft.spells.divination;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class TreasureScryingRitual extends Spell {

    public TreasureScryingRitual(Witchcraft plugin) {
        super(plugin, "treasure_scrying_ritual", "Dowsing of the Hidden Way",
                SpellCategory.DIVINATION,
                List.of(Ingredient.ECHO_SHARD, Ingredient.GOLD_INGOT, Ingredient.AMETHYST_SHARD),
                "thesaurus videre via abscondita",
                2400, 7, 0.85, 0.1, 0.05,
                "witchcraft.scrying");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                revealNearbyStructures(caster);
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                caster.sendMessage("\u00A7cYou sense nothing but emptiness...");
            }
            default -> {}
        }
        return result;
    }

    private void revealNearbyStructures(Player caster) {
        Location loc = caster.getLocation();
        caster.sendMessage("\u00A75\u00A7l--- Dowsing Results ---");

        // Approximate nearby structure hints based on biome
        String biome = String.valueOf(loc.getBlock().getBiome());
        caster.sendMessage("\u00A77Current biome: \u00A7f" + biome.replace("_", " ").toLowerCase());

        int chunkX = loc.getBlockX() >> 4;
        int chunkZ = loc.getBlockZ() >> 4;
        // Use seed-based hinting for deterministic but vague directions
        long seed = (long) chunkX * 73856093L ^ (long) chunkZ * 19349663L;
        double angle = (seed % 360) * Math.PI / 180.0;
        int dist = 200 + (int) Math.abs(seed % 800);
        int hintX = (int) (Math.cos(angle) * dist);
        int hintZ = (int) (Math.sin(angle) * dist);

        caster.sendMessage("\u00A77Whispers speak of something \u00A7f" + dist + "\u00A77 blocks away...");
        caster.sendMessage("\u00A77Approximate direction: \u00A7f" + hintX + ", " + hintZ);
        caster.sendMessage("\u00A77The spirits are vague... explore and discover.");
        caster.sendMessage("\u00A75\u00A7l--- End Dowsing ---");

        playEffects(loc);
    }

    private void playEffects(Location loc) {
        var world = loc.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.ENCHANT, loc.clone().add(0, 1, 0), 50, 1, 1, 1);
        world.playSound(loc, org.bukkit.Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.5f);
    }
}
