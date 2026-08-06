package com.witchcraft.config;

import com.witchcraft.Witchcraft;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages plugin configuration and messages.
 */
public class ConfigManager {

    private final Witchcraft plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private final Map<String, String> messageCache = new HashMap<>();

    public ConfigManager(Witchcraft plugin) {
        this.plugin = plugin;
        loadConfig();
        loadMessages();
    }

    /**
     * Reloads all configuration files.
     */
    public void reload() {
        loadConfig();
        loadMessages();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);

        // Load defaults
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messages.setDefaults(defaultMessages);
        }

        // Build message cache
        messageCache.clear();
        flattenMessages("", messages, messageCache);
    }

    /**
     * Recursively flattens nested message configuration into dot-notation keys.
     */
    private void flattenMessages(String prefix, org.bukkit.configuration.ConfigurationSection section,
                                  Map<String, String> cache) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            Object value = section.get(key);
            if (value instanceof org.bukkit.configuration.ConfigurationSection sub) {
                flattenMessages(fullKey, sub, cache);
            } else if (value != null) {
                cache.put(fullKey, ChatColor.translateAlternateColorCodes('&', String.valueOf(value)));
            }
        }
    }

    /**
     * Gets the main plugin configuration.
     *
     * @return the config file configuration
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Gets a message by its key, returning the default if not found.
     *
     * @param key the message key
     * @return the translated message
     */
    public String getMessage(String key) {
        String prefix = messageCache.getOrDefault("general.prefix",
                ChatColor.translateAlternateColorCodes('&', "&5[Witchcraft] &r"));
        String message = messageCache.get(key);
        if (message == null) {
            return prefix + ChatColor.RED + "Missing message: " + key;
        }
        return prefix + message;
    }

    /**
     * Gets a message with placeholders replaced.
     *
     * @param key          the message key
     * @param placeholders pairs of placeholder->value
     * @return the translated message with placeholders
     */
    public String getMessage(String key, String... placeholders) {
        String msg = getMessage(key);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }
        return msg;
    }

    /**
     * Gets a raw message without the prefix.
     *
     * @param key the message key
     * @return the message without prefix
     */
    public String getRawMessage(String key) {
        String message = messageCache.get(key);
        if (message == null) {
            return ChatColor.RED + "Missing message: " + key;
        }
        return message;
    }

    /**
     * Gets the guide book page content.
     *
     * @return the guide book pages
     */
    public java.util.List<String> getGuidePages() {
        var pageList = messages.getStringList("guide.pages");
        if (pageList.isEmpty()) {
            return getDefaultGuidePages();
        }
        return pageList.stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toList();
    }

    private java.util.List<String> getDefaultGuidePages() {
        return java.util.List.of(
                "\u00A75\u00A7lWitchcraft Guide\u00A70\n\n\u00A77This book will guide you through\nthe ancient art of folk magic.\n\n\u00A77Magic is not instant. It requires\npreparation, knowledge, and respect\nfor the unseen forces."
        );
    }
}
