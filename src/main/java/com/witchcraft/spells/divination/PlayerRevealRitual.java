package com.witchcraft.spells.divination;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerRevealRitual extends Spell {

    public PlayerRevealRitual(Witchcraft plugin) {
        super(plugin, "player_reveal_ritual", "Eyes of the Coven",
                SpellCategory.DIVINATION,
                1200, 4, 0.9, 0.08, 0.02,
                "witchcraft.scrying");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int radius = 100;
                List<Player> nearby = caster.getWorld().getNearbyPlayers(caster.getLocation(), radius)
                        .stream().filter(Player::isOnline).toList();
                caster.sendMessage("\u00A75\u00A7l--- Players Nearby ---");
                caster.sendMessage("\u00A77Players within " + radius + " blocks:");
                for (Player player : nearby) {
                    int dist = (int) player.getLocation().distance(caster.getLocation());
                    String dir = getDirection(caster.getLocation(), player.getLocation());
                    caster.sendMessage("\u00A77- \u00A7f" + player.getName() +
                            " \u00A77(\u00A7e" + dist + "m " + dir + "\u00A77)");
                }
                caster.sendMessage("\u00A75\u00A7l--- End Reveal ---");
                playEffects(caster.getLocation());
            }
            case FAILURE -> caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            case BACKFIRE -> {
                caster.sendMessage("\u00A7cYour senses are overwhelmed...");
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private String getDirection(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double angle = Math.toDegrees(Math.atan2(dz, dx));
        if (angle < 0) angle += 360;
        if (angle >= 337.5 || angle < 22.5) return "E";
        if (angle < 67.5) return "SE";
        if (angle < 112.5) return "S";
        if (angle < 157.5) return "SW";
        if (angle < 202.5) return "W";
        if (angle < 247.5) return "NW";
        if (angle < 292.5) return "N";
        return "NE";
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.PORTAL, center.clone().add(0, 2, 0), 20, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, 0.5f, 1.5f);
    }
}
