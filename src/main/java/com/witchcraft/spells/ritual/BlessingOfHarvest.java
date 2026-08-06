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

public class BlessingOfHarvest extends Spell {

    public BlessingOfHarvest(Witchcraft plugin) {
        super(plugin, "blessing_of_harvest", "Blessing of the Harvest",
                SpellCategory.FERTILITY,
                1200, 4, 0.9, 0.08, 0.02,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 20;
                var world = caster.getWorld();
                int cropsGrown = 0;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        var block = world.getBlockAt(
                                caster.getLocation().getBlockX() + x,
                                caster.getLocation().getBlockY(),
                                caster.getLocation().getBlockZ() + z);
                        if (isCrop(block.getType())) {
                            block.applyBoneMeal(org.bukkit.block.BlockFace.UP);
                            cropsGrown++;
                        }
                    }
                }
                List<Player> nearby = world.getNearbyPlayers(caster.getLocation(), radius)
                        .stream().filter(Player::isOnline).toList();
                for (Player player : nearby) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 0));
                    player.sendMessage("\u00A7a\u00A7lThe harvest blesses you with abundance!");
                }
                caster.sendMessage("\u00A7a\u00A7l" + cropsGrown + cropsGrown + " crops enriched!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                int radius = 5;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        var block = caster.getWorld().getBlockAt(
                                caster.getLocation().getBlockX() + x,
                                caster.getLocation().getBlockY(),
                                caster.getLocation().getBlockZ() + z);
                        if (isCrop(block.getType())) {
                            block.setType(org.bukkit.Material.AIR);
                        }
                    }
                }
            }
            default -> {}
        }
        return result;
    }

    private boolean isCrop(org.bukkit.Material material) {
        return switch (material) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART, PUMPKIN_STEM, MELON_STEM -> true;
            default -> false;
        };
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, center.clone().add(0, 1, 0), 80, 3, 2, 3);
        world.playSound(center, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }
}
