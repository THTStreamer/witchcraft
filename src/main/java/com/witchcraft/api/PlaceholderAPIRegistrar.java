package com.witchcraft.api;

import com.witchcraft.Witchcraft;
import org.bukkit.Bukkit;

/**
 * Safely registers PlaceholderAPI expansion using reflection
 * so the class is only loaded when PlaceholderAPI is present.
 */
public class PlaceholderAPIRegistrar {

    /**
     * Attempts to register the PlaceholderAPI expansion via reflection.
     * This avoids direct class references that would fail if PlaceholderAPI is absent.
     *
     * @param plugin the Witchcraft plugin instance
     */
    public static void register(Witchcraft plugin) {
        try {
            // Check if PlaceholderAPI is loaded
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return;

            // Load the expansion class via reflection to avoid NoClassDefFoundError
            Class<?> expansionClass = Class.forName(
                    "com.witchcraft.api.WitchcraftPlaceholderExpansion");

            // Create instance using constructor that takes Witchcraft plugin
            Object expansion = expansionClass.getConstructor(Witchcraft.class).newInstance(plugin);

            // Call register() method
            expansionClass.getMethod("register").invoke(expansion);

            plugin.getLogger().info("PlaceholderAPI expansion registered.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("PlaceholderAPI expansion class not found (PlaceholderAPI not installed?).");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register PlaceholderAPI expansion: " + e.getMessage());
        }
    }
}
