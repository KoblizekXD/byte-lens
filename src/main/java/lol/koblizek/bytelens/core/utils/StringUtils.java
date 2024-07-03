package lol.koblizek.bytelens.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("StringUtils is a utility class and cannot be instantiated");
    }

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

    /**
     * Returns an input stream of bytes of the given string.
     * @param string the string to convert
     * @return an input stream of the given string
     */
    public static @NotNull InputStream stream(@NotNull String string) {
        Objects.requireNonNull(string);
        return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
    }

    public static @Nullable URL toRemoteURL(@NotNull String path) {
        Objects.requireNonNull(path);
        try {
            return new URI(path).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            LoggerFactory.getLogger(StringUtils.class).error("Failed to convert path to URL: {}", path, e);
            return null;
        }
    }

    /**
     * Reads the content of the remote file at the given URI.
     * @param uri the address of the remote file
     * @return the content of the remote file
     */
    public static @NotNull String readRemote(URI uri) {
        try (var stream = uri.toURL().openStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LoggerFactory.getLogger(StringUtils.class).error("Failed to read remote file: {}", uri, e);
            return "";
        }
    }

    /**
     * Reads the content of the remote file at the given URI.
     * @param address the address of the remote file
     * @return the content of the remote file
     */
    public static @NotNull String readRemote(String address) {
        try {
            return readRemote(new URI(address));
        } catch (URISyntaxException e) {
            LoggerFactory.getLogger(StringUtils.class).error("Failed to parse URI: {}", address, e);
            return "";
        }
    }
}
