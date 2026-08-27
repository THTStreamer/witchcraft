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

public class SinkingMireCurse extends Spell {

    public SinkingMireCurse(Witchcraft plugin) {
        super(plugin, "sinking_mire_curse", "Curse of the Sinking Mire",
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
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 400, 1));
                    enemy.sendMessage("\u00A75\u00A7cThe mire drags you down...");
                }
                caster.sendMessage("\u00A75\u00A7lSinking mire claims " + enemies.size() + " enemies!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
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
        world.spawnParticle(org.bukkit.Particle.BLOCK, center.clone().add(0, 0.5, 0), 60, 2, 0.5, 2,
                org.bukkit.Material.MUD.createBlockData());
        world.playSound(center, org.bukkit.Sound.BLOCK_MUD_BREAK, 1.0f, 0.5f);
    }
}
