package com.witchcraft;

import com.witchcraft.book.GuideBookBuilder;
import com.witchcraft.book.SpellBookManager;
import com.witchcraft.commands.WitchcraftCommand;
import com.witchcraft.config.ConfigManager;
import com.witchcraft.core.ArcaneExhaustion;
import com.witchcraft.core.SpellRegistry;
import com.witchcraft.coven.CovenManager;
import com.witchcraft.coven.CovenSpellRegistry;
import com.witchcraft.data.DataManager;
import com.witchcraft.incantation.IncantationManager;
import com.witchcraft.ritual.RitualManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

/**
 * Main plugin class for Witchcraft.
 * An immersive magical progression system for Minecraft.
 */
public final class Witchcraft extends JavaPlugin {

    private static Witchcraft instance;

    private ConfigManager configManager;
    private DataManager dataManager;
    private SpellRegistry spellRegistry;
    private RitualManager ritualManager;
    private IncantationManager incantationManager;
    private ArcaneExhaustion arcaneExhaustion;
    private GuideBookBuilder guideBookBuilder;
    private SpellBookManager spellBookManager;
    private CovenManager covenManager;
    private CovenSpellRegistry covenSpellRegistry;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Initialize managers in dependency order
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.spellRegistry = new SpellRegistry(this);
        this.arcaneExhaustion = new ArcaneExhaustion(this);
        this.incantationManager = new IncantationManager(this);
        this.ritualManager = new RitualManager(this);
        this.guideBookBuilder = new GuideBookBuilder(this);
        this.spellBookManager = new SpellBookManager();
        this.covenManager = new CovenManager(this);
        this.covenSpellRegistry = new CovenSpellRegistry(this);

        // Register commands
        WitchcraftCommand command = new WitchcraftCommand(this);
        var cmdMap = Bukkit.getPluginCommand("witchcraft");
        if (cmdMap != null) {
            cmdMap.setExecutor(command);
            cmdMap.setTabCompleter(command);
        } else {
            // Fallback: register via reflection-based command map access for Paper
            try {
                var getMap = Bukkit.getServer().getClass().getMethod("getCommandMap");
                var commandMap = (org.bukkit.command.CommandMap) getMap.invoke(Bukkit.getServer());
                var cmd = new org.bukkit.command.Command("witchcraft", "Main Witchcraft command", "/witchcraft <subcommand>", List.of("wc")) {
                    @Override
                    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                        return command.onCommand(sender, this, commandLabel, args);
                    }
                    @Override
                    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                        return command.onTabComplete(sender, this, alias, args);
                    }
                };
                commandMap.register("witchcraft", cmd);
            } catch (Exception e) {
                getLogger().severe("Failed to register witchcraft command: " + e.getMessage());
            }
        }

        // Register coven command
        var covenCommand = new com.witchcraft.coven.CovenCommand(this);
        try {
            var getMap = Bukkit.getServer().getClass().getMethod("getCommandMap");
            var commandMap = (org.bukkit.command.CommandMap) getMap.invoke(Bukkit.getServer());
            var cmd = new org.bukkit.command.Command("coven", "Coven management command", "/coven <subcommand>", List.of()) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return covenCommand.onCommand(sender, this, commandLabel, args);
                }
                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return covenCommand.onTabComplete(sender, this, alias, args);
                }
            };
            commandMap.register("witchcraft", cmd);
        } catch (Exception e) {
            getLogger().severe("Failed to register coven command: " + e.getMessage());
        }

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.ritual.RitualListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.incantation.IncantationListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.spells.protection.ProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.coven.CovenSpellListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.coven.CovenChunkListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.book.SpellBookLootListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.book.SpellBookTradeListener(this), this);

        // Register PlaceholderAPI expansion
        try {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new com.witchcraft.api.WitchcraftPlaceholderExpansion(this).register();
                getLogger().info("PlaceholderAPI expansion registered.");
            }
        } catch (NoClassDefFoundError e) {
            getLogger().warning("PlaceholderAPI expansion could not be loaded.");
        }

        // Start periodic save task
        startSaveTask();

        // Start ritual tick processor
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (ritualManager != null) {
                ritualManager.processRituals();
            }
        }, 1L, 1L);

        getLogger().info("Witchcraft has been enabled. The ancient arts awaken...");
    }

    @Override
    public void onDisable() {
        // Save all data
        if (this.dataManager != null) {
            this.dataManager.saveAll();
        }
        getLogger().info("Witchcraft has been disabled. The spirits rest.");
    }

    /**
     * Starts the periodic data save task.
     */
    private void startSaveTask() {
        int interval = configManager.getConfig().getInt("storage.save-interval", 6000);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                if (dataManager != null) {
                    dataManager.saveAll();
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to save Witchcraft data", e);
            }
        }, interval, interval);
    }

    /**
     * Gives the Witchcraft guide book to a player.
     *
     * @param player the player to give the book to
     */
    public void giveGuideBook(Player player) {
        player.getInventory().addItem(guideBookBuilder.buildBook());
        player.sendMessage(configManager.getMessage("guide-received",
                "&aYou have received the Witchcraft Guide."));
    }

    /**
     * Checks if a player is under Arcane Exhaustion.
     *
     * @param player the player to check
     * @return true if the player is exhausted
     */
    public boolean isExhausted(Player player) {
        return arcaneExhaustion.isExhausted(player.getUniqueId());
    }

    /**
     * Returns the singleton instance of the plugin.
     *
     * @return the plugin instance
     */
    public static Witchcraft getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public SpellRegistry getSpellRegistry() {
        return spellRegistry;
    }

    public RitualManager getRitualManager() {
        return ritualManager;
    }

    public IncantationManager getIncantationManager() {
        return incantationManager;
    }

    public ArcaneExhaustion getArcaneExhaustion() {
        return arcaneExhaustion;
    }

    public GuideBookBuilder getGuideBookBuilder() {
        return guideBookBuilder;
    }

    public CovenManager getCovenManager() {
        return covenManager;
    }

    public CovenSpellRegistry getCovenSpellRegistry() {
        return covenSpellRegistry;
    }

    public SpellBookManager getSpellBookManager() {
        return spellBookManager;
    }
}
