package lol.koblizek.bytelens.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.resource.ResourceManager;

public final class ByteLens extends Application {

    private static ByteLens instance;

    public static ByteLens getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    private Stage currentStage;

    @Override
    public void start(Stage stage) throws Exception {
        currentStage = stage;
        Scene scene = getResourceManager().getScene("/lol/koblizek/bytelens/hello-view.fxml");
        stage.setTitle("ByteLens");
        stage.setScene(scene);
        stage.show();
    }

    public ByteLens() {
        ResourceManager.init();

        instance = this;
    }

    public ResourceManager getResourceManager() {
        return ResourceManager.getInstance();
    }
}
