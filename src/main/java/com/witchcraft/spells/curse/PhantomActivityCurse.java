package com.witchcraft.spells.curse;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;



/**
 * Curse spell that increases phantom activity around a target.
 */
public class PhantomActivityCurse extends Spell {

    public PhantomActivityCurse(Witchcraft plugin) {
        super(plugin, "phantom_activity_curse", "Curse of the Night Watch",
                SpellCategory.CURSE,
                600, 3, 0.8, 0.15, 0.05,
                "witchcraft.curse", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                // Reset sleep timer and give fatigue to prevent sleeping
                target.setPlayerListName("\u00A74" + target.getName());
                target.sendMessage(plugin.getConfigManager().getMessage("curse.received"));
                caster.sendMessage(plugin.getConfigManager().getMessage("curse.applied",
                        "%target%", target.getName()));

                // Spawn phantoms near the target periodically
                // This is handled by a scheduled task in the curse manager
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (target.isOnline() && !target.isSleeping()) {
                        // Spawn a few phantoms
                        for (int i = 0; i < 3; i++) {
                            var loc = target.getLocation().add(
                                    (Math.random() - 0.5) * 20,
                                    10 + Math.random() * 10,
                                    (Math.random() - 0.5) * 20);
                            target.getWorld().spawnEntity(loc,
                                    org.bukkit.entity.EntityType.PHANTOM);
                        }
                    }
                }, 100L); // 5 seconds delay
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                // Spawn phantoms near caster
                for (int i = 0; i < 5; i++) {
                    var loc = caster.getLocation().add(
                            (Math.random() - 0.5) * 20,
                            10 + Math.random() * 10,
                            (Math.random() - 0.5) * 20);
                    caster.getWorld().spawnEntity(loc,
                            org.bukkit.entity.EntityType.PHANTOM);
                }
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
            }
            default -> {
            }
        }
        return result;
    }
}
