package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.decompiler.DecompilationManager;
import lol.koblizek.bytelens.core.utils.StringUtils;

import java.util.Arrays;

public class SelectDecompilerModalController extends Controller {

    @FXML private Button doneButton;
    @FXML private ComboBox<String> decompilerSelector;

    public SelectDecompilerModalController(ByteLens byteLens) {
        super(byteLens);
    }

    public static void open(ByteLens bl) {
        Stage stage = new Stage();
        stage.setTitle("Select decompiler");
        stage.initOwner(bl.getPrimaryStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(bl.getResourceManager().getScene("select-decompiler-modal"));
        stage.showAndWait();
    }

    @Override
    public void initialize() {
        var cached = getByteLens().getDecompilationManager().getCachedDecompilers();
        decompilerSelector.getItems().addAll(StringUtils.reverseArray(Arrays.stream(DecompilationManager.Providers.VINEFLOWER.getVersions())
                .map(v -> "Vineflower " + v)
                .map(str -> str + (StringUtils.contains(cached, str) ? " (Cached)" : "")).toArray(String[]::new)));
        decompilerSelector.getSelectionModel().select(0);
    }

    @FXML
    public void promptModalClose(MouseEvent mouseEvent) {
        ((Stage)doneButton.getScene().getWindow()).close();
    }
}
