package com.witchcraft.coven;

import com.witchcraft.Witchcraft;
import com.witchcraft.data.CovenData;
import com.witchcraft.data.CovenRank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Command handler for /coven.
 */
public class CovenCommand implements CommandExecutor, TabCompleter {

    private final Witchcraft plugin;

    public CovenCommand(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00A7cOnly players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player, args);
            case "invite" -> handleInvite(player, args);
            case "accept" -> handleAccept(player);
            case "leave" -> handleLeave(player);
            case "disband" -> handleDisband(player);
            case "info" -> handleInfo(player);
            case "list" -> handleList(player);
            case "kick" -> handleKick(player, args);
            case "claim" -> handleClaim(player);
            case "unclaim" -> handleUnclaim(player);
            case "chunks" -> handleChunks(player);
            case "setrank" -> handleSetRank(player, args);
            case "promote" -> handlePromote(player, args);
            case "demote" -> handleDemote(player, args);
            case "transfer" -> handleTransfer(player, args);
            default -> {
                sendHelp(player);
                yield true;
            }
        };
    }

    private boolean handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("\u00A7cUsage: /coven create <name>");
            return true;
        }

        if (plugin.getCovenManager().getCovenForMember(player.getUniqueId()) != null) {
            player.sendMessage("\u00A7cYou are already in a coven. Leave it first.");
            return true;
        }

        String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        CovenData coven = plugin.getCovenManager().createCoven(player, name);
        if (coven == null) {
            player.sendMessage("\u00A7cFailed to create coven.");
            return true;
        }

        player.sendMessage("\u00A7aCoven \u00A7e" + name + " \u00A7ahas been formed! You are the \u00A7fPriest\u00A7a.");
        return true;
    }

    private boolean handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("\u00A7cUsage: /coven invite <player>");
            return true;
        }

        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A7cYou are not in a coven.");
            return true;
        }

        if (!coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders (Priest/Priestess) can invite members.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("\u00A7cYou cannot invite yourself.");
            return true;
        }

        if (plugin.getCovenManager().getCovenForMember(target.getUniqueId()) != null) {
            player.sendMessage("\u00A7c" + target.getName() + " is already in a coven.");
            return true;
        }

        if (plugin.getCovenManager().inviteToCoven(player, target)) {
            player.sendMessage("\u00A7aInvite sent to \u00A7e" + target.getName());
            target.sendMessage("\u00A75\u00A7l--- Coven Invite ---");
            target.sendMessage("\u00A77You have been invited to join coven: \u00A7e" + coven.getName());
            target.sendMessage("\u00A77Type \u00A7f/coven accept \u00A77to join.");
            target.sendMessage("\u00A75\u00A7l--- End Invite ---");
        } else {
            player.sendMessage("\u00A7cFailed to send invite.");
        }

        return true;
    }

    private boolean handleAccept(Player player) {
        CovenData coven = plugin.getCovenManager().acceptInvite(player);
        if (coven == null) {
            player.sendMessage("\u00A7cNo pending coven invites found.");
            return true;
        }

        player.sendMessage("\u00A7aYou have joined coven: \u00A7e" + coven.getName() + " \u00A77as an Initiate.");

        for (UUID memberId : coven.getMemberIds()) {
            if (!memberId.equals(player.getUniqueId())) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.sendMessage("\u00A7e" + player.getName() + " \u00A7ahas joined the coven!");
                }
            }
        }

        return true;
    }

    private boolean handleLeave(Player player) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A7cYou are not in a coven.");
            return true;
        }

        if (coven.isLeader(player.getUniqueId()) && coven.getSize() > 1) {
            player.sendMessage("\u00A7cYou are a leader. Use /coven transfer to pass leadership, or /coven disband if you are the last leader.");
            return true;
        }

        plugin.getCovenManager().leaveCoven(player);
        player.sendMessage("\u00A7aYou have left the coven.");

        for (UUID memberId : coven.getMemberIds()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("\u00A7e" + player.getName() + " \u00A77has left the coven.");
            }
        }

        return true;
    }

    private boolean handleDisband(Player player) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A7cYou are not in a coven.");
            return true;
        }

        if (!coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly a leader can disband the coven.");
            return true;
        }

        for (UUID memberId : coven.getMemberIds()) {
            if (!memberId.equals(player.getUniqueId())) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.sendMessage("\u00A7cThe coven has been disbanded by the leader.");
                }
            }
        }

        plugin.getCovenManager().disbandCoven(coven);
        player.sendMessage("\u00A7aYour coven has been disbanded.");
        return true;
    }

    private boolean handleInfo(Player player) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A77You are not in a coven.");
            return true;
        }

        List<Player> online = plugin.getCovenManager().getOnlineMembers(coven);
        CovenRank myRank = coven.getRank(player.getUniqueId());

        player.sendMessage("\u00A75\u00A7l--- Coven Info ---");
        player.sendMessage("\u00A77Name: \u00A7e" + coven.getName());
        player.sendMessage("\u00A77Your Rank: \u00A7f" + myRank.getTitle());
        player.sendMessage("\u00A77Members: \u00A7f" + coven.getSize());
        player.sendMessage("\u00A77Online: \u00A7a" + online.size());
        player.sendMessage("\u00A77Claimed Chunks: \u00A7f" + coven.getClaimedChunkCount() +
                "/" + CovenData.MAX_CLAIMED_CHUNKS);
        player.sendMessage("");

        // Show leaders
        Set<UUID> priests = coven.getMembersWithRank(CovenRank.PRIEST);
        Set<UUID> priestesses = coven.getMembersWithRank(CovenRank.PRIESTESS);
        Set<UUID> council = coven.getMembersWithRank(CovenRank.COUNCIL);

        if (!priests.isEmpty() || !priestesses.isEmpty()) {
            player.sendMessage("\u00A75\u00A7lLeaders:");
            for (UUID id : priests) {
                Player p = Bukkit.getPlayer(id);
                player.sendMessage("\u00A77  \u00A7ePriest: \u00A7f" + (p != null ? p.getName() : "Offline"));
            }
            for (UUID id : priestesses) {
                Player p = Bukkit.getPlayer(id);
                player.sendMessage("\u00A77  \u00A7ePriestess: \u00A7f" + (p != null ? p.getName() : "Offline"));
            }
        }

        if (!council.isEmpty()) {
            player.sendMessage("\u00A75\u00A7lCouncil:");
            for (UUID id : council) {
                Player p = Bukkit.getPlayer(id);
                player.sendMessage("\u00A77  \u00A77" + (p != null ? p.getName() : "Offline"));
            }
        }

        player.sendMessage("\u00A75\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        return true;
    }

    private boolean handleList(Player player) {
        var covens = plugin.getCovenManager().getAllCovens();
        if (covens.isEmpty()) {
            player.sendMessage("\u00A77No covens exist yet.");
            return true;
        }

        player.sendMessage("\u00A75\u00A7l--- All Covens ---");
        for (CovenData coven : covens) {
            int online = plugin.getCovenManager().getOnlineMembers(coven).size();
            player.sendMessage("\u00A77- \u00A7e" + coven.getName() +
                    " \u00A77(\u00A7f" + coven.getSize() + "\u00A77 members, \u00A7a" + online + " online)");
        }
        player.sendMessage("\u00A75\u00A7l--- End Covens ---");
        return true;
    }

    private boolean handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("\u00A7cUsage: /coven kick <player>");
            return true;
        }

        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A7cYou are not in a coven.");
            return true;
        }

        if (!coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders can kick members.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("\u00A7cYou cannot kick yourself.");
            return true;
        }

        if (!coven.isMember(target.getUniqueId())) {
            player.sendMessage("\u00A7c" + target.getName() + " is not in your coven.");
            return true;
        }

        plugin.getCovenManager().leaveCoven(target);
        player.sendMessage("\u00A7a" + target.getName() + " has been kicked from the coven.");
        target.sendMessage("\u00A7cYou have been kicked from the coven.");
        return true;
    }

    private boolean handleClaim(Player player) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A7cYou are not in a coven.");
            return true;
        }

        if (!coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders can claim chunks.");
            return true;
        }

        plugin.getCovenManager().claimChunk(player);
        return true;
    }

    private boolean handleUnclaim(Player player) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A7cYou are not in a coven.");
            return true;
        }

        if (!coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders can unclaim chunks.");
            return true;
        }

        plugin.getCovenManager().unclaimChunk(player);
        return true;
    }

    private boolean handleChunks(Player player) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null) {
            player.sendMessage("\u00A7cYou are not in a coven.");
            return true;
        }

        var chunks = coven.getClaimedChunks();
        player.sendMessage("\u00A75\u00A7l--- Claimed Chunks (" +
                coven.getClaimedChunkCount() + "/" + CovenData.MAX_CLAIMED_CHUNKS + ") ---");
        if (chunks.isEmpty()) {
            player.sendMessage("\u00A77No chunks claimed yet.");
        } else {
            for (String chunkKey : chunks) {
                String[] parts = chunkKey.split(":");
                player.sendMessage("\u00A77- \u00A7e" + parts[0] +
                        " \u00A77[" + parts[1] + ", " + parts[2] + "]");
            }
        }
        player.sendMessage("\u00A75\u00A7l--- End Chunks ---");
        return true;
    }

    private boolean handleSetRank(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("\u00A7cUsage: /coven setrank <player> <rank>");
            player.sendMessage("\u00A77Ranks: priest, priestess, council, initiate");
            return true;
        }

        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null || !coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders can set ranks.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        CovenRank rank;
        try {
            rank = CovenRank.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("\u00A7cInvalid rank. Use: priest, priestess, council, initiate");
            return true;
        }

        if (plugin.getCovenManager().setRank(player, target, rank)) {
            player.sendMessage("\u00A7aSet " + target.getName() + "'s rank to \u00A7f" + rank.getTitle());
        } else {
            player.sendMessage("\u00A7cFailed to set rank. Leader slots may be full (max 2).");
        }
        return true;
    }

    private boolean handlePromote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("\u00A7cUsage: /coven promote <player>");
            return true;
        }

        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null || !coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders can promote members.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        CovenRank newRank = plugin.getCovenManager().promote(player, target);
        if (newRank != null) {
            player.sendMessage("\u00A7aPromoted " + target.getName() + " to \u00A7f" + newRank.getTitle());
        } else {
            player.sendMessage("\u00A7cCannot promote further. Leader slots may be full.");
        }
        return true;
    }

    private boolean handleDemote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("\u00A7cUsage: /coven demote <player>");
            return true;
        }

        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null || !coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders can demote members.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        CovenRank newRank = plugin.getCovenManager().demote(player, target);
        if (newRank != null) {
            player.sendMessage("\u00A7aDemoted " + target.getName() + " to \u00A7f" + newRank.getTitle());
        } else {
            player.sendMessage("\u00A7cCannot demote further.");
        }
        return true;
    }

    private boolean handleTransfer(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("\u00A7cUsage: /coven transfer <player>");
            return true;
        }

        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        if (coven == null || !coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cOnly leaders can transfer leadership.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        if (!coven.isMember(target.getUniqueId())) {
            player.sendMessage("\u00A7c" + target.getName() + " is not in your coven.");
            return true;
        }

        if (plugin.getCovenManager().transferLeadership(player, target)) {
            player.sendMessage("\u00A7aLeadership transferred to \u00A7e" + target.getName() + "\u00A7a.");
        } else {
            player.sendMessage("\u00A7cFailed to transfer leadership.");
        }
        return true;
    }

    private void sendHelp(Player player) {
        CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
        boolean isLeader = coven != null && coven.isLeader(player.getUniqueId());

        player.sendMessage("\u00A75\u00A7l--- Coven Commands ---");
        player.sendMessage("\u00A77/coven create <name> \u00A7f- Create a new coven");
        player.sendMessage("\u00A77/coven invite <player> \u00A7f- Invite a player");
        player.sendMessage("\u00A77/coven accept \u00A7f- Accept an invite");
        player.sendMessage("\u00A77/coven leave \u00A7f- Leave your coven");
        player.sendMessage("\u00A77/coven info \u00A7f- Show coven info");
        player.sendMessage("\u00A77/coven list \u00A7f- List all covens");
        if (isLeader) {
            player.sendMessage("\u00A77/coven kick <player> \u00A7f- Kick a member");
            player.sendMessage("\u00A77/coven claim \u00A7f- Claim chunk");
            player.sendMessage("\u00A77/coven unclaim \u00A7f- Unclaim chunk");
            player.sendMessage("\u00A77/coven chunks \u00A7f- List claimed chunks");
            player.sendMessage("\u00A77/coven setrank <player> <rank> \u00A7f- Set rank");
            player.sendMessage("\u00A77/coven promote <player> \u00A7f- Promote member");
            player.sendMessage("\u00A77/coven demote <player> \u00A7f- Demote member");
            player.sendMessage("\u00A77/coven transfer <player> \u00A7f- Transfer leadership");
            player.sendMessage("\u00A77/coven disband \u00A7f- Disband the coven");
        }
        player.sendMessage("\u00A75\u00A7l--- End Help ---");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();

        if (args.length == 1) {
            List<String> cmds = new ArrayList<>(Arrays.asList("create", "invite", "accept", "leave",
                    "info", "list", "chunks"));
            CovenData coven = plugin.getCovenManager().getCovenForMember(player.getUniqueId());
            if (coven != null && coven.isLeader(player.getUniqueId())) {
                cmds.addAll(Arrays.asList("kick", "claim", "unclaim", "setrank", "promote", "demote",
                        "transfer", "disband"));
            }
            return cmds.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setrank")) {
            return Arrays.asList("priest", "priestess", "council", "initiate").stream()
                    .filter(r -> r.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
