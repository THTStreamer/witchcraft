package com.witchcraft.api;

import com.witchcraft.Witchcraft;
import com.witchcraft.data.CovenData;
import com.witchcraft.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * PlaceholderAPI expansion for Witchcraft.
 * Registered as an internal expansion (part of the plugin).
 */
public class WitchcraftPlaceholderExpansion extends PlaceholderExpansion {

    private final Witchcraft plugin;

    public WitchcraftPlaceholderExpansion(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "witchcraft";
    }

    @Override
    public String getAuthor() {
        return "THTStreamer";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Keep registered across PAPI reloads
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";

        String param = params.toLowerCase();

        // ===== Player placeholders =====

        if (param.equals("exhausted")) {
            return String.valueOf(plugin.getArcaneExhaustion().isExhausted(player.getUniqueId()));
        }

        if (param.equals("exhaustion_remaining")) {
            return String.valueOf(plugin.getArcaneExhaustion().getRemainingTicks(player.getUniqueId()));
        }

        if (param.equals("can_cast")) {
            return String.valueOf(plugin.getArcaneExhaustion().canCast(player.getUniqueId()));
        }

        PlayerData playerData = plugin.getDataManager().getPlayerData(player.getUniqueId());

        if (param.equals("learned_count")) {
            return String.valueOf(playerData.getLearnedIncantations().size());
        }

        if (param.equals("rituals_count")) {
            return String.valueOf(playerData.getKnownRituals().size());
        }

        if (param.equals("has_guide")) {
            return String.valueOf(playerData.hasReceivedGuide());
        }

        // ===== Coven placeholders =====

        if (param.equals("in_coven")) {
            return String.valueOf(playerData.isInCoven());
        }

        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());

        if (param.equals("coven_name")) {
            return coven != null ? coven.getName() : "";
        }

        if (param.equals("coven_size")) {
            return coven != null ? String.valueOf(coven.getSize()) : "0";
        }

        if (param.equals("coven_role")) {
            if (coven == null) return "";
            return coven.getRank(player.getUniqueId()).getName();
        }

        if (param.equals("coven_chunks")) {
            return coven != null ? String.valueOf(coven.getClaimedChunkCount()) : "0";
        }

        if (param.equals("coven_max_chunks")) {
            return String.valueOf(CovenData.MAX_CLAIMED_CHUNKS);
        }

        if (param.equals("coven_leader")) {
            if (coven == null) return "";
            var leaders = coven.getMembersWithRank(com.witchcraft.data.CovenRank.PRIEST);
            leaders.addAll(coven.getMembersWithRank(com.witchcraft.data.CovenRank.PRIESTESS));
            if (leaders.isEmpty()) return "None";
            UUID leaderId = leaders.iterator().next();
            Player leader = org.bukkit.Bukkit.getPlayer(leaderId);
            return leader != null ? leader.getName() : "Offline";
        }

        // ===== Spell-specific placeholders =====

        if (param.startsWith("knows_")) {
            String spellId = param.substring(6);
            return String.valueOf(plugin.getIncantationManager().hasLearned(
                    player.getUniqueId(), spellId));
        }

        if (param.startsWith("cooldown_")) {
            String spellId = param.substring(9);
            return String.valueOf(plugin.getIncantationManager().getCooldowns()
                    .getRemainingCooldown(player.getUniqueId(), spellId));
        }

        return null;
    }
}
