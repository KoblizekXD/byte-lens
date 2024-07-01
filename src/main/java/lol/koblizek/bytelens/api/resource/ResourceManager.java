package lol.koblizek.bytelens.api.resource;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
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

    public URL get(@NotNull String path) {
        Objects.requireNonNull(path);
        return Objects.requireNonNull(getClass().getResource(this.path + path));
    }

    public FXMLLoader getFXML(@NotNull String path) {
        Objects.requireNonNull(path);
        return new FXMLLoader(get(path));
    }

    public InputStream openStream(@NotNull String path) {
        Objects.requireNonNull(path);
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

    public Properties getProperties(String path) {
        var properties = new Properties();
        try (var stream = getClass().getResourceAsStream(this.path + path)) {
            properties.load(stream);
        } catch (IOException e) {
            LOGGER.error("Failed to load properties: {}", path, e);
        }
        return properties;
    }

    public void loadFont(String path) {
        try (var stream = getClass().getResourceAsStream(this.path + path)) {
            Font.loadFont(stream, 12);
        } catch (IOException e) {
            LOGGER.error("Failed to load font: {}", path, e);
        }
    }

    public static Image getJBIcon(String id, boolean isDark) {
        return getJBIcon(id, isDark, 16, 16);
    }

    public static Image getJBIcon(String id, boolean isDark, int width, int height) {
        return tryGetFromCache(id + "@" + width + "x" + height).orElseGet(() -> {
            var url = StringUtils.toRemoteURL(StringUtils.getCompliantIconURL(id, isDark));
            if (url == null) {
                return null;
            }
            return saveInCache(id, convertSVGToImage(url, width, height));
        });
    }

    public static @Nullable String read(@NotNull URL url) {
        Objects.requireNonNull(url);
        try (InputStream stream = url.openStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to read resource: {}", url, e);
            return null;
        }
    }

    private static @Nullable Image convertSVGToImage(@NotNull URL url, int width, int height) {
        Objects.requireNonNull(url);
        String svgContent = read(url);

        if (svgContent == null) {
            return null;
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
