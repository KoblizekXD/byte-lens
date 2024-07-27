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

package lol.koblizek.bytelens.api.ui.toolwindows;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DefaultStringConverter;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.ExtendedCodeArea;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import lol.koblizek.bytelens.api.ui.Opener;
import lol.koblizek.bytelens.api.ui.contextmenus.DefaultModuleContextMenu;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.StandardDirectoryWatcher;
import lol.koblizek.bytelens.core.utils.ui.MenuTargetedTreeCell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

public class ProjectTreeToolWindow extends TreeView<String> implements ToolWindow.ToolWindowNode {

    private final Opener opener;
    @FXML private IconifiedTreeItem root;

    private ByteLens byteLens;

    public ProjectTreeToolWindow(Opener opener) {
        this.opener = opener;
    }

    @Override
    public Node create(ByteLens byteLens) throws IOException {
        this.byteLens = byteLens;
        var loader = byteLens.getResourceManager().getFXML("components/project-tw.fxml");
        loader.setRoot(this);
        loader.setController(this);
        return loader.load();
    }

    @FXML
    public void initialize() {
        Optional<DefaultProject> optionalProject = byteLens.getCurrentProject();
        optionalProject.ifPresentOrElse(project -> {
            setCellFactory(view -> new MenuTargetedTreeCell(new DefaultStringConverter()));
            this.setOnMouseClicked(this::itemSelectionEvent);
            root.setValue(project.getName());
            root.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.ModuleGroup"));
            appendTreeItem(root, project.getProjectFile().getFileName().toString(), item ->
                    item.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Json")));
            appendTreeItem(root, "Sources", item -> {
                item.overrideIcon("AllIcons.Expui.Nodes.Module");
                item.setContextMenu(byteLens.getResourceManager().getContextMenu("default-module-context-menu", DefaultModuleContextMenu.class));
                var icon = getModule(project.getSources());
                icon.overrideIcon("AllIcons.Expui.Nodes.SourceRoot");
                item.getChildren().add(icon);
            });
            appendTreeItem(root, "Resources", item -> {
                item.overrideIcon("AllIcons.Expui.Nodes.Module");
                var icon = getModule(project.getResources());
                icon.overrideIcon("AllIcons.Expui.Nodes.ResourcesRoot");
                item.getChildren().add(icon);
            });
            appendTreeItem(root, "External Libraries", item -> {
                item.overrideIcon("AllIcons.Expui.Nodes.Module");
                var icon = getModule(project.getReferenceLibraries());
                icon.overrideIcon("AllIcons.Expui.Nodes.LibraryFolder");
                item.getChildren().add(icon);
            });
            appendTreeItem(root, "Workspace", item -> item.overrideIcon("AllIcons.Expui.Nodes.Module"));
        }, () -> {
            root.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.ErrorIntroduction"));
            root.setValue("No module/project is open");
        });
    }

    private void appendTreeItem(TreeItem<String> parent, String value, Consumer<IconifiedTreeItem> configurator) {
        var item = new IconifiedTreeItem(value);
        configurator.accept(item);
        parent.getChildren().add(item);
    }

    private IconifiedTreeItem getModule(Path rootPath) {
        IconifiedTreeItem rootItem = new IconifiedTreeItem(rootPath);
        try {
            Files.list(rootPath)
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            rootItem.getChildren().add(getModule(path));
                        } else {
                            rootItem.getChildren().add(new IconifiedTreeItem(path));
                        }
                    });
            var watcher = new StandardDirectoryWatcher(rootPath, rootItem);
            watcher.start(byteLens.getCachedExecutor());
        } catch (IOException e) {
            byteLens.getLogger().error("An error occurred in initial file lookup:", e);
        }
        return rootItem;
    }

    private void itemSelectionEvent(MouseEvent event) {
        IconifiedTreeItem newV = (IconifiedTreeItem) this.getSelectionModel().getSelectedItem();
        if (newV == null || event.getClickCount() != 2) {
            return;
        }
        ExtendedCodeArea codeArea = new ExtendedCodeArea();
        codeArea.bridge(byteLens);
        if (newV instanceof IconifiedTreeItem iti && !iti.isDirectory() && iti.isFileSystemManaged()) { // This means it's actual file/dir
            try {
                codeArea.clear();
                switch (iti.getExtension()) {
                    case "class" -> codeArea.appendText(byteLens.getDecompilationManager().getDecompiler()
                            .decompilePreview(Files.readAllBytes(iti.getPath())));
                    case null, default -> codeArea.appendText(Files.readString(iti.getPath()));
                }
            } catch (IOException e) {
                byteLens.getLogger().error("Error occurred on file read!", e);
            }
            opener.open(codeArea, iti.getPath().getFileName().toString());
        }
    }
}
