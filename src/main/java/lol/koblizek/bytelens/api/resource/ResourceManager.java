package lol.koblizek.bytelens.api.resource;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public final class ResourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    private final ByteLens byteLens;
    private String path;

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

    public Resource get(String path) {
        var resource = getClass().getResource(this.path + path);
        if (resource == null)
            throw new IllegalArgumentException("Resource not found: " + path);
        return new Resource(resource);
    }

    public Scene getScene(String path) {
        try {
            var loader = get("views/" + path + ".fxml").toLoader();
            loader.setControllerFactory(this::injectByteLens);
            return new Scene(loader.load());
        } catch (IOException e) {
            LOGGER.error("Failed to load scene: {}", path, e);
            return null;
        }
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

    private static final Map<String, Image> cache = new HashMap<>();

    public static Image getJBIcon(String path, boolean isDark) {
        return getJBIcon(path, isDark, 16, 16);
    }

    public static Image getJBIcon(String path, boolean isDark, int width, int height) {
        if (cache.containsKey(path))
            return cache.get(path);
        try {
            Image svg = new Resource(new URI(StringUtils.getCompliantIconURL(path, isDark)).toURL()).toSVG(width, height);
            cache.put(path, svg);
            return svg;
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.error("Failed to load icon: {}", path, e);
            return null;
        }
    }
}
