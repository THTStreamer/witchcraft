package com.witchcraft.commands;

import com.witchcraft.Witchcraft;
import com.witchcraft.coven.CovenSpell;
import com.witchcraft.core.Ingredient;
import com.witchcraft.incantation.Incantation;
import com.witchcraft.ritual.RitualRecipe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-game grimoire command for browsing spells, incantations, and ritual recipes.
 */
public class GrimoireCommand implements CommandExecutor, TabCompleter {

    private static final int LINES_PER_PAGE = 8;

    private final Witchcraft plugin;

    public GrimoireCommand(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00A7cThis command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("witchcraft.grimoire")) {
            player.sendMessage("\u00A7cYou don't have permission to use the grimoire.");
            return true;
        }

        if (args.length == 0) {
            sendGrimoireHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        return switch (subCommand) {
            case "incantations" -> handleIncantations(player, args);
            case "rituals" -> handleRituals(player, args);
            case "coven" -> handleCovenSpells(player, args);
            case "help" -> { sendGrimoireHelp(player); yield true; }
            default -> {
                player.sendMessage("\u00A7cUnknown grimoire command. Use /grimoire help");
                yield true;
            }
        };
    }

    private void sendGrimoireHelp(Player player) {
        player.sendMessage("\u00A75\u00A7l\u2550\u2550\u2550 The Grimoire \u2550\u2550\u2550");
        player.sendMessage("\u00A77Your guide to the dark arts of Witchcraft.");
        player.sendMessage("");
        player.sendMessage("\u00A7d/grimoire incantations [page] \u00A77- Browse incantations");
        player.sendMessage("\u00A7d/grimoire rituals [page] \u00A77- Browse ritual recipes");
        player.sendMessage("\u00A7d/grimoire coven [page] \u00A77- Browse coven spells");
        player.sendMessage("\u00A7d/grimoire help \u00A77- Show this help");
        player.sendMessage("\u00A75\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
    }

    private boolean handleIncantations(Player player, String[] args) {
        List<Incantation> all = new ArrayList<>(plugin.getIncantationManager().getAllIncantations());
        int page = getPage(args, 1);
        int totalPages = (int) Math.ceil((double) all.size() / LINES_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        player.sendMessage("\u00A75\u00A7l\u2550\u2550\u2550 Incantations (Page " + page + "/" + totalPages + ") \u2550\u2550\u2550");
        player.sendMessage("\u00A77Type these words in chat to cast. Hold a target paper to aim.");

        int start = (page - 1) * LINES_PER_PAGE;
        int end = Math.min(start + LINES_PER_PAGE, all.size());

        if (start >= all.size()) {
            player.sendMessage("\u00A7cNo incantations on this page.");
            return true;
        }

        for (int i = start; i < end; i++) {
            Incantation inc = all.get(i);
            boolean learned = plugin.getIncantationManager().hasLearned(player.getUniqueId(), inc.getId());
            String status = learned ? "\u00A7a[Learned]" : "\u00A7c[Unknown]";

            player.sendMessage("\u00A7d\u00A7l" + inc.getDisplayName() + " " + status);
            player.sendMessage("\u00A77  \u00A7f\"" + inc.getIncantation() + "\"");
            player.sendMessage("\u00A77  " + inc.getDescription());
        }

        sendPageFooter(player, page, totalPages, "incantations");
        return true;
    }

    private boolean handleRituals(Player player, String[] args) {
        List<RitualRecipe> all = new ArrayList<>(plugin.getRitualManager().getRecipeRegistry().getAllRecipes());
        int page = getPage(args, 1);
        int totalPages = (int) Math.ceil((double) all.size() / LINES_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        player.sendMessage("\u00A75\u00A7l\u2550\u2550\u2550 Ritual Recipes (Page " + page + "/" + totalPages + ") \u2550\u2550\u2550");
        player.sendMessage("\u00A77Combine ingredients in a cauldron to perform rituals.");

        int start = (page - 1) * LINES_PER_PAGE;
        int end = Math.min(start + LINES_PER_PAGE, all.size());

        if (start >= all.size()) {
            player.sendMessage("\u00A7cNo rituals on this page.");
            return true;
        }

        for (int i = start; i < end; i++) {
            RitualRecipe recipe = all.get(i);
            StringBuilder ingredients = new StringBuilder();
            List<Ingredient> req = recipe.getRequiredIngredients();
            for (int j = 0; j < req.size(); j++) {
                ingredients.append("\u00A7f").append(req.get(j).getDisplayName());
                if (j < req.size() - 1) ingredients.append("\u00A77, ");
            }

            String type = recipe.isCovenRitual() ?
                    "\u00A7c[Coven x" + recipe.getRequiredCovenSize() + "]" : "\u00A7a[Solo]";

            player.sendMessage("\u00A7d\u00A7l" + recipe.getDisplayName() + " " + type);
            player.sendMessage("\u00A77  Ingredients: " + ingredients);

            if (recipe.isMoonPhaseRequired()) {
                player.sendMessage("\u00A77  Moon Phase: " + recipe.getRequiredMoonPhase());
            }
            if (recipe.isWeatherRequired()) {
                player.sendMessage("\u00A77  Weather: " + recipe.getRequiredWeather());
            }
        }

        sendPageFooter(player, page, totalPages, "rituals");
        return true;
    }

    private boolean handleCovenSpells(Player player, String[] args) {
        List<CovenSpell> all = new ArrayList<>(plugin.getCovenSpellRegistry().getAllSpells());
        int page = getPage(args, 1);
        int totalPages = (int) Math.ceil((double) all.size() / LINES_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        player.sendMessage("\u00A75\u00A7l\u2550\u2550\u2550 Coven Spells (Page " + page + "/" + totalPages + ") \u2550\u2550\u2550");
        player.sendMessage("\u00A77Multi-player incantations. Each member says one line in order.");

        int start = (page - 1) * LINES_PER_PAGE;
        int end = Math.min(start + LINES_PER_PAGE, all.size());

        if (start >= all.size()) {
            player.sendMessage("\u00A7cNo coven spells on this page.");
            return true;
        }

        for (int i = start; i < end; i++) {
            CovenSpell spell = all.get(i);
            player.sendMessage("\u00A7d\u00A7l" + spell.getDisplayName());
            player.sendMessage("\u00A77  Requires: " + spell.getRequiredCovenSize() +
                    " members within " + (int) spell.getCovenRadius() + " blocks");

            List<String> lines = spell.getIncantationLines();
            for (int j = 0; j < lines.size(); j++) {
                player.sendMessage("\u00A77  Line " + (j + 1) + ": \u00A7f\"" + lines.get(j) + "\"");
            }
        }

        sendPageFooter(player, page, totalPages, "coven");
        return true;
    }

    private int getPage(String[] args, int defaultPage) {
        if (args.length > 1) {
            try {
                return Math.max(1, Integer.parseInt(args[1]));
            } catch (NumberFormatException ignored) {}
        }
        return defaultPage;
    }

    private void sendPageFooter(Player player, int current, int total, String category) {
        player.sendMessage("");
        if (current > 1) {
            player.sendMessage("\u00A7d/grimoire " + category + " " + (current - 1) + " \u00A77- Previous page");
        }
        if (current < total) {
            player.sendMessage("\u00A7d/grimoire " + category + " " + (current + 1) + " \u00A77- Next page");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("incantations", "rituals", "coven", "help").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && !args[0].equalsIgnoreCase("help")) {
            try {
                int page = Integer.parseInt(args[1]);
                return new ArrayList<>();
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }
}
