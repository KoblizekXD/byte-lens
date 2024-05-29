package lol.koblizek.bytelens.api.resource;

import javafx.scene.Scene;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.controllers.Controller;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
            if (loader.getController() instanceof Controller controller)
                controller.setByteLens(byteLens);
            return new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Resource getJBIcon(String path, boolean isDark) {
        try {
            return new Resource(new URL("https://intellij-icons.jetbrains.design/icons/"
                    + path.toLowerCase().replace(".", "/").replace("allicons", "AllIcons")
                    + (isDark ? "_dark" : "")
                    + ".svg"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
