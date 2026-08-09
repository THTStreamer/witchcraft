package com.witchcraft.api;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.data.CovenData;
import com.witchcraft.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI expansion for Witchcraft.
 *
 * Supported placeholders:
 *
 * Player:
 *   %witchcraft_exhausted%          - true/false
 *   %witchcraft_exhaustion_remaining% - remaining ticks
 *   %witchcraft_can_cast%           - true/false
 *   %witchcraft_learned_count%      - number of learned incantations
 *   %witchcraft_rituals_count%      - number of known rituals
 *   %witchcraft_has_guide%          - true/false
 *
 * Coven:
 *   %witchcraft_in_coven%           - true/false
 *   %witchcraft_coven_name%         - coven name
 *   %witchcraft_coven_size%         - member count
 *   %witchcraft_coven_role%         - leader/member
 *   %witchcraft_coven_chunks%       - claimed chunks count
 *   %witchcraft_coven_max_chunks%   - max chunks (4)
 *   %witchcraft_coven_leader%       - leader name
 *
 * Spell-specific:
 *   %witchcraft_knows_<spell_id>%   - true/false
 *   %witchcraft_cooldown_<spell_id>% - remaining cooldown ticks
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
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";

        // Normalize params to lowercase for case-insensitive matching
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
            return coven.isLeader(player.getUniqueId()) ? "leader" : "member";
        }

        if (param.equals("coven_chunks")) {
            return coven != null ? String.valueOf(coven.getClaimedChunkCount()) : "0";
        }

        if (param.equals("coven_max_chunks")) {
            return String.valueOf(CovenData.MAX_CLAIMED_CHUNKS);
        }

        if (param.equals("coven_leader")) {
            if (coven == null) return "";
            org.bukkit.entity.Player leader = org.bukkit.Bukkit.getPlayer(coven.getLeaderId());
            return leader != null ? leader.getName() : "Offline";
        }

        // ===== Spell-specific placeholders =====

        // %witchcraft_knows_<spell_id>%
        if (param.startsWith("knows_")) {
            String spellId = param.substring(6);
            return String.valueOf(plugin.getIncantationManager().hasLearned(
                    player.getUniqueId(), spellId));
        }

        // %witchcraft_cooldown_<spell_id>%
        if (param.startsWith("cooldown_")) {
            String spellId = param.substring(9);
            return String.valueOf(plugin.getIncantationManager().getCooldowns()
                    .getRemainingCooldown(player.getUniqueId(), spellId));
        }

        return null; // unrecognized placeholder
    }
}
