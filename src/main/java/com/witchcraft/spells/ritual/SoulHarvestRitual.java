package com.witchcraft.spells.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.ExperienceOrb;

import java.util.List;

public class SoulHarvestRitual extends Spell {

    public SoulHarvestRitual(Witchcraft plugin) {
        super(plugin, "soul_harvest_ritual", "Gathering of Lost Souls",
                SpellCategory.CURSE,
                List.of(Ingredient.ECHO_SHARD, Ingredient.QUARTZ, Ingredient.COAL),
                "anima collectio perdita",
                1200, 5, 0.85, 0.1, 0.05,
                "witchcraft.cast");
    }

    @Override
    public SpellResult execute(Player caster, Location location, Player target) {
        SpellResult result = rollResult();
        switch (result) {
            case SUCCESS -> {
                int xpAmount = 20 + (int) (Math.random() * 30);
                ExperienceOrb orb = location.getWorld().spawn(location.clone().add(0, 1, 0),
                        ExperienceOrb.class);
                orb.setExperience(xpAmount);
                caster.sendMessage("\u00A75Gathered \u00A7f" + xpAmount + " \u00A75experience from lost souls.");
                playEffects(caster.getLocation());
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                caster.setLevel(Math.max(0, caster.getLevel() - 5));
            }
            default -> {}
        }
        return result;
    }

    private void playEffects(Location loc) {
        var world = loc.getWorld();
        if (world == null) return;
        world.spawnParticle(org.bukkit.Particle.SOUL, loc.clone().add(0, 1, 0), 50, 1, 1, 1);
        world.playSound(loc, org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
    }
}
