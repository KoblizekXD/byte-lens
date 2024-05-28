package lol.koblizek.bytelens.api.resource;

import javafx.scene.Scene;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.controllers.Controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class ResourceManager {

    private static ResourceManager instance;
    private final ByteLens byteLens;

    public static void init(ByteLens inst) {
        if (instance == null)
            instance = new ResourceManager(inst);
        else throw new IllegalStateException("ResourceManager already initialized");
    }

    private ResourceManager(ByteLens byteLens) {
        this.byteLens = byteLens;
    }

    public static ResourceManager getInstance() {
        return instance;
    }

    public Resource get(String path) {
        var resource = getClass().getResource(path);
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

    public Resource getJBIcon(String path, boolean isDark) {
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
