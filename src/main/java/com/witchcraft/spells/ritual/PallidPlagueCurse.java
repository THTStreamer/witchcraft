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

public class PallidPlagueCurse extends Spell {

    public PallidPlagueCurse(Witchcraft plugin) {
        super(plugin, "pallid_plague_curse", "Curse of the Pallid Plague",
                SpellCategory.CURSE,
                2800, 9, 0.8, 0.12, 0.08,
                "witchcraft.curse");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 20;
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
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 1));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 0));
                    enemy.sendMessage("\u00A75\u00A7cPallid plague festers within you...");
                }
                caster.sendMessage("\u00A75\u00A7lPallid plague spreads to " + enemies.size() + " enemies!");
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
        world.playSound(center, org.bukkit.Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0f, 0.7f);
    }
}
