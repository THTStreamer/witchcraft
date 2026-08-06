package com.witchcraft.util;

/**
 * Utility class for text operations.
 */
public final class TextUtils {

    private TextUtils() {
    }

    /**
     * Normalizes a string for incantation matching.
     * Removes punctuation, extra spaces, and converts to lowercase.
     *
     * @param input the input string
     * @return the normalized string
     */
    public static String normalizeForIncantation(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Capitalizes the first letter of each word.
     *
     * @param input the input string
     * @return the capitalized string
     */
    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) return input;

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Truncates a string to a maximum length, adding ellipsis if truncated.
     *
     * @param input      the input string
     * @param maxLength  the maximum length
     * @return the truncated string
     */
    public static String truncate(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) return input;
        return input.substring(0, maxLength - 3) + "...";
    }

    /**
     * Removes color codes from a string.
     *
     * @param input the input string
     * @return the string without color codes
     */
    public static String stripColors(String input) {
        if (input == null) return "";
        return input.replaceAll("§[0-9a-fk-or]", "")
                .replaceAll("&[0-9a-fk-or]", "");
    }
}
