package lol.koblizek.bytelens.api.resource;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import lol.koblizek.bytelens.core.ByteLens;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class ResourceManager {

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

    private ResourceManager(ByteLens byteLens) {
        this.byteLens = byteLens;
    }

    public void setPath(@NotNull String path) {
        this.path = path;
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
            var loader = get(path).toLoader();
            loader.setControllerFactory(this::injectByteLens);
            return new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Object injectByteLens(Class<?> type) {
        for (Constructor<?> ctor : type.getConstructors()) {
            if (ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0] == ByteLens.class) {
                try {
                    return ctor.newInstance(byteLens);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("No constructor found for " + type);
    }

    private static final Map<String, Image> cache = new HashMap<>();

    public static Image getJBIcon(String path, boolean isDark) {
        if (cache.containsKey(path))
            return cache.get(path);
        try {
            String[] p2s = path.split("\\.");
            for (int i = 0; i < p2s.length - 1; i++) {
                p2s[i] = p2s[i].toLowerCase();
            }
            String p2xs = p2s[p2s.length - 1];
            p2xs = Character.toLowerCase(p2xs.charAt(0)) + p2xs.substring(1);
            p2s[p2s.length - 1] = p2xs;
            path = String.join(".", p2s);
            System.out.println(path);
            Image svg = new Resource(new URL("https://intellij-icons.jetbrains.design/icons/"
                    + path.replace(".", "/").replace("allicons", "AllIcons")
                    + (isDark ? "_dark" : "")
                    + ".svg")).toSVG();
            cache.put(path, svg);
            return svg;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
