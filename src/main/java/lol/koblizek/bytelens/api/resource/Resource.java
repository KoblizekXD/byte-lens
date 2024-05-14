package lol.koblizek.bytelens.api.resource;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import lol.koblizek.bytelens.core.svg.SVGTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Resource {

    private final URL url;
    private final byte[] bytes;

    Resource(@NotNull URL url) {
        this.url = url;
        try {
            bytes = url.openStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getUrl() {
        return url;
    }

    public String getPath() {
        return url.getPath();
    }

    @Override
    public String toString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * @return A copy of the resource as a byte array.
     */
    public byte[] toBytes() {
        return bytes.clone();
    }

    /**
     * Attempts to convert the resource to an image.
     * @return The resource as an image.
     */
    public ImageView toImageView() {
        return new ImageView(url.toExternalForm());
    }

    /**
     * Attempts to convert the resource to a properties object.
     * @return The resource as a properties object.
     */
    public Properties toProperties() {
        var properties = new Properties();
        try (var stream = url.openStream()) {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    /**
     * Attempts to load the resource as an FXML file.
     * @return A new FXMLLoader instance for the resource.
     */
    public FXMLLoader toLoader() {
        return new FXMLLoader(url);
    }

    /**
     * Attempts to convert the resource to an SVG image.
     * @return The resource as a read SVG image.
     */
    public Image toSVG() {
        SVGTranscoder transcoder = new SVGTranscoder();
        try (InputStream stream = url.openStream()) {
            transcoder.transcode(new TranscoderInput(stream), null);
            return SwingFXUtils.toFXImage(transcoder.getImg(), null);
        } catch (IOException | TranscoderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to convert the resource to a font.
     * @return The resource as a font.
     */
    public Font toFont() {
        return Font.loadFont(url.toExternalForm(), 12);
    }
}
