package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import lol.koblizek.bytelens.api.util.ProjectCreator;
import lol.koblizek.bytelens.core.ByteLens;

import static javafx.scene.layout.Priority.ALWAYS;

public class NewProjectViewController extends Controller {

    @FXML public ListView<ProjectCreator> projectTypeListing;
    @FXML public HBox hbox;

    public NewProjectViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        getByteLens().getProjectTypes().forEach(projectTypeListing.getItems()::add);
        projectTypeListing.getSelectionModel().select(0);
        projectTypeListing.getSelectionModel().selectedItemProperty().subscribe(menu -> {
            if (menu == null) return;
            HBox.setHgrow(menu, ALWAYS);
            if (hbox.getChildren().size() == 2) {
                hbox.getChildren().set(1, menu);
            } else {
                hbox.getChildren().add(menu);
            }
        });
    }
}
