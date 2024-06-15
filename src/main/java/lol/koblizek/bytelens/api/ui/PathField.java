package lol.koblizek.bytelens.api.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.IOException;

public class PathField extends StackPane {
    public PathField() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/path-field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void selectPath() {
        System.out.println("yes");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.showOpenDialog(getScene().getWindow());
    }
}
