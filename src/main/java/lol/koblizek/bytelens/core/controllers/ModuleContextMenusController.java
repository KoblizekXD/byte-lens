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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import lol.koblizek.bytelens.api.util.IconifiedMenuItem;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.ui.MenuTargetedTreeCell;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class ModuleContextMenusController extends Controller {

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
        importFile("Import Source Archive",
                new FileChooser.ExtensionFilter("Jar Files", "*.jar", "*.war", "*.zip"),
                new FileChooser.ExtensionFilter("Android Library", "*.aar"),
                new FileChooser.ExtensionFilter("Android Application", "*.apk"));
    }

    @FXML
    private void sourceModuleImportClass(ActionEvent actionEvent) {
        importFile("Import Class File", new FileChooser.ExtensionFilter("Class Files", "*.class", "*.dex"));
    }

    @FXML
    private void resourceModuleImportMappings(ActionEvent actionEvent) {
        importFile("Import Mappings",
                new FileChooser.ExtensionFilter("Mappings Files", "*.txt", "*.csv", "*.tiny", "*.tsrg", "*.mappings", "*.proguard")
        );
    }

    private void importFile(String title, FileChooser.ExtensionFilter... filters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(filters);
        File f = fileChooser.showOpenDialog(getByteLens().getPrimaryStage());
        if (f != null) {
            getByteLens().submitTask(() -> {
                try {
                    Files.copy(f.toPath(), selectedTreeItem.getPath().resolve(f.getName()));
                } catch (IOException e) {
                    getLogger().error("Failed to copy file", e);
                }
            });
        }
    }

    @FXML
    public void shown(ContextMenuEvent windowEvent) {
        selectedTreeItem = (IconifiedTreeItem) ((MenuTargetedTreeCell) windowEvent.getSource()).getTreeItem();
    }

    @FXML
    public void copyAction(ActionEvent event) {
        var clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.put(DataFormat.FILES, Collections.singletonList(selectedTreeItem.getPath().toFile()));
        clipboard.setContent(content);
        getLogger().info("Copied file/directory into clipboard: {}", selectedTreeItem.getPath());
    }

    @FXML
    public void deleteAction(ActionEvent event) {
        getByteLens().submitTask(() -> {
            getLogger().info("Deleting file/directory: {}", selectedTreeItem.getPath());
            try {
                if (Files.isDirectory(selectedTreeItem.getPath())) {
                    FileUtils.deleteDirectory(selectedTreeItem.getPath().toFile());
                } else {
                    Files.delete(selectedTreeItem.getPath());
                }
            } catch (IOException e) {
                getLogger().error("Failed to delete file", e);
            }
        });
    }

    @FXML
    public void pasteAction(ActionEvent event) {
        var clipboard = Clipboard.getSystemClipboard();
        if (clipboard.getFiles() == null) return;

        getByteLens().submitTask(() -> {
            for (Path file : clipboard.getFiles().stream().map(File::toPath).toList()) {
                getLogger().info("Pasting file/directory: {}", file);
                try {
                    if (Files.isDirectory(file)) {
                        FileUtils.copyDirectory(file.toFile(), selectedTreeItem.getPath().toFile());
                    } else {
                        Files.copy(file, selectedTreeItem.getPath().resolve(file.getFileName()));
                    }
                } catch (IOException e) {
                    getLogger().error("Failed to paste file/directory: {}", file, e);
                }
            }
        });
    }
}
