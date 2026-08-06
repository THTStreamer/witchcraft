package com.witchcraft.spells.divination;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import com.witchcraft.spells.protection.AntiScryingProtection;
import org.bukkit.Location;
import org.bukkit.entity.Player;



/**
 * Scrying ritual that allows spying on another player.
 */
public class ScryingRitual extends Spell {

    public ScryingRitual(Witchcraft plugin) {
        super(plugin, "scrying_ritual", "Mirror of the Soul",
                SpellCategory.DIVINATION,
                2400, 7, 0.85, 0.1, 0.05,
                "witchcraft.scrying", true);
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        if (target == null) return SpellResult.INVALID;

        AntiScryingProtection antiScrying = getAntiScryingProtection();
        if (antiScrying != null && antiScrying.isProtected(target.getUniqueId())) {
            caster.sendMessage(plugin.getConfigManager().getMessage("scrying.blocked"));
            target.sendMessage(plugin.getConfigManager().getMessage("anti-scrying.protected-player-notified"));
            return SpellResult.BLOCKED;
        }

        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                revealInformation(caster, target);
                caster.sendMessage(plugin.getConfigManager().getMessage("scrying.started"));
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                revealInformation(caster, caster);
            }
            default -> {
            }
        }
        return result;
    }

    private void revealInformation(Player caster, Player target) {
        caster.sendMessage("\u00A75\u00A7l--- Scrying Results ---");
        caster.sendMessage("\u00A77Target: \u00A7f" + target.getName());

        String biome = String.valueOf(target.getLocation().getBlock().getBiome())
                .replace("_", " ").toLowerCase();
        caster.sendMessage(plugin.getConfigManager().getMessage("scrying.result",
                "%target%", target.getName(), "%biome%", biome));

        String dimension = target.getWorld().getEnvironment().name()
                .replace("_", " ").toLowerCase();
        caster.sendMessage("\u00A77Dimension: \u00A7f" + dimension);

        Location loc = target.getLocation();
        int approxX = (loc.getBlockX() / 100) * 100;
        int approxZ = (loc.getBlockZ() / 100) * 100;
        caster.sendMessage(plugin.getConfigManager().getMessage("scrying.result-coords",
                "%x%", String.valueOf(approxX), "%z%", String.valueOf(approxZ)));

        caster.sendMessage(plugin.getConfigManager().getMessage("scrying.result-health",
                "%health%", String.valueOf((int) target.getHealth())));

        caster.sendMessage(plugin.getConfigManager().getMessage("scrying.result-hunger",
                "%hunger%", String.valueOf(target.getFoodLevel())));

        String weather = target.getWorld().hasStorm() ? "stormy" : "clear";
        caster.sendMessage(plugin.getConfigManager().getMessage("scrying.result-weather",
                "%weather%", weather));

        String item = target.getInventory().getItemInMainHand().getType().name()
                .replace("_", " ").toLowerCase();
        caster.sendMessage(plugin.getConfigManager().getMessage("scrying.result-held-item",
                "%item%", item));

        caster.sendMessage("\u00A75\u00A7l--- End Scrying ---");
    }

    private AntiScryingProtection getAntiScryingProtection() {
        var spell = plugin.getSpellRegistry().getSpell("anti_scrying_protection");
        if (spell instanceof AntiScryingProtection protection) {
            return protection;
        }
        return null;
    }
}
