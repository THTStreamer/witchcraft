package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import com.witchcraft.data.CovenData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MassSummonsRitual extends Spell {

    public MassSummonsRitual(Witchcraft plugin) {
        super(plugin, "mass_summons_ritual", "Ritual of Mass Summons",
                SpellCategory.DIVINATION,
                1800, 6, 0.85, 0.1, 0.05,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(caster.getUniqueId());
        if (coven == null) return SpellResult.FAILURE;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int summoned = 0;
                for (Player member : plugin.getCovenManager().getOnlineMembers(coven)) {
                    if (!member.equals(caster) && member.getWorld().equals(caster.getWorld())) {
                        member.teleport(caster.getLocation().clone().add(0, 0.5, 0));
                        member.sendMessage("\u00A75\u00A7lYou have been summoned by the coven!");
                        summoned++;
                    }
                }
                caster.sendMessage("\u00A7a\u00A7l" + summoned + " coven member(s) summoned!");
                playEffects(caster.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.damage(4.0);
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location center) {
        var world = center.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.PORTAL, center.clone().add(0, 1, 0), 100, 2, 2, 2);
        world.spawnParticle(org.bukkit.Particle.REVERSE_PORTAL, center.clone().add(0, 1, 0), 50, 2, 2, 2);
        world.playSound(center, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
    }
}
