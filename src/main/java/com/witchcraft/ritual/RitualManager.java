package com.witchcraft.ritual;

import com.witchcraft.Witchcraft;
import com.witchcraft.api.events.WitchSpellCastEvent;
import com.witchcraft.core.Ingredient;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCooldown;
import com.witchcraft.core.SpellResult;
import com.witchcraft.util.TargetPaper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all ritual cauldron activities.
 * Tracks cauldrons by location, processes ingredients, and completes rituals.
 */
public class RitualManager {

    private final Witchcraft plugin;
    private final RitualRecipeRegistry recipeRegistry;

    /**
     * Active rituals keyed by cauldron location string.
     */
    private final Map<String, RitualCauldron> activeRituals = new ConcurrentHashMap<>();

    /**
     * Pending rituals (ingredients being added, not yet started).
     * Keyed by cauldron location string.
     */
    private final Map<String, RitualCauldron> pendingRituals = new ConcurrentHashMap<>();

    private final SpellCooldown cooldowns = new SpellCooldown();

    public RitualManager(Witchcraft plugin) {
        this.plugin = plugin;
        this.recipeRegistry = new RitualRecipeRegistry();
    }

    /**
     * Gets the recipe registry.
     *
     * @return the recipe registry
     */
    public RitualRecipeRegistry getRecipeRegistry() {
        return recipeRegistry;
    }

    /**
     * Gets the spell cooldown manager.
     *
     * @return the cooldown manager
     */
    public SpellCooldown getCooldowns() {
        return cooldowns;
    }

    /**
     * Attempts to add a target paper to a cauldron.
     * The target paper designates the player to be targeted by the spell.
     *
     * @param player      the player adding the paper
     * @param block       the cauldron block
     * @param targetPaper the target paper item
     * @return true if the paper was accepted
     */
    public boolean addTargetPaper(Player player, Block block, ItemStack targetPaper) {
        String locationKey = getLocationKey(block.getLocation());

        // Check if cauldron is already active
        if (activeRituals.containsKey(locationKey)) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.already-ritual"));
            return false;
        }

        // Get or create pending ritual
        RitualCauldron cauldron = pendingRituals.get(locationKey);

        if (cauldron == null) {
            // No recipe yet - we need ingredients first before a target paper makes sense
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.target-before-ingredients"));
            return false;
        }

        // Extract target from paper
        UUID targetUUID = TargetPaper.getTargetUUID(plugin, targetPaper);
        String targetName = TargetPaper.getTargetName(plugin, targetPaper);

        if (targetUUID == null || targetName == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.invalid-target-paper"));
            return false;
        }

        // Check if spell requires a target
        Spell spell = plugin.getSpellRegistry().getSpell(cauldron.getRecipe().getSpellId());
        if (spell != null && !spell.requiresTarget()) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.no-target-needed"));
            return false;
        }

        // Don't allow targeting yourself
        if (targetUUID.equals(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.cannot-target-self"));
            return false;
        }

        // Set target on cauldron
        cauldron.setTargetPlayerId(targetUUID);
        cauldron.setTargetPlayerName(targetName);

        // Consume the paper
        if (targetPaper.getAmount() > 1) {
            targetPaper.setAmount(targetPaper.getAmount() - 1);
        } else {
            player.getInventory().removeItem(targetPaper);
        }

        player.sendMessage(plugin.getConfigManager().getMessage("ritual.target-added",
                "%target%", targetName));

        // Play effect
        block.getWorld().spawnParticle(org.bukkit.Particle.ENCHANT,
                block.getLocation().add(0.5, 1, 0.5), 15, 0.3, 0.3, 0.3);
        block.getWorld().playSound(block.getLocation(),
                org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.2f);

        // Check if all ingredients + target are ready
        if (cauldron.hasAllIngredients()) {
            pendingRituals.remove(locationKey);
            startRitual(player, cauldron);
        }

        return true;
    }

    /**
     * Attempts to add an ingredient to a cauldron.
     * If the cauldron has no pending ritual, creates one.
     * If all ingredients are now present, automatically starts the ritual.
     *
     * @param player     the player adding the ingredient
     * @param block      the cauldron block
     * @param ingredient the ingredient being added
     * @return true if the ingredient was accepted
     */
    public boolean addIngredient(Player player, Block block, Ingredient ingredient) {
        String locationKey = getLocationKey(block.getLocation());

        // Check if cauldron is already active (ritual in progress)
        if (activeRituals.containsKey(locationKey)) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.already-ritual"));
            return false;
        }

        // Check if this cauldron has water
        if (block.getType() != Material.WATER_CAULDRON) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.cauldron-empty"));
            return false;
        }

        // Get or create pending ritual for this cauldron
        RitualCauldron cauldron = pendingRituals.get(locationKey);

        if (cauldron == null) {
            // No pending ritual yet - find all recipes that contain this ingredient
            // and create a tentative cauldron with the best match
            RitualRecipe recipe = findRecipeForIngredient(ingredient);
            if (recipe == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("ritual.ingredient-failed"));
                return false;
            }

            cauldron = new RitualCauldron(player.getUniqueId(), block.getLocation(), recipe);
            pendingRituals.put(locationKey, cauldron);
        } else {
            // Already have a pending ritual - check if this ingredient fits
            // Try to find a recipe that matches ALL added ingredients + this new one
            java.util.List<Ingredient> allIngredients = new java.util.ArrayList<>(cauldron.getAddedIngredients());
            allIngredients.add(ingredient);

            RitualRecipe bestMatch = findBestRecipeMatch(allIngredients);
            if (bestMatch == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("ritual.ingredient-failed"));
                return false;
            }

            // If we found a better matching recipe, switch to it
            if (!bestMatch.equals(cauldron.getRecipe())) {
                cauldron.setRecipe(bestMatch);
            }
        }

        // Verify the ingredient belongs to this recipe
        if (!cauldron.getRecipe().getRequiredIngredients().contains(ingredient)) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.ingredient-failed"));
            return false;
        }

        // Check if we already have this ingredient
        long currentCount = cauldron.getAddedIngredients().stream()
                .filter(i -> i == ingredient)
                .count();
        long requiredCount = cauldron.getRecipe().getRequiredIngredients().stream()
                .filter(i -> i == ingredient)
                .count();
        if (currentCount >= requiredCount) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.ingredient-failed"));
            return false;
        }

        // Add the ingredient
        if (!cauldron.addIngredient(ingredient)) {
            player.sendMessage(plugin.getConfigManager().getMessage("ritual.ingredient-failed"));
            return false;
        }

        // Notify player
        player.sendMessage(plugin.getConfigManager().getMessage("ritual.ingredient-added",
                "%ingredient%", ingredient.getDisplayName()));

        // Play drip effect
        block.getWorld().spawnParticle(org.bukkit.Particle.DRIPPING_WATER,
                block.getLocation().add(0.5, 1, 0.5), 10, 0.3, 0.3, 0.3);
        block.getWorld().playSound(block.getLocation(),
                org.bukkit.Sound.BLOCK_WATER_AMBIENT, 0.5f, 1.2f);

        // Check if all ingredients have been added
        if (cauldron.hasAllIngredients()) {
            // Remove from pending and start the ritual
            pendingRituals.remove(locationKey);
            startRitual(player, cauldron);
        }

        return true;
    }

    /**
     * Finds the best recipe match for a set of ingredients.
     * Prefers recipes where the ingredients are a subset of required ingredients.
     *
     * @param ingredients the ingredients to match
     * @return the best matching recipe, or null if none match
     */
    private RitualRecipe findBestRecipeMatch(java.util.List<Ingredient> ingredients) {
        RitualRecipe bestMatch = null;
        int bestScore = -1;

        for (RitualRecipe recipe : recipeRegistry.getAllRecipes()) {
            java.util.List<Ingredient> required = recipe.getRequiredIngredients();

            // Check if all provided ingredients are in this recipe
            boolean allMatch = true;
            for (Ingredient ing : ingredients) {
                long requiredCount = required.stream().filter(i -> i == ing).count();
                long providedCount = ingredients.stream().filter(i -> i == ing).count();
                if (providedCount > requiredCount) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                // Score = how many required ingredients we've provided (more is better)
                int score = ingredients.size();
                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = recipe;
                }
            }
        }

        return bestMatch;
    }

    /**
     * Starts a ritual with the given cauldron and recipe.
     */
    private void startRitual(Player caster, RitualCauldron cauldron) {
        RitualRecipe recipe = cauldron.getRecipe();

        // Check permission
        if (!caster.hasPermission("witchcraft.cast")) {
            caster.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return;
        }

        // Check exhaustion
        if (plugin.getArcaneExhaustion().isExhausted(caster.getUniqueId())) {
            caster.sendMessage(plugin.getConfigManager().getMessage("incantation.exhausted"));
            return;
        }

        // Check cooldown
        if (cooldowns.isOnCooldown(caster.getUniqueId(), recipe.getSpellId())) {
            caster.sendMessage(plugin.getConfigManager().getMessage("ritual.already-ritual"));
            return;
        }

        // Check moon phase
        if (recipe.isMoonPhaseRequired()) {
            int currentPhase = caster.getWorld().getMoonPhase().ordinal();
            if (currentPhase != recipe.getRequiredMoonPhase()) {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.moon-phase-wrong"));
                return;
            }
        }

        // Check weather
        if (recipe.isWeatherRequired()) {
            String currentWeather = caster.getWorld().hasStorm() ? "storm" : "clear";
            if (!currentWeather.equals(recipe.getRequiredWeather().toLowerCase())) {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.weather-wrong"));
                return;
            }
        }

        // Check coven requirements
        if (recipe.isCovenRitual()) {
            com.witchcraft.data.CovenData coven = plugin.getCovenManager().getCovenForMember(caster.getUniqueId());
            if (coven == null) {
                caster.sendMessage("\u00A7cThis ritual requires a coven.");
                applyCovenFailurePenalty(caster);
                return;
            }

            int membersPresent = plugin.getCovenManager().countMembersNear(
                    coven, cauldron.getCauldronLocation(), recipe.getCovenRadius());
            if (membersPresent < recipe.getRequiredCovenSize()) {
                caster.sendMessage("\u00A7cThis ritual requires " + recipe.getRequiredCovenSize() +
                        " coven members nearby. Only " + membersPresent + " present.");
                applyCovenFailurePenalty(caster);
                return;
            }
        }

        // Check XP
        Spell spell = plugin.getSpellRegistry().getSpell(recipe.getSpellId());
        if (spell != null && caster.getLevel() < spell.getXpCost()) {
            caster.sendMessage(plugin.getConfigManager().getMessage("ritual.insufficient-xp"));
            return;
        }

        // Check if spell requires a target and one is set
        if (spell != null && spell.requiresTarget() && !cauldron.hasTarget()) {
            caster.sendMessage(plugin.getConfigManager().getMessage("ritual.target-required"));
            return;
        }

        // Consume ingredients from player's inventory
        consumeIngredients(caster, recipe);

        // Activate the ritual
        cauldron.setActive(true);
        cauldron.setStartTime(Bukkit.getCurrentTick());
        activeRituals.put(cauldron.getLocationKey(), cauldron);

        caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-started"));
        playRitualEffects(cauldron.getCauldronLocation(), "start");
    }

    /**
     * Applies the coven failure penalty - caster loses magic for 3 Minecraft days (63000 ticks).
     */
    private void applyCovenFailurePenalty(Player caster) {
        plugin.getArcaneExhaustion().applyExhaustion(caster.getUniqueId());
        caster.sendMessage("\u00A7c\u00A7lThe ancient spirits punish your failure!");
        caster.sendMessage("\u00A77You have lost your magic for 3 Minecraft days.");
        caster.getWorld().spawnParticle(org.bukkit.Particle.SMOKE,
                caster.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5);
        caster.getWorld().playSound(caster.getLocation(),
                org.bukkit.Sound.ENTITY_WITHER_HURT, 1.0f, 0.5f);
    }

    /**
     * Consumes the required ingredients from a player's inventory.
     */
    private void consumeIngredients(Player player, RitualRecipe recipe) {
        for (Ingredient required : recipe.getRequiredIngredients()) {
            boolean consumed = false;
            for (var item : player.getInventory().getContents()) {
                if (item != null && item.getType() == required.getMaterial() && !consumed) {
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().removeItem(item);
                    }
                    consumed = true;
                }
            }
        }
    }

    /**
     * Finds a recipe that includes the given ingredient.
     */
    private RitualRecipe findRecipeForIngredient(Ingredient ingredient) {
        for (RitualRecipe recipe : recipeRegistry.getAllRecipes()) {
            if (recipe.getRequiredIngredients().contains(ingredient)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Processes all active rituals. Should be called every tick.
     */
    public void processRituals() {
        long currentTick = Bukkit.getCurrentTick();

        activeRituals.entrySet().removeIf(entry -> {
            RitualCauldron ritual = entry.getValue();

            if (!ritual.isActive()) return true;

            Player caster = Bukkit.getPlayer(ritual.getCasterId());
            if (caster == null || !caster.isOnline()) {
                return true;
            }

            // Verify caster is still near the cauldron
            if (!isNearCauldron(caster, ritual.getCauldronLocation())) {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.too-far-away"));
                cancelRitual(ritual);
                return true;
            }

            if (ritual.shouldComplete(currentTick)) {
                completeRitual(caster, ritual);
                return true;
            }

            // Play charging effects periodically
            double progress = ritual.getChargeProgress(currentTick);
            if (currentTick % 20 == 0) {
                playChargingEffects(ritual.getCauldronLocation(), progress);
            }

            return false;
        });

        // Clean up expired pending rituals (no interaction for 30 seconds)
        pendingRituals.entrySet().removeIf(entry -> {
            RitualCauldron ritual = entry.getValue();
            // If no ritual started within 600 ticks (30 seconds), remove it
            return ritual.getAddedIngredients().isEmpty() && ritual.getStartTime() == 0;
        });
    }

    /**
     * Completes a ritual.
     */
    private void completeRitual(Player caster, RitualCauldron ritual) {
        Spell spell = plugin.getSpellRegistry().getSpell(ritual.getRecipe().getSpellId());
        if (spell == null) return;

        // Resolve target player from target paper
        Player target = null;
        if (ritual.hasTarget()) {
            target = Bukkit.getPlayer(ritual.getTargetPlayerId());
            if (target == null || !target.isOnline()) {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.target-offline",
                        "%target%", ritual.getTargetPlayerName()));
                playRitualEffects(ritual.getCauldronLocation(), "failure");
                return;
            }
        }

        // Set cooldown
        cooldowns.setCooldown(caster.getUniqueId(), spell.getId(), spell.getCooldownTicks());

        // Consume XP
        caster.setLevel(caster.getLevel() - spell.getXpCost());

        // Execute the spell with target
        SpellResult result = spell.execute(caster, caster.getLocation(), target);

        // Fire event
        WitchSpellCastEvent event = new WitchSpellCastEvent(caster, spell, target);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
            playRitualEffects(ritual.getCauldronLocation(), "failure");
            return;
        }

        switch (result) {
            case SUCCESS -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-success"));
                playRitualEffects(ritual.getCauldronLocation(), "success");
                playRitualSounds(ritual.getCauldronLocation(), "success");
            }
            case FAILURE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-failure"));
                playRitualEffects(ritual.getCauldronLocation(), "failure");
                playRitualSounds(ritual.getCauldronLocation(), "failure");
            }
            case BACKFIRE -> {
                caster.sendMessage(plugin.getConfigManager().getMessage("ritual.ritual-backfire"));
                applyBackfireEffects(caster);
                playRitualEffects(ritual.getCauldronLocation(), "backfire");
                playRitualSounds(ritual.getCauldronLocation(), "backfire");
            }
            default -> {
            }
        }

        ritual.setCompleted(true);
    }

    /**
     * Applies backfire effects to the caster.
     */
    private void applyBackfireEffects(Player caster) {
        caster.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 200, 1));
        caster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
        caster.damage(4.0);
    }

    /**
     * Checks if a player is within activation distance of the cauldron.
     */
    private boolean isNearCauldron(Player player, Location cauldronLocation) {
        double maxDistance = plugin.getConfigManager().getConfig()
                .getDouble("general.cauldron-activation-distance", 3.0);
        return player.getLocation().distance(cauldronLocation) <= maxDistance;
    }

    /**
     * Cancels a ritual and refunds ingredients.
     */
    private void cancelRitual(RitualCauldron ritual) {
        activeRituals.remove(ritual.getLocationKey());
    }

    /**
     * Cancels all rituals for a player.
     *
     * @param playerId the player's UUID
     */
    public void cancelRituals(UUID playerId) {
        activeRituals.values().removeIf(r -> r.getCasterId().equals(playerId));
        pendingRituals.values().removeIf(r -> r.getCasterId().equals(playerId));
    }

    /**
     * Plays charging effects with increasing intensity.
     */
    private void playChargingEffects(Location location, double progress) {
        var world = location.getWorld();
        if (world == null) return;

        int baseCount = (int) (10 * progress);
        double spread = 0.5 + progress;

        world.spawnParticle(org.bukkit.Particle.ENCHANT,
                location.clone().add(0, 1, 0), baseCount, spread, spread, spread);
        world.spawnParticle(org.bukkit.Particle.SMOKE,
                location.clone().add(0, 0.5, 0), baseCount / 2, 0.3, 0.3, 0.3);

        // Play ambient sounds during charge
        if (progress > 0.5 && Math.random() < 0.1) {
            world.playSound(location, org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                    0.3f, 0.8f + (float) progress * 0.4f);
        }
    }

    /**
     * Plays ritual particle effects.
     */
    private void playRitualEffects(Location location, String type) {
        var world = location.getWorld();
        if (world == null) return;

        int density = (int) (20 * plugin.getConfigManager().getConfig()
                .getDouble("effects.particle-density", 1.0));

        switch (type) {
            case "start" -> {
                world.spawnParticle(org.bukkit.Particle.ENCHANT, location, density, 0.5, 0.5, 0.5);
                world.spawnParticle(org.bukkit.Particle.SMOKE, location, density / 2, 0.3, 0.3, 0.3);
            }
            case "success" -> {
                world.spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING,
                        location.clone().add(0, 1, 0), density * 2, 1, 1, 1);
                world.spawnParticle(org.bukkit.Particle.ENCHANT,
                        location.clone().add(0, 1, 0), density, 0.5, 0.5, 0.5);
            }
            case "failure" -> {
                world.spawnParticle(org.bukkit.Particle.SMOKE, location, density, 0.5, 0.5, 0.5);
                world.spawnParticle(org.bukkit.Particle.LARGE_SMOKE,
                        location, density / 2, 0.3, 0.3, 0.3);
            }
            case "backfire" -> {
                world.spawnParticle(org.bukkit.Particle.FLAME,
                        location.clone().add(0, 1, 0), density * 2, 0.5, 0.5, 0.5);
                world.spawnParticle(org.bukkit.Particle.SMOKE,
                        location.clone().add(0, 1, 0), density, 0.5, 0.5, 0.5);
            }
        }
    }

    /**
     * Plays ritual sounds.
     */
    private void playRitualSounds(Location location, String type) {
        var world = location.getWorld();
        if (world == null) return;

        switch (type) {
            case "success" -> {
                world.playSound(location, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                world.playSound(location, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.5f);
            }
            case "failure" -> {
                world.playSound(location, org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 0.5f);
                world.playSound(location, org.bukkit.Sound.BLOCK_LAVA_POP, 0.5f, 0.5f);
            }
            case "backfire" -> {
                world.playSound(location, org.bukkit.Sound.ENTITY_WITHER_HURT, 1.0f, 0.5f);
                world.playSound(location, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 0.5f);
            }
        }
    }

    /**
     * Generates a location key for map lookups.
     *
     * @param location the location
     * @return "world:x:y:z"
     */
    public String getLocationKey(Location location) {
        return location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" +
                location.getBlockZ();
    }
}
