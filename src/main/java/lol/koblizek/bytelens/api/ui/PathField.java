package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class PathField extends StackPane {

    private BooleanProperty directoryOnly;

    @FXML private TextField textField;

    public PathField() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/path-field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to load", e);
        }
    }

    @FXML
    public void initialize() {
        directoryOnly = new SimpleBooleanProperty();
    }

    @FXML
    public void selectPath() {
        Path p = null;
        if (isDirectoryOnly()) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Open Directory");
            var f = chooser.showDialog(getScene().getWindow());
            if (f != null) {
                p = f.toPath();
            }
        } else {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            var f = chooser.showOpenDialog(getScene().getWindow());
            if (f != null) {
                p = f.toPath();
            }
        }
        if (p != null) {
            textField.setText(p.toString());
        }
    }

    public BooleanProperty directoryOnlyProperty() {
        return directoryOnly;
    }

    public boolean isDirectoryOnly() {
        return directoryOnly.get();
    }

    public void setDirectoryOnly(boolean directoryOnly) {
        this.directoryOnly.set(directoryOnly);
    }

    public String getText() {
        return textField.getText();
    }
}
