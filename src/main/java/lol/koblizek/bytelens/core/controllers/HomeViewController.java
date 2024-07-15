/*
 * This file is part of byte-lens, licensed under the GNU General Public License v3.0.
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
