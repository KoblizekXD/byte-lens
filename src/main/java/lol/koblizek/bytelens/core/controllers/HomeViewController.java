package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.core.ByteLens;

public class HomeViewController extends Controller {

    @FXML private ListView<String> projectListing;

    public HomeViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        projectListing.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var selectedItem = projectListing.getSelectionModel().getSelectedItem();
                if (selectedItem == null || selectedItem.isBlank()) {
                    return;
                }
                getByteLens().openProject(getByteLens().findProjectByName(selectedItem).get());
            }
        });
        for (DefaultProject project : getByteLens().getProjects()) {
            projectListing.getItems().add(project.getName());
        }
    }

    @FXML
    public void buttonNewProjectClicked() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(getByteLens().getPrimaryStage());
        stage.setTitle("New Project");
        stage.setScene(getByteLens().getScene("new-project-view"));
        stage.show();
    }
}
