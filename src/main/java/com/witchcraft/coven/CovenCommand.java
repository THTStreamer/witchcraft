package com.witchcraft.coven;

import com.witchcraft.Witchcraft;
import com.witchcraft.data.CovenData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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

        player.sendMessage("\u00A7aCoven \u00A7e" + name + " \u00A7ahas been formed!");
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
            player.sendMessage("\u00A7cOnly the coven leader can invite members.");
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

        player.sendMessage("\u00A7aYou have joined coven: \u00A7e" + coven.getName());

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

        if (coven.isLeader(player.getUniqueId())) {
            player.sendMessage("\u00A7cYou are the leader. Use /coven disband instead, or transfer leadership first.");
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
            player.sendMessage("\u00A7cOnly the leader can disband the coven.");
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
        Player leader = Bukkit.getPlayer(coven.getLeaderId());

        player.sendMessage("\u00A75\u00A7l--- Coven Info ---");
        player.sendMessage("\u00A77Name: \u00A7e" + coven.getName());
        player.sendMessage("\u00A77Leader: \u00A7f" + (leader != null ? leader.getName() : "Unknown"));
        player.sendMessage("\u00A77Members: \u00A7f" + coven.getSize());
        player.sendMessage("\u00A77Online: \u00A7a" + online.size());
        player.sendMessage("\u00A75\u00A7l--- End Info ---");
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
            player.sendMessage("\u00A7cOnly the leader can kick members.");
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
            player.sendMessage("\u00A7cOnly the coven leader can claim chunks.");
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
            player.sendMessage("\u00A7cOnly the coven leader can unclaim chunks.");
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

    private void sendHelp(Player player) {
        player.sendMessage("\u00A75\u00A7l--- Coven Commands ---");
        player.sendMessage("\u00A77/coven create <name> \u00A7f- Create a new coven");
        player.sendMessage("\u00A77/coven invite <player> \u00A7f- Invite a player");
        player.sendMessage("\u00A77/coven accept \u00A7f- Accept an invite");
        player.sendMessage("\u00A77/coven leave \u00A7f- Leave your coven");
        player.sendMessage("\u00A77/coven kick <player> \u00A7f- Kick a member (leader)");
        player.sendMessage("\u00A77/coven disband \u00A7f- Disband the coven (leader)");
        player.sendMessage("\u00A77/coven claim \u00A7f- Claim chunk (leader)");
        player.sendMessage("\u00A77/coven unclaim \u00A7f- Unclaim chunk (leader)");
        player.sendMessage("\u00A77/coven chunks \u00A7f- List claimed chunks");
        player.sendMessage("\u00A77/coven info \u00A7f- Show coven info");
        player.sendMessage("\u00A77/coven list \u00A7f- List all covens");
        player.sendMessage("\u00A75\u00A7l--- End Help ---");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "invite", "accept", "leave", "kick", "disband",
                            "claim", "unclaim", "chunks", "info", "list")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && Arrays.asList("invite", "kick").contains(args[0].toLowerCase())) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
