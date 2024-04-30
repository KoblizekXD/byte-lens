package lol.koblizek.bytelens.api.resource;

import javafx.scene.Scene;

import java.io.IOException;

public final class ResourceManager {

    private static ResourceManager instance;

    public static void init() {
        if (instance == null)
            instance = new ResourceManager();
        else throw new IllegalStateException("ResourceManager already initialized");
    }

    private ResourceManager() {}

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
            return new Scene(get(path).toLoader().load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
