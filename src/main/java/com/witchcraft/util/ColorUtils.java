package com.witchcraft.util;

import org.bukkit.ChatColor;

/**
 * Utility class for color-related operations.
 */
public final class ColorUtils {

    private ColorUtils() {
    }

    /**
     * Translates color codes in a string.
     *
     * @param text the text to translate
     * @return the translated text
     */
    public static String translate(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Converts a hex color string to a ChatColor.
     *
     * @param hex the hex color string (e.g., "#FF0000")
     * @return the ChatColor, or WHITE if invalid
     */
    public static ChatColor hexToChatColor(String hex) {
        if (hex == null || hex.length() != 7 || !hex.startsWith("#")) {
            return ChatColor.WHITE;
        }
        try {
            int r = Integer.parseInt(hex.substring(1, 3), 16);
            int g = Integer.parseInt(hex.substring(3, 5), 16);
            int b = Integer.parseInt(hex.substring(5, 7), 16);
            return ChatColor.valueOf(getClosestColorName(r, g, b));
        } catch (NumberFormatException e) {
            return ChatColor.WHITE;
        }
    }

    /**
     * Gets the closest named color to the given RGB values.
     */
    private static String getClosestColorName(int r, int g, int b) {
        ChatColor closest = ChatColor.WHITE;
        double minDistance = Double.MAX_VALUE;

        for (ChatColor color : ChatColor.values()) {
            if (color == ChatColor.BOLD || color == ChatColor.ITALIC ||
                    color == ChatColor.UNDERLINE || color == ChatColor.STRIKETHROUGH ||
                    color == ChatColor.MAGIC || color == ChatColor.RESET) {
                continue;
            }

            // Approximate RGB for each ChatColor
            int[] rgb = getChatColorRGB(color);
            if (rgb == null) continue;

            double distance = Math.sqrt(
                    Math.pow(r - rgb[0], 2) +
                            Math.pow(g - rgb[1], 2) +
                            Math.pow(b - rgb[2], 2));

            if (distance < minDistance) {
                minDistance = distance;
                closest = color;
            }
        }

        return closest.name();
    }

    /**
     * Gets approximate RGB values for a ChatColor.
     */
    private static int[] getChatColorRGB(ChatColor color) {
        return switch (color) {
            case BLACK -> new int[]{0, 0, 0};
            case DARK_BLUE -> new int[]{0, 0, 170};
            case DARK_GREEN -> new int[]{0, 170, 0};
            case DARK_AQUA -> new int[]{0, 170, 170};
            case DARK_RED -> new int[]{170, 0, 0};
            case DARK_PURPLE -> new int[]{170, 0, 170};
            case GOLD -> new int[]{255, 170, 0};
            case GRAY -> new int[]{170, 170, 170};
            case DARK_GRAY -> new int[]{85, 85, 85};
            case BLUE -> new int[]{85, 85, 255};
            case GREEN -> new int[]{85, 255, 85};
            case AQUA -> new int[]{85, 255, 255};
            case RED -> new int[]{255, 85, 85};
            case LIGHT_PURPLE -> new int[]{255, 85, 255};
            case YELLOW -> new int[]{255, 255, 85};
            case WHITE -> new int[]{255, 255, 255};
            default -> null;
        };
    }
}
