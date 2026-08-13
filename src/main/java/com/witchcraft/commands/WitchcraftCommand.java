package com.witchcraft.commands;

import com.witchcraft.Witchcraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main command handler for the Witchcraft plugin.
 * Routes /witchcraft admin to AdminCommand, keeps reload/list/help.
 */
public class WitchcraftCommand implements CommandExecutor, TabCompleter {

    private final Witchcraft plugin;
    private final AdminCommand adminCommand;

    public WitchcraftCommand(Witchcraft plugin) {
        this.plugin = plugin;
        this.adminCommand = new AdminCommand(plugin);
    }

    public AdminCommand getAdminCommand() {
        return adminCommand;
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
            case "list" -> handleList(sender);
            case "admin" -> adminCommand.onCommand(sender, command, label, shiftArgs(args));
            case "help" -> { sendHelp(sender); yield true; }
            default -> {
                sender.sendMessage(plugin.getConfigManager().getMessage("general.prefix") +
                        "\u00A7cUnknown subcommand. Use /witchcraft help");
                yield true;
            }
        };
    }

    private String[] shiftArgs(String[] args) {
        if (args.length <= 1) return new String[0];
        return Arrays.copyOfRange(args, 1, args.length);
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

    private boolean handleList(CommandSender sender) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage("\u00A7cYou don't have permission.");
            return true;
        }

        sender.sendMessage("\u00A75\u00A7l--- Registered Spells ---");
        for (var spell : plugin.getSpellRegistry().getAllSpells()) {
            sender.sendMessage("\u00A77- \u00A7f" + spell.getId() + " \u00A77(" +
                    spell.getCategory().getDisplayName() + ")");
        }
        sender.sendMessage("\u00A75\u00A7l--- End Spells ---");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("\u00A75\u00A7l--- Witchcraft Commands ---");
        sender.sendMessage("\u00A77/witchcraft help \u00A7f- Show this help");
        sender.sendMessage("\u00A77/witchcraft reload \u00A7f- Reload configuration");
        sender.sendMessage("\u00A77/witchcraft list \u00A7f- List all spells");
        sender.sendMessage("\u00A77/witchcraft admin <sub> \u00A7f- Admin commands (witchcraft.admin)");
        sender.sendMessage("\u00A75\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        sender.sendMessage("\u00A77Use \u00A7d/witchcraft admin help \u00A77for admin commands.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> commands = new ArrayList<>(Arrays.asList("help", "reload", "list"));
            if (sender.hasPermission("witchcraft.admin")) {
                commands.add("admin");
            }
            return commands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("witchcraft.admin")) {
            return adminCommand.onTabComplete(sender, command, label, shiftArgs(args));
        }

        return new ArrayList<>();
    }
}
