package lol.koblizek.bytelens.api.resource;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Resource {

    private final URL url;
    private final byte[] bytes;

    Resource(URL url) {
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

    public byte[] toBytes() {
        return bytes.clone();
    }

    public ImageView toImageView() {
        return new ImageView(url.toExternalForm());
    }
}
