package lol.koblizek.bytelens.core.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import lol.koblizek.bytelens.api.util.IconifiedMenuItem;
import lol.koblizek.bytelens.core.ByteLens;

import java.io.File;
import java.nio.file.Path;

public class ModuleContextMenusController extends Controller {

    @FXML
    private IconifiedMenuItem sourceModuleImportJar;

    public ModuleContextMenusController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {

    }

    @FXML
    private void sourceModuleImportJar(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Source Jar File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jar Files", "*.jar", "*.war", "*.zip"),
                new FileChooser.ExtensionFilter("Android Library", "*.aar"),
                new FileChooser.ExtensionFilter("Android Application", "*.apk")
        );
        File f = fileChooser.showOpenDialog(getByteLens().getPrimaryStage());
        if (f != null) {
            Path path = f.toPath();
        }
    }
}
