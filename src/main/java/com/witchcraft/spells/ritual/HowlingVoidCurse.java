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

public class HowlingVoidCurse extends Spell {

    public HowlingVoidCurse(Witchcraft plugin) {
        super(plugin, "howling_void_curse", "Curse of the Howling Void",
                SpellCategory.CURSE,
                3000, 9, 0.8, 0.12, 0.08,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 25;
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
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 0));
                    enemy.sendMessage("\u00A75\u00A7cThe void howls and tears at your soul!");
                }
                caster.sendMessage("\u00A75\u00A7lHowling void consumes " + enemies.size() + " enemies!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
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
        world.spawnParticle(org.bukkit.Particle.PORTAL, center.clone().add(0, 2, 0), 60, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.ENTITY_WARDEN_ROAR, 1.0f, 0.7f);
    }
}
