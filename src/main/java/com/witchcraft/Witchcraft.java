package com.witchcraft;

import com.witchcraft.book.GuideBookBuilder;
import com.witchcraft.commands.WitchcraftCommand;
import com.witchcraft.config.ConfigManager;
import com.witchcraft.core.ArcaneExhaustion;
import com.witchcraft.core.SpellRegistry;
import com.witchcraft.data.DataManager;
import com.witchcraft.incantation.IncantationManager;
import com.witchcraft.ritual.RitualManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

        // Register commands
        WitchcraftCommand command = new WitchcraftCommand(this);
        getCommand("witchcraft").setExecutor(command);
        getCommand("witchcraft").setTabCompleter(command);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.ritual.RitualListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.incantation.IncantationListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.witchcraft.spells.protection.ProtectionListener(this), this);

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
}
