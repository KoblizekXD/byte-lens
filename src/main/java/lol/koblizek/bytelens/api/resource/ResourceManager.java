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

package lol.koblizek.bytelens.api.resource;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import lol.koblizek.bytelens.api.ui.contextmenus.ContextMenuContainer;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.Preconditions;
import lol.koblizek.bytelens.core.utils.StringUtils;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public final class ResourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    private final ByteLens byteLens;
    private String path;
    private static final PNGTranscoder transcoder = new PNGTranscoder();

    public static ResourceManager create(ByteLens inst, String path) {
        var rm = new ResourceManager(inst);
        rm.setPath(path);
        return rm;
    }

    public static ResourceManager create(ByteLens inst) {
        var rm = new ResourceManager(inst);
        rm.setPath("");
        return rm;
    }

    private void setPath(@NotNull String path) {
        this.path = path;
    }

    private ResourceManager(ByteLens byteLens) {
        this.byteLens = byteLens;
    }

    public @NotNull String getPath() {
        return path;
    }

    /**
     * @param path Relative path to the FXML file
     * @return Scene loaded from the FXML file
     */
    public Scene getScene(String path) {
        try {
            var loader = getFXML("views/" + path + ".fxml");
            loader.setControllerFactory(this::injectByteLens);
            return new Scene(loader.load());
        } catch (IOException e) {
            LOGGER.error("Failed to load scene: {}", path, e);
            return null;
        }
    }

    /**
     * @param path Relative path to the FXML file
     * @return Default node loaded from the FXML file
     */
    public Node getNode(String path) {
        try {
            var loader = getFXML("views/" + path + ".fxml");
            loader.setControllerFactory(this::injectByteLens);
            return loader.load();
        } catch (IOException e) {
            LOGGER.error("Failed to load node: {}", path, e);
            return null;
        }
    }

    /**
     * @param path Relative path to the resource
     * @return URL of the resource
     */
    public URL get(@NotNull String path) {
        Preconditions.nonNull(path);
        return Objects.requireNonNull(getClass().getResource(this.path + path));
    }

    /**
     * @param path Relative path to the FXML file
     * @return FXMLLoader of the FXML file
     */
    public FXMLLoader getFXML(@NotNull String path) {
        Preconditions.nonNull(path);
        return new FXMLLoader(get(path));
    }

    /**
     * @param path Relative path to the resource
     * @return InputStream of the resource
     */
    public InputStream openStream(@NotNull String path) {
        Preconditions.nonNull(path);
        return getClass().getResourceAsStream(this.path + path);
    }

    private Object injectByteLens(Class<?> type) {
        for (Constructor<?> ctor : type.getConstructors()) {
            if (ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0] == ByteLens.class) {
                try {
                    return ctor.newInstance(byteLens);
                } catch (ReflectiveOperationException e) {
                    LOGGER.error("Failed to inject ByteLens into controller", e);
                }
            }
        }
        throw new IllegalArgumentException("No constructor found for " + type);
    }

    /**
     * Loads properties from the specified path
     * @param path Relative path to the properties file
     * @return Properties object with the loaded properties
     */
    public Properties getProperties(String path) {
        var properties = new Properties();
        try (var stream = getClass().getResourceAsStream(this.path + path)) {
            properties.load(stream);
        } catch (IOException e) {
            LOGGER.error("Failed to load properties: {}", path, e);
        }
        return properties;
    }

    /**
     * Loads a font from the specified path, with the font size of 12
     * @param path Relative path to the font file
     */
    public void loadFont(String path) {
        try (var stream = getClass().getResourceAsStream(this.path + path)) {
            Font.loadFont(stream, 12);
        } catch (IOException e) {
            LOGGER.error("Failed to load font: {}", path, e);
        }
    }

    /**
     * Returns an icon from the JetBrains icon pack, with default size of 16x16.
     * @param id Icon ID
     * @param isDark Whether the icon should be in dark mode
     * @return Image instance of the icon
     * @see #getJBIcon(String, boolean, int, int)
     */
    public static Image getJBIcon(String id, boolean isDark) {
        return getJBIcon(id, isDark, 16, 16);
    }

    /**
     * Returns an icon from the JetBrains icon pack.
     * Image will first be tried to be loaded from cache, if it fails, it will be remotely
     * downloaded, converted into png and saved to cache.
     * @param id Icon ID
     * @param isDark Whether the icon should be in dark mode
     * @param width Icon width
     * @param height Icon height
     * @return Image instance of the icon
     */
    public static Image getJBIcon(String id, boolean isDark, int width, int height) {
        return tryGetFromCache(id + "@" + width + "x" + height).orElseGet(() -> {
            var url = StringUtils.toRemoteURL(StringUtils.getCompliantIconURL(id, isDark));
            if (url == null) {
                return null;
            }
            return saveInCache(id, convertSVGToImage(url, width, height));
        });
    }

    /**
     * Reads the content as string of the resource from the specified URL in UTF-8 encoding
     * @param url URL of the resource
     * @return Content of the resource as a string
     */
    public static @Nullable String read(@NotNull URL url) {
        Preconditions.nonNull(url);
        try (InputStream stream = url.openStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to read resource: {}", url, e);
            return null;
        }
    }

    public <T extends ContextMenu> T getContextMenu(String path, Class<T> type) {
        try {
            var loader = getFXML("components/context-menus/" + path + ".fxml");
            loader.setController(injectByteLens(type));
            loader.setRoot(loader.getController());
            return loader.load();
        } catch (IOException e) {
            LOGGER.error("Failed to load context menu: {}", path, e);
            return null;
        }
    }

    public ContextMenu getContextMenu(String path) {
        return getContextMenu(path, ContextMenu.class);
    }

    public ContextMenuContainer getContextMenuContainer(String path) {
        FXMLLoader loader = getFXML("components/context-menus/" + path + ".fxml");
        try {
            return loader.load();
        } catch (IOException e) {
            LOGGER.error("Failed to load context menu container: {}", path, e);
            return null;
        }
    }

    private static @Nullable Image convertSVGToImage(@NotNull URL url, int width, int height) {
        Preconditions.nonNull(url);
        String svgContent = read(url);

        if (svgContent == null) {
            // Return stub image if failed to read SVG content
            try {
                return SwingFXUtils.toFXImage(ImageIO.read(ResourceManager.class.getResourceAsStream("/lol/koblizek/bytelens/resources/stub_dark@16x16.png")), null);
            } catch (IOException e) {
                LOGGER.error("Failed to read stub image, this isn't supposed to happen", e);
            }
        }

        svgContent = svgContent.replaceFirst("width=\"\\d+\"", "width=\"" + width + "\"")
                .replaceFirst("height=\"\\d+\"", "height=\"" + height + "\"");

        try (var stream = StringUtils.stream(svgContent);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            TranscoderInput input = new TranscoderInput(stream);
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(input, output);

            byte[] imageBytes = outputStream.toByteArray();

            return SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(imageBytes)), null);
        } catch (IOException | TranscoderException e) {
            LOGGER.error("Failed to convert SVG to image: {}", url, e);
            return null;
        }
    }

    private static Optional<Image> tryGetFromCache(@NotNull String id) {
        if (Preconditions.isAnyNull(id)) {
            return Optional.empty();
        }

        var target = ByteLens.getUserDataPath().resolve("cache/img").resolve(id + ".png");
        if (!Files.exists(target)) {
            return Optional.empty();
        }

        try {
            return Optional.of(SwingFXUtils.toFXImage(ImageIO.read(target.toFile()), null));
        } catch (IOException e) {
            LOGGER.error("Failed to read cached image: {}", target, e);
            return Optional.empty();
        }
    }

    private static @Nullable Image saveInCache(@NotNull String id, @NotNull Image image) {
        if (Preconditions.isAnyNull(id, image)) {
            return null;
        }

        var target = ByteLens.getUserDataPath().resolve("cache/img").resolve(id + "@"
                + (int) image.getWidth() + "x" + (int) image.getHeight()
                + ".png");
        try {
            Files.createDirectories(target.getParent());
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", target.toFile());
            return image;
        } catch (IOException e) {
            LOGGER.error("Failed to save image to cache: {}", target, e);
            return null;
        }
    }
}
