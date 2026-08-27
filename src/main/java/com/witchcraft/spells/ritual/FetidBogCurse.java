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

public class FetidBogCurse extends Spell {

    public FetidBogCurse(Witchcraft plugin) {
        super(plugin, "fetid_bog_curse", "Curse of the Fetid Bog",
                SpellCategory.CURSE,
                2400, 8, 0.8, 0.12, 0.08,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 15;
                List<Player> enemies;
                if (target != null) {
                    enemies = List.of(target);
                } else {
                    var casterCoven = plugin.getCovenManager().getCovenForMember(caster.getUniqueId());
                    enemies = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                            .stream()
                            .filter(p -> !p.equals(caster) && !isSameCoven(p, casterCoven))
                            .toList();
                }
                for (Player enemy : enemies) {
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 400, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 0));
                    enemy.sendMessage("\u00A75\u00A7cFetid bog seeps into your lungs...");
                }
                caster.sendMessage("\u00A75\u00A7lFetid bog engulfs " + enemies.size() + " enemies!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private boolean isSameCoven(Player p, com.witchcraft.data.CovenData casterCoven) {
        if (casterCoven == null) return false;
        var other = plugin.getCovenManager().getCovenForMember(p.getUniqueId());
        return other != null && other.getCovenId().equals(casterCoven.getCovenId());
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.ITEM_SLIME, center.clone().add(0, 1, 0), 50, 2, 1, 2);
        world.spawnParticle(org.bukkit.Particle.SMOKE, center.clone().add(0, 1, 0), 30, 1, 1, 1);
        world.playSound(center, org.bukkit.Sound.BLOCK_SLIME_BLOCK_BREAK, 1.0f, 0.5f);
    }
}
