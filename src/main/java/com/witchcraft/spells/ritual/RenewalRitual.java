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

public class RenewalRitual extends Spell {

    public RenewalRitual(Witchcraft plugin) {
        super(plugin, "renewal_ritual", "Ritual of Renewal",
                SpellCategory.FERTILITY,
                1200, 4, 0.9, 0.08, 0.02,
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
                    player.setHealth(player.getMaxHealth());
                    player.setFoodLevel(20);
                    player.setSaturation(20f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 1));
                    player.getActivePotionEffects().forEach(e ->
                            player.removePotionEffect(e.getType()));
                    player.sendMessage("\u00A7a\u00A7lYou feel completely renewed!");
                }
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.setHealth(Math.max(1, caster.getHealth() - 6));
                caster.setFoodLevel(Math.max(0, caster.getFoodLevel() - 10));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING, center.clone().add(0, 1, 0), 100, 3, 3, 3);
        world.spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, center.clone().add(0, 1, 0), 50, 3, 3, 3);
        world.playSound(center, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }
}
