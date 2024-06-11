package lol.koblizek.bytelens.api.resource;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
     * Attempts to convert the resource to an SVG image with default width & height
     * (16).
     * @return The resource as a read SVG image.
     */
    public Image toSVG() {
        return toSVG(16, 16);
    }

    /**
     * Attempts to convert the resource to an SVG image.
     * @param width The width of the image.
     * @param height The height of the image.
     * @return The resource as a read SVG image.
     */
    public Image toSVG(int width, int height) {
        PNGTranscoder transcoder = new PNGTranscoder();
        String svg = new String(bytes, StandardCharsets.UTF_8);
        svg = svg.replaceFirst("width=\"[0-9]+\"", "width=\"" + width + "\"")
                .replaceFirst("height=\"[0-9]+\"", "height=\"" + height + "\"");
        System.out.println(svg);
        try (InputStream stream = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8))) {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(new TranscoderInput(stream), output);
            return SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())), null);
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
