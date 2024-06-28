package lol.koblizek.bytelens.core.utils;

import org.jetbrains.annotations.NotNull;

public class StringUtils {

    /**
     * Capitalizes the first letter of the given word.
     * @param word the word to capitalize
     * @return the capitalized word
     */
    public static @NotNull String capitalize(@NotNull String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    /**
     * Converts the given word into a camel case representation.
     * @param word the word to convert
     * @return the camel case representation of the word
     */
    public static @NotNull String toCamelCase(@NotNull String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }

    private static final String ICON_DOMAIN = "https://intellij-icons.jetbrains.design/icons/";

    /**
     * Converts the given icon name into a valid svg URL.
     * <p>
     *     Example: <br>
     *     Input: {@code AllIcons.Expui.Breakpoints.BreakpointFieldValid} <br>
     *     Output: <a href="https://intellij-icons.jetbrains.design/icons/AllIcons/expui/breakpoints/breakpointFieldValid.svg">https://intellij-icons.jetbrains.design/icons/AllIcons/expui/breakpoints/breakpointFieldValid.svg</a>
     * </p>
     * @param prettyName the "pretty" name of the icon
     * @param dark whether the icon should be in dark mode
     * @return the compliant icon URL
     */
    public static @NotNull String getCompliantIconURL(@NotNull String prettyName, boolean dark) {
        String[] parts = prettyName.split("\\.");
        for (int i = 1; i < parts.length; i++) {
            parts[i] = toCamelCase(parts[i]);
        }
        return ICON_DOMAIN + String.join("/", parts) + (dark ? "_dark" : "") + ".svg";
    }
}
