/*
 * This file is part of byte-lens.
 *
 * Copyright (c) 2024 KoblizekXD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package lol.koblizek.bytelens.core.utils;

import lol.koblizek.bytelens.api.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

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
        Preconditions.nonNull(string);
        return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
    }

    public static @Nullable URL toRemoteURL(@NotNull String path) {
        Preconditions.nonNull(path);
        try {
            return new URI(path).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.error("Failed to convert path to URL: {}", path, e);
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
            LOGGER.error("Failed to read remote file: {}", uri, e);
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
            LOGGER.error("Failed to parse URI: {}", address, e);
            return "";
        }
    }

    public static String stackTraceToString(Exception e) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ex) {
            LOGGER.error("Failed to convert stack trace to string", ex);
            return "";
        }
    }

    public static <T> T[] reverseArray(T[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            T temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
        return array;
    }

    public static <T> boolean contains(T[] array, T t) {
        for (T t1 : array) {
            if (t1.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public static String hashOf(@NotNull URL stream) {
        Preconditions.nonNull(stream);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return digestedURLToString(stream, md);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("MD5 algorithm was not found", e);
            return "ReadError";
        }
    }

    public static String hashOf(@NotNull Path file) {
        Preconditions.nonNull(file);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return digestedPathToString(file, md);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("MD5 algorithm was not found", e);
            return Constants.READ_FAILED + StringUtils.stackTraceToString(e);
        }
    }

    private static String digestedPathToString(Path path, MessageDigest digest) {
        try (DigestInputStream dis = new DigestInputStream(Files.newInputStream(path), digest)) {
            dis.readAllBytes();
            return new String(dis.getMessageDigest().digest());
        } catch (IOException e) {
            LOGGER.error(Constants.ERROR_FAILED_TO_HASH, e);
            return Constants.READ_FAILED + StringUtils.stackTraceToString(e);
        }
    }

    private static String digestedURLToString(URL path, MessageDigest digest) {
        try (DigestInputStream dis = new DigestInputStream(path.openStream(), digest)) {
            dis.readAllBytes();
            return new String(dis.getMessageDigest().digest());
        } catch (IOException e) {
            LOGGER.error(Constants.ERROR_FAILED_TO_HASH, e);
            return Constants.READ_FAILED + StringUtils.stackTraceToString(e);
        }
    }

    /**
     * Converts a string number to a boolean.
     * @param num number to convert
     * @return true if the number is 1, false otherwise
     */
    public static boolean stringNumberToBoolean(String num) {
        try {
            int i = Integer.parseInt(num);
            return i == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param num number to check
     * @return true if the number is a valid integer, false otherwise
     */
    public static boolean isNumber(String num) {
        try {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param num number to check
     * @return true if the number is a valid boolean(1 or 0), if it is not(or is not a number) returns false
     */
    public static boolean isNumberBoolean(String num) {
        return num.equals("1") || num.equals("0");
    }
}
