/*
 * This file is part of byte-lens.
 *
 * Copyright (c) 2024 KoblizekXD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package lol.koblizek.bytelens.core.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ui.SearchArea;
import lol.koblizek.bytelens.core.ByteLens;

import java.util.ArrayList;

public class HomeViewController extends Controller {

    @FXML private SearchArea searchArea;
    private ObservableList<String> projectListingDefault;
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
                getByteLens().findProjectByName(selectedItem).ifPresentOrElse(project ->
                        getByteLens().openProject(project),
                        () -> getLogger().error("Failed to open project - project with name {} not found, but was present in listing", selectedItem)
                );
            }
        });
        for (DefaultProject project : getByteLens().getProjects()) {
            projectListing.getItems().add(project.getName());
        }
        projectListingDefault = FXCollections.observableList(new ArrayList<>(projectListing.getItems()));
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

    @FXML
    public void searchBarTyped(KeyEvent keyEvent) {
        projectListing.getItems().clear();
        for (String s : projectListingDefault) {
            if (s.toLowerCase().contains(searchArea.getText().toLowerCase())) {
                projectListing.getItems().add(s);
            }
        }
    }

    @FXML
    public void buttonOpenExistingProjectClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Existing Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ByteLens Project", "*.bl.json"));
        var file = fileChooser.showOpenDialog(getByteLens().getPrimaryStage());
        if (file != null) {
            getLogger().info("Importing project from file: {}", file.getAbsolutePath());
            getByteLens().openProject(new DefaultProject(file.toPath().getParent()));
            getByteLens().getCurrentProject().ifPresentOrElse(project ->
                    getByteLens().getProjects().addLast(project),
                    () -> getLogger().error("Failed to import project - current project is not assigned")
            );
            getLogger().info("Project imported successfully");
        }
    }
}
