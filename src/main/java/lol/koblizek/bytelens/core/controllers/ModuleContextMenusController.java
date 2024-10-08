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
import lol.koblizek.bytelens.api.ui.ExtendedCodeArea;
import lol.koblizek.bytelens.api.ui.contextmenus.LigmaContextMenu;
import lol.koblizek.bytelens.api.util.ASMUtil;
import lol.koblizek.bytelens.api.util.Constants;
import lol.koblizek.bytelens.api.util.IconifiedMenuItem;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.ui.MenuTargetedTreeCell;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

public class ModuleContextMenusController extends Controller {

    @FXML private LigmaContextMenu fileContentTab;
    private IconifiedTreeItem selectedTreeItem;

    public ModuleContextMenusController(ByteLens byteLens) {
        super(byteLens);
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
                    if (Files.isDirectory(selectedTreeItem.getPath()))
                        Files.copy(f.toPath(), selectedTreeItem.getPath().resolve(f.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                    else
                        Files.copy(f.toPath(), selectedTreeItem.getPath().resolveSibling(f.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    getLogger().error("Failed to copy file", e);
                }
            });
        }
    }

    private Path savePrompt(String title, String text, @Nullable Path initialDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory((initialDirectory == null ? null : initialDirectory.toFile()));
        File f = fileChooser.showSaveDialog(getByteLens().getPrimaryStage());
        if (f != null) {
            getByteLens().submitTask(() -> {
                try {
                    Files.writeString(f.toPath(), text);
                } catch (IOException e) {
                    getLogger().error("Failed to save file", e);
                }
            });
            return f.toPath();
        }
        return null;
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
        var files = clipboard.getFiles();
        getByteLens().submitTask(() -> {
            for (Path source : files.stream().map(File::toPath).toList()) {
                getLogger().info("Pasting file/directory: {}", source);
                try {
                    Path targetPath = Files.isDirectory(selectedTreeItem.getPath()) ? selectedTreeItem.getPath() : selectedTreeItem.getPath().getParent();
                    if (Files.isDirectory(source)) {
                        FileUtils.copyDirectory(source.toFile(), targetPath.toFile());
                    } else {
                        Files.copy(source, targetPath.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    getLogger().error("Failed to paste file/directory: {}", source, e);
                }
            }
        });
    }

    @FXML
    public void decompileToInstructions(ActionEvent event) {
        try (InputStream is = Files.newInputStream(selectedTreeItem.getPath())) {
            ExtendedCodeArea codeArea = new ExtendedCodeArea();
            codeArea.bridge(getByteLens());
            codeArea.setStyle("-fx-font-family: Inter");
            codeArea.setUserData(new Object[] { selectedTreeItem, false });
            codeArea.setContextMenu(fileContentTab);
            codeArea.appendText(getByteLens().submitTask(() -> {
                ClassReader reader = new ClassReader(is);
                return ASMUtil.wrapTextifier(reader);
            }).get());
            getByteLens().getContext().openTab(selectedTreeItem.valueProperty(), codeArea);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            getLogger().error(Constants.ERROR_FAILED_TO_DECOMPILE, e);
        }
    }

    @FXML
    public void decompileToASMLikeCode(ActionEvent event) {
        getByteLens().assureContext();
        try (InputStream is = Files.newInputStream(selectedTreeItem.getPath())) {
            ExtendedCodeArea codeArea = new ExtendedCodeArea();
            codeArea.bridge(getByteLens());
            codeArea.setStyle("-fx-font-family: Inter");
            codeArea.setContextMenu(fileContentTab);
            codeArea.setUserData(new Object[] { selectedTreeItem, false });
            codeArea.appendText(getByteLens().submitTask(() -> {
                ClassReader reader = new ClassReader(is);
                return ASMUtil.wrapASMifier(reader);
            }).get());
            getByteLens().getContext().openTab(selectedTreeItem.valueProperty(), codeArea);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            getLogger().error(Constants.ERROR_FAILED_TO_DECOMPILE, e);
        }
    }

    @FXML
    public void decompile(ActionEvent event) {
        getByteLens().assureContext();
        try (InputStream is = Files.newInputStream(selectedTreeItem.getPath())) {
            ExtendedCodeArea codeArea = new ExtendedCodeArea();
            codeArea.bridge(getByteLens());
            codeArea.setStyle("-fx-font-family: Inter"); // For some reason its set to jbr mono by default?!
            codeArea.setContextMenu(fileContentTab);
            codeArea.setUserData(new Object[] { selectedTreeItem, false }); // Selected + if is saved or is previewed
            codeArea.appendText(getByteLens().submitTask(() -> getByteLens().getDecompilationManager().getDecompiler()
                    .decompilePreview(is)).get());
            //noinspection DataFlowIssue
            getByteLens().getContext().openTab(selectedTreeItem.valueProperty(), codeArea);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            getLogger().error(Constants.ERROR_FAILED_TO_DECOMPILE, e);
        }
    }

    @FXML
    public void save(ActionEvent event) {
        getByteLens().assureContext();
        try {
            ExtendedCodeArea area = (ExtendedCodeArea) ((IconifiedMenuItem) event.getSource()).getParentPopup().getOwnerNode();
            Object[] data = (Object[]) area.getUserData();
            IconifiedTreeItem item = (IconifiedTreeItem) data[0];
            boolean isSaved = (boolean) data[1];
            if (item == null) return;
            Path path = item.getPath();
            if (isSaved) {
                Files.writeString(path, area.getText());
                getLogger().info("Saved file: {}", path);
            } else {
                Path saved = savePrompt("Save File", area.getText(), path.getParent());
                if (saved != null) { //noinspection DataFlowIssue
                    getByteLens().getContext().closeActiveTab();
                    getByteLens().getContext().open(saved);
                }
            }
        } catch (Exception e) {
            getLogger().error("Failed to save file", e);
        }
    }
}
