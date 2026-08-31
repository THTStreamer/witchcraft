package com.witchcraft.coven;

import com.witchcraft.Witchcraft;
import com.witchcraft.api.events.WitchCovenSpellCastEvent;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.core.SpellResult;
import com.witchcraft.data.CovenData;
import com.witchcraft.util.TargetPaper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A spell that requires multiple coven members to chant incantations in sequence.
 * Each member says one line of the incantation in order to activate the spell.
 */
public class CovenSpell {

    private final Witchcraft plugin;
    private final String id;
    private final String displayName;
    private final SpellCategory category;
    private final int requiredCovenSize;
    private final double covenRadius;
    private final List<String> incantationLines;
    private final Spell effectSpell;
    private final long timeoutTicks;

    private final Map<UUID, ActiveCovenSpell> activeSpells = new ConcurrentHashMap<>();

    public CovenSpell(Witchcraft plugin, String id, String displayName, SpellCategory category,
                      int requiredCovenSize, double covenRadius, List<String> incantationLines,
                      Spell effectSpell, long timeoutTicks) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.category = category;
        this.requiredCovenSize = requiredCovenSize;
        this.covenRadius = covenRadius;
        this.incantationLines = incantationLines;
        this.effectSpell = effectSpell;
        this.timeoutTicks = timeoutTicks;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public SpellCategory getCategory() { return category; }
    public int getRequiredCovenSize() { return requiredCovenSize; }
    public double getCovenRadius() { return covenRadius; }
    public List<String> getIncantationLines() { return incantationLines; }
    public Spell getEffectSpell() { return effectSpell; }

    /**
     * Attempts to progress the coven spell when a player says a line.
     *
     * @param speaker the player speaking
     * @param message the message said
     * @return true if the line was accepted
     */
    public boolean onPlayerSpeak(Player speaker, String message) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(speaker.getUniqueId());
        if (coven == null) return false;

        String normalizedMessage = normalizeMessage(message);

        // Find or create active spell for this coven
        ActiveCovenSpell active = activeSpells.get(coven.getCovenId());

        if (active == null) {
            // Check if this is the first line of the incantation
            String firstLine = normalizeMessage(incantationLines.get(0));
            if (!normalizedMessage.equals(firstLine)) return false;

            // Check if enough coven members are nearby
            Location center = speaker.getLocation();
            int membersNear = plugin.getCovenManager().countMembersNear(coven, center, covenRadius);
            if (membersNear < requiredCovenSize) {
                speaker.sendMessage("\u00A7cYou need " + requiredCovenSize +
                        " coven members within " + (int) covenRadius +
                        " blocks. Only " + membersNear + " present.");
                return false;
            }

            // Check permission
            if (!speaker.hasPermission("witchcraft.cast")) {
                speaker.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
                return false;
            }

            // Check exhaustion
            if (plugin.getArcaneExhaustion().isExhausted(speaker.getUniqueId())) {
                speaker.sendMessage(plugin.getConfigManager().getMessage("incantation.exhausted"));
                return false;
            }

            // Check if caster knows this coven spell/ritual (only caster needs to know)
            if (!speaker.hasPermission("witchcraft.admin")) {
                var pdata = plugin.getDataManager().getPlayerData(speaker.getUniqueId());
                if (!pdata.knowsRitual(id)) {
                    speaker.sendMessage("\u00A7cYou haven't learned this coven spell. Read its book: \u00A77" + displayName);
                    return false;
                }
            }

            // Start a new active spell
            active = new ActiveCovenSpell(coven, speaker, center, incantationLines.size());
            activeSpells.put(coven.getCovenId(), active);
            active.markLineSpoken(speaker.getUniqueId(), 0);
            broadcastToCoven(coven, "\u00A75\u00A7l" + speaker.getName() +
                    " begins the incantation for " + displayName + "...");
            return true;
        }

        // Check if spell has timed out
        if (System.currentTimeMillis() - active.getStartTime() > timeoutTicks * 50) {
            activeSpells.remove(coven.getCovenId());
            broadcastToCoven(coven, "\u00A7cThe incantation fades... too slow.");
            return false;
        }

        // Find which line this message matches
        int lineIndex = -1;
        for (int i = 0; i < incantationLines.size(); i++) {
            if (normalizeMessage(incantationLines.get(i)).equals(normalizedMessage)) {
                lineIndex = i;
                break;
            }
        }

        if (lineIndex == -1) return false;

        // Check if this line was already spoken
        if (active.hasSpokenLine(lineIndex)) return false;

        // Check if the previous lines have been spoken (must be in order)
        if (lineIndex > 0 && !active.hasSpokenLine(lineIndex - 1)) {
            speaker.sendMessage("\u00A7cYou must wait for the previous line to be spoken.");
            return false;
        }

        // Check if the speaker is a coven member
        if (!coven.isMember(speaker.getUniqueId())) {
            speaker.sendMessage("\u00A7cYou are not a member of this coven.");
            return false;
        }

        // Check if this speaker has already spoken a line (each member says one line)
        if (active.hasSpeakerSpoken(speaker.getUniqueId())) {
            speaker.sendMessage("\u00A7cYou have already spoken your line.");
            return false;
        }

        // Accept the line
        active.markLineSpoken(speaker.getUniqueId(), lineIndex);
        broadcastToCoven(coven, "\u00A7d" + speaker.getName() +
                " speaks: \u00A77\"" + incantationLines.get(lineIndex) + "\"");

        // Play effect
        speaker.getWorld().spawnParticle(org.bukkit.Particle.ENCHANT,
                speaker.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5);

        // Check if all lines have been spoken
        if (active.allLinesSpoken()) {
            activeSpells.remove(coven.getCovenId());
            executeCovenSpell(active);
        }

        return true;
    }

    /**
     * Executes the coven spell after all lines are chanted.
     */
    private void executeCovenSpell(ActiveCovenSpell active) {
        Player caster = active.getCaster();
        CovenData coven = active.getCoven();

        broadcastToCoven(coven, "\u00A75\u00A7lThe incantation is complete! " + displayName + " activates!");

        // Check for target paper in caster's hand
        Player target = null;
        ItemStack targetPaper = TargetPaper.findInInventory(plugin, caster);
        if (targetPaper != null) {
            java.util.UUID targetUUID = TargetPaper.getTargetUUID(plugin, targetPaper);
            if (targetUUID != null) {
                target = Bukkit.getPlayer(targetUUID);
                if (target != null && target.isOnline() && !targetUUID.equals(caster.getUniqueId())) {
                    // Consume the target paper
                    TargetPaper.consumeFromInventory(plugin, caster);
                    broadcastToCoven(coven, "\u00A75\u00A7lTarget locked: \u00A7f" + target.getName());
                } else {
                    target = null;
                }
            }
        }

        // Build participant list from active spell speakers
        List<Player> participants = new ArrayList<>();
        for (UUID memberId : coven.getMemberIds()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                participants.add(member);
            }
        }

        // Fire event
        WitchCovenSpellCastEvent event = new WitchCovenSpellCastEvent(caster, this, coven,
                participants, target);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            broadcastToCoven(coven, "\u00A7cThe spell was disrupted!");
            return;
        }

        // Execute the effect spell
        SpellResult result = effectSpell.execute(caster, active.getCenter(), target);

        // Play success effects
        playCovenSpellEffects(active.getCenter(), result);

        // Set cooldown for all coven members who participated
        for (UUID memberId : coven.getMemberIds()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                plugin.getIncantationManager().getCooldowns().setCooldown(
                        memberId, effectSpell.getId(), effectSpell.getCooldownTicks());
            }
        }
    }

    private void playCovenSpellEffects(Location center, SpellResult result) {
        var world = center.getWorld();
        if (world == null) return;

        switch (result) {
            case SUCCESS -> {
                world.spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING,
                        center.clone().add(0, 1, 0), 100, 2, 2, 2);
                world.spawnParticle(org.bukkit.Particle.ENCHANT,
                        center.clone().add(0, 1, 0), 50, 1, 1, 1);
                world.playSound(center, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            }
            case FAILURE -> {
                world.spawnParticle(org.bukkit.Particle.SMOKE,
                        center.clone().add(0, 1, 0), 50, 1, 1, 1);
                world.playSound(center, org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 0.5f);
            }
            case BACKFIRE -> {
                world.spawnParticle(org.bukkit.Particle.FLAME,
                        center.clone().add(0, 1, 0), 80, 1, 1, 1);
                world.playSound(center, org.bukkit.Sound.ENTITY_WITHER_HURT, 1.0f, 0.5f);
            }
            default -> {}
        }
    }

    private void broadcastToCoven(CovenData coven, String message) {
        for (Player member : plugin.getCovenManager().getOnlineMembers(coven)) {
            member.sendMessage(message);
        }
    }

    private String normalizeMessage(String message) {
        return message.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Tracks the state of an active coven spell incantation.
     */
    private static class ActiveCovenSpell {
        private final CovenData coven;
        private final Player caster;
        private final Location center;
        private final long startTime;
        private final int totalLines;
        private final Set<Integer> spokenLines = new HashSet<>();
        private final Set<UUID> speakersWhoSpoke = new HashSet<>();

        ActiveCovenSpell(CovenData coven, Player caster, Location center, int totalLines) {
            this.coven = coven;
            this.caster = caster;
            this.center = center.clone();
            this.startTime = System.currentTimeMillis();
            this.totalLines = totalLines;
        }

        CovenData getCoven() { return coven; }
        Player getCaster() { return caster; }
        Location getCenter() { return center; }
        long getStartTime() { return startTime; }

        void markLineSpoken(UUID speakerId, int lineIndex) {
            spokenLines.add(lineIndex);
            speakersWhoSpoke.add(speakerId);
        }

        boolean hasSpokenLine(int lineIndex) {
            return spokenLines.contains(lineIndex);
        }

        boolean hasSpeakerSpoken(UUID speakerId) {
            return speakersWhoSpoke.contains(speakerId);
        }

        boolean allLinesSpoken() {
            return spokenLines.size() >= totalLines;
        }
    }
}
