package com.witchcraft.commands;

import com.witchcraft.Witchcraft;
import com.witchcraft.core.Spell;
import com.witchcraft.core.SpellCategory;
import com.witchcraft.util.TargetPaper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main command handler for the Witchcraft plugin.
 */
public class WitchcraftCommand implements CommandExecutor, TabCompleter {

    private final Witchcraft plugin;

    public WitchcraftCommand(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        return switch (subCommand) {
            case "reload" -> handleReload(sender);
            case "givebook" -> handleGiveBook(sender, args);
            case "learn" -> handleLearn(sender, args);
            case "unlearn" -> handleUnlearn(sender, args);
            case "purge" -> handlePurge(sender, args);
            case "debug" -> handleDebug(sender, args);
            case "list" -> handleList(sender);
            case "exhaust" -> handleExhaust(sender, args);
            case "targetpaper" -> handleTargetPaper(sender, args);
            case "help" -> { sendHelp(sender); yield true; }
            default -> {
                sender.sendMessage(plugin.getConfigManager().getMessage("general.prefix") +
                        "\u00A7cUnknown subcommand. Use /witchcraft help");
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("witchcraft.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        plugin.getConfigManager().reload();
        sender.sendMessage(plugin.getConfigManager().getMessage("general.reload-success"));
        return true;
    }

    private boolean handleGiveBook(CommandSender sender, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("\u00A7cPlayer not found.");
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage("\u00A7cPlease specify a player.");
            return true;
        }

        plugin.giveGuideBook(target);
        sender.sendMessage("\u00A7aGuide book given to " + target.getName());
        return true;
    }

    private boolean handleLearn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("\u00A7cUsage: /witchcraft learn <player> <incantation_id>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        String incantationId = args[2];
        boolean learned = plugin.getIncantationManager().learnIncantation(
                target.getUniqueId(), incantationId);

        if (learned) {
            sender.sendMessage("\u00A7a" + target.getName() + " learned incantation: " + incantationId);
            target.sendMessage(plugin.getConfigManager().getMessage("incantation.learned",
                    "%spell%", incantationId));
        } else {
            sender.sendMessage("\u00A7cFailed to teach incantation. Check ID and if already known.");
        }
        return true;
    }

    private boolean handleUnlearn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("\u00A7cUsage: /witchcraft unlearn <player> <incantation_id>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        String incantationId = args[2];
        var data = plugin.getDataManager().getPlayerData(target.getUniqueId());
        data.unlearnIncantation(incantationId);
        sender.sendMessage("\u00A7a" + target.getName() + " unlearned incantation: " + incantationId);
        return true;
    }

    private boolean handlePurge(CommandSender sender, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft purge <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        // Clear all magical effects
        plugin.getArcaneExhaustion().removeExhaustion(target.getUniqueId());
        plugin.getIncantationManager().getCooldowns().clearCooldowns(target.getUniqueId());
        plugin.getRitualManager().cancelRituals(target.getUniqueId());

        sender.sendMessage("\u00A7aAll magical effects cleared for " + target.getName());
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("witchcraft.debug")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        if (sender instanceof Player player) {
            sender.sendMessage("\u00A75\u00A7l--- Witchcraft Debug Info ---");
            sender.sendMessage("\u00A77Spells registered: \u00A7f" +
                    plugin.getSpellRegistry().getAllSpells().size());
            sender.sendMessage("\u00A77Incantations registered: \u00A7f" +
                    plugin.getIncantationManager().getAllIncantations().size());
            sender.sendMessage("\u00A77Exhausted: \u00A7f" +
                    plugin.getArcaneExhaustion().isExhausted(player.getUniqueId()));
            sender.sendMessage("\u00A77Learned incantations: \u00A7f" +
                    plugin.getDataManager().getPlayerData(player.getUniqueId())
                            .getLearnedIncantations().size());
            sender.sendMessage("\u00A75\u00A7l--- End Debug ---");
        } else {
            sender.sendMessage("\u00A77Spells registered: " +
                    plugin.getSpellRegistry().getAllSpells().size());
            sender.sendMessage("\u00A77Incantations registered: " +
                    plugin.getIncantationManager().getAllIncantations().size());
        }
        return true;
    }

    private boolean handleList(CommandSender sender) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        sender.sendMessage("\u00A75\u00A7l--- Registered Spells ---");
        for (Spell spell : plugin.getSpellRegistry().getAllSpells()) {
            sender.sendMessage("\u00A77- \u00A7f" + spell.getId() + " \u00A77(" +
                    spell.getCategory().getDisplayName() + ")");
        }
        sender.sendMessage("\u00A75\u00A7l--- End Spells ---");
        return true;
    }

    private boolean handleExhaust(CommandSender sender, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft exhaust <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        plugin.getArcaneExhaustion().applyExhaustion(target.getUniqueId());
        sender.sendMessage("\u00A7aArcane Exhaustion applied to " + target.getName());
        return true;
    }

    private boolean handleTargetPaper(CommandSender sender, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft targetpaper <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        if (sender instanceof Player player) {
            ItemStack paper = TargetPaper.create(plugin, target.getName(), target.getUniqueId());
            player.getInventory().addItem(paper);
            player.sendMessage("\u00A7aTarget paper for \u00A7c" + target.getName() + " \u00A7agiven.");
        } else {
            sender.sendMessage("\u00A7cThis command can only be used by players.");
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("\u00A75\u00A7l--- Witchcraft Commands ---");
        sender.sendMessage("\u00A77/witchcraft help \u00A7f- Show this help");
        sender.sendMessage("\u00A77/witchcraft reload \u00A7f- Reload configuration");
        sender.sendMessage("\u00A77/witchcraft givebook [player] \u00A7f- Give guide book");
        sender.sendMessage("\u00A77/witchcraft learn <player> <id> \u00A7f- Teach incantation");
        sender.sendMessage("\u00A77/witchcraft unlearn <player> <id> \u00A7f- Remove incantation");
        sender.sendMessage("\u00A77/witchcraft purge <player> \u00A7f- Clear all magic effects");
        sender.sendMessage("\u00A77/witchcraft list \u00A7f- List all spells");
        sender.sendMessage("\u00A77/witchcraft exhaust <player> \u00A7f- Apply exhaustion");
        sender.sendMessage("\u00A77/witchcraft targetpaper <player> \u00A7f- Create target paper");
        sender.sendMessage("\u00A77/witchcraft debug \u00A7f- Show debug info");
        sender.sendMessage("\u00A75\u00A7l--- End Help ---");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> commands = new ArrayList<>(Arrays.asList("help", "reload", "givebook", "learn", "unlearn",
                            "purge", "list", "exhaust", "debug"));
            if (sender.hasPermission("witchcraft.admin")) {
                commands.add("targetpaper");
            }
            return commands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "givebook", "learn", "unlearn", "purge", "exhaust", "targetpaper" -> {
                    if (args[0].equalsIgnoreCase("targetpaper") && !sender.hasPermission("witchcraft.admin")) {
                        return new ArrayList<>();
                    }
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("learn")) {
            return plugin.getIncantationManager().getAllIncantations().stream()
                    .map(i -> i.getId())
                    .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
