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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.FileChooser;
import lol.koblizek.bytelens.api.ui.contextmenus.LigmaContextMenu;
import lol.koblizek.bytelens.api.util.IconifiedMenuItem;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.ui.MenuTargetedTreeCell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ModuleContextMenusController extends Controller {

    @FXML private LigmaContextMenu sourceModule;
    @FXML private IconifiedMenuItem sourceModuleImportJar;

    private IconifiedTreeItem selectedTreeItem;

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
            try {
                Files.copy(f.toPath(), selectedTreeItem.getPath().resolve(f.getName()));
            } catch (IOException e) {
                getLogger().error("Failed to copy file", e);
            }
        }
    }

    @FXML
    public void shown(ContextMenuEvent windowEvent) {
        selectedTreeItem = (IconifiedTreeItem) ((MenuTargetedTreeCell)windowEvent.getSource()).getTreeItem();
    }
}
