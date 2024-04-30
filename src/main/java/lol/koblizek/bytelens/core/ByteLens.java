package lol.koblizek.bytelens.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
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

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 480);
        stage.setTitle("ByteLens");
        stage.setScene(scene);
        stage.show();
    }

    public ByteLens() {
        ResourceManager.init();

        instance = this;
    }
}
