package com.witchcraft.commands;

import com.witchcraft.Witchcraft;
import com.witchcraft.book.LoreBookManager;
import com.witchcraft.book.SpellBookData;
import com.witchcraft.book.SpellBookManager;
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
 * Admin command handler: /witchcraft admin <subcommand>
 * Requires witchcraft.admin permission.
 */
public class AdminCommand implements CommandExecutor, TabCompleter {

    private final Witchcraft plugin;

    public AdminCommand(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            sender.sendMessage("\u00A7cYou don't have permission to use admin commands.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        return switch (subCommand) {
            case "givebook" -> handleGiveBook(sender, args);
            case "learn" -> handleLearn(sender, args);
            case "unlearn" -> handleUnlearn(sender, args);
            case "purge" -> handlePurge(sender, args);
            case "exhaust" -> handleExhaust(sender, args);
            case "targetpaper" -> handleTargetPaper(sender, args);
            case "debug" -> handleDebug(sender, args);
            case "givegrimoire" -> handleGiveGrimoire(sender, args);
            case "givelore" -> handleGiveLore(sender, args);
            case "giveritual" -> handleGiveGrimoire(sender, args);
            case "help" -> { sendHelp(sender); yield true; }
            default -> {
                sender.sendMessage("\u00A7cUnknown admin subcommand. Use /witchcraft admin help");
                yield true;
            }
        };
    }

    private boolean handleGiveBook(CommandSender sender, String[] args) {
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
        if (args.length < 3) {
            sender.sendMessage("\u00A7cUsage: /witchcraft admin learn <player> <incantation_id>");
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
        if (args.length < 3) {
            sender.sendMessage("\u00A7cUsage: /witchcraft admin unlearn <player> <incantation_id>");
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
        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft admin purge <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        plugin.getArcaneExhaustion().removeExhaustion(target.getUniqueId());
        plugin.getIncantationManager().getCooldowns().clearCooldowns(target.getUniqueId());
        plugin.getRitualManager().cancelRituals(target.getUniqueId());

        sender.sendMessage("\u00A7aAll magical effects cleared for " + target.getName());
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
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

    private boolean handleExhaust(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft admin exhaust <player>");
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
        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft admin targetpaper <player>");
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

    private boolean handleGiveGrimoire(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft admin givegrimoire <player> [book_id|list|random]");
            sender.sendMessage("\u00A77Use \u00A7flist \u00A77to see all spell + lore books.");
            sender.sendMessage("\u00A77Also: \u00A7f/witchcraft admin givelore <player> [lore_id|list]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }

        SpellBookManager bookManager = plugin.getSpellBookManager();
        LoreBookManager loreManager = plugin.getLoreBookManager();

        // List all available books (spell + lore)
        if (args.length > 2 && args[2].equalsIgnoreCase("list")) {
            sender.sendMessage("\u00A75\u00A7l--- Available Spell Books (" + bookManager.getBookCount() + ") ---");
            for (SpellBookData book : bookManager.getAllBooks()) {
                sender.sendMessage("\u00A77- \u00A7f" + book.getSpellId() + " \u00A77(" + book.getCategory() + ") " + book.getTitle());
            }
            if (loreManager != null) {
                sender.sendMessage("\u00A75\u00A7l--- Available Lore Books (" + loreManager.getLoreCount() + ") ---");
                for (SpellBookData lore : loreManager.getAllLoreBooks()) {
                    sender.sendMessage("\u00A77- \u00A7f" + lore.getSpellId() + " \u00A77(Lore) " + lore.getTitle());
                }
            }
            sender.sendMessage("\u00A77Total spell books: \u00A7f" + bookManager.getBookCount() + " \u00A77| Lore: \u00A7f" + (loreManager != null ? loreManager.getLoreCount() : 0));
            sender.sendMessage("\u00A75\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
            return true;
        }

        // Try lore list alias
        if (args.length > 2 && args[2].equalsIgnoreCase("lore_list")) {
            if (loreManager == null) {
                sender.sendMessage("\u00A7cLore manager not available.");
                return true;
            }
            sender.sendMessage("\u00A75\u00A7l--- Available Lore Books ---");
            for (SpellBookData lore : loreManager.getAllLoreBooks()) {
                sender.sendMessage("\u00A77- \u00A7f" + lore.getSpellId() + " \u00A7f" + lore.getTitle());
            }
            return true;
        }

        // Specific book or random
        if (args.length > 2) {
            String bookId = args[2];
            // Try spell book first
            ItemStack book = bookManager.createBookItem(bookId);
            if (book != null) {
                target.getInventory().addItem(book);
                sender.sendMessage("\u00A7aSpell book '\u00A7f" + bookId + "\u00A7a' given to " + target.getName());
                return true;
            }
            // Try lore book
            if (loreManager != null) {
                ItemStack lore = loreManager.createLoreBookItem(bookId);
                if (lore != null) {
                    target.getInventory().addItem(lore);
                    sender.sendMessage("\u00A7aLore book '\u00A7f" + bookId + "\u00A7a' given to " + target.getName());
                    return true;
                }
            }
            sender.sendMessage("\u00A7cBook not found: " + bookId + ". Use /witchcraft admin givegrimoire <player> list");
            return true;
        } else {
            // Random spell book
            String randomId = bookManager.getRandomBookId();
            ItemStack book = bookManager.createBookItem(randomId);
            if (book != null) {
                target.getInventory().addItem(book);
                sender.sendMessage("\u00A7aRandom spell book '\u00A7f" + randomId + "\u00A7a' given to " + target.getName());
            }
        }
        return true;
    }

    private boolean handleGiveLore(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("\u00A7cUsage: /witchcraft admin givelore <player> [lore_id|list]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("\u00A7cPlayer not found.");
            return true;
        }
        LoreBookManager loreManager = plugin.getLoreBookManager();
        if (loreManager == null) {
            sender.sendMessage("\u00A7cLore manager not available.");
            return true;
        }
        if (args.length > 2 && args[2].equalsIgnoreCase("list")) {
            sender.sendMessage("\u00A75\u00A7l--- Available Lore Books ---");
            for (SpellBookData lore : loreManager.getAllLoreBooks()) {
                sender.sendMessage("\u00A77- \u00A7f" + lore.getSpellId() + " \u00A7f" + lore.getTitle());
            }
            sender.sendMessage("\u00A77Total: \u00A7f" + loreManager.getLoreCount());
            return true;
        }
        if (args.length > 2) {
            String loreId = args[2];
            ItemStack lore = loreManager.createLoreBookItem(loreId);
            if (lore == null) {
                sender.sendMessage("\u00A7cLore book not found: " + loreId + ". Use /witchcraft admin givelore <player> list");
                return true;
            }
            target.getInventory().addItem(lore);
            sender.sendMessage("\u00A7aLore book '\u00A7f" + loreId + "\u00A7a' given to " + target.getName());
        } else {
            String randomId = loreManager.getRandomLoreId();
            ItemStack lore = loreManager.createLoreBookItem(randomId);
            if (lore != null) {
                target.getInventory().addItem(lore);
                sender.sendMessage("\u00A7aRandom lore book '\u00A7f" + randomId + "\u00A7a' given to " + target.getName());
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("\u00A75\u00A7l--- Witchcraft Admin Commands ---");
        sender.sendMessage("\u00A77/witchcraft admin givebook [player] \u00A7f- Give guide book");
        sender.sendMessage("\u00A77/witchcraft admin learn <player> <id> \u00A7f- Teach incantation");
        sender.sendMessage("\u00A77/witchcraft admin unlearn <player> <id> \u00A7f- Remove incantation");
        sender.sendMessage("\u00A77/witchcraft admin purge <player> \u00A7f- Clear all magic effects");
        sender.sendMessage("\u00A77/witchcraft admin exhaust <player> \u00A7f- Apply exhaustion");
        sender.sendMessage("\u00A77/witchcraft admin targetpaper <player> \u00A7f- Create target paper");
        sender.sendMessage("\u00A77/witchcraft admin givegrimoire <player> [id|list] \u00A7f- Give spell/lore book");
        sender.sendMessage("\u00A77/witchcraft admin givelore <player> [id|list] \u00A7f- Give lore book");
        sender.sendMessage("\u00A77/witchcraft admin debug \u00A7f- Show debug info");
        sender.sendMessage("\u00A75\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("witchcraft.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("givebook", "learn", "unlearn", "purge", "exhaust",
                            "targetpaper", "debug", "givegrimoire", "givelore", "help").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "givebook", "learn", "unlearn", "purge", "exhaust", "targetpaper", "givegrimoire", "givelore", "giveritual" -> {
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

        if (args.length == 3 && args[0].equalsIgnoreCase("unlearn")) {
            return plugin.getIncantationManager().getAllIncantations().stream()
                    .map(i -> i.getId())
                    .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("givegrimoire")) {
            List<String> bookIds = new ArrayList<>();
            bookIds.add("list");
            bookIds.add("lore_list");
            bookIds.add("random");
            bookIds.addAll(plugin.getSpellBookManager().getAllBooks().stream()
                    .map(SpellBookData::getSpellId)
                    .collect(Collectors.toList()));
            if (plugin.getLoreBookManager() != null) {
                bookIds.addAll(plugin.getLoreBookManager().getAllLoreBooks().stream()
                        .map(SpellBookData::getSpellId)
                        .collect(Collectors.toList()));
            }
            return bookIds.stream()
                    .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("givelore") || args[0].equalsIgnoreCase("giveritual"))) {
            List<String> loreIds = new ArrayList<>();
            loreIds.add("list");
            loreIds.add("random");
            if (plugin.getLoreBookManager() != null) {
                loreIds.addAll(plugin.getLoreBookManager().getAllLoreBooks().stream()
                        .map(SpellBookData::getSpellId)
                        .collect(Collectors.toList()));
            }
            // also allow spell ids for giveritual alias
            if (args[0].equalsIgnoreCase("giveritual")) {
                loreIds.addAll(plugin.getSpellBookManager().getAllBooks().stream()
                        .map(SpellBookData::getSpellId)
                        .collect(Collectors.toList()));
            }
            return loreIds.stream()
                    .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
