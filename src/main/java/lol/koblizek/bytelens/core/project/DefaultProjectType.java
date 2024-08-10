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

package lol.koblizek.bytelens.core.project;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DefaultStringConverter;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ui.ExtendedCodeArea;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import lol.koblizek.bytelens.api.ui.toolwindows.ProjectTreeToolWindow;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import lol.koblizek.bytelens.api.util.ProjectCreator;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.AdvancedDirectoryWatcher;
import lol.koblizek.bytelens.core.utils.StringUtils;
import lol.koblizek.bytelens.core.utils.ui.MenuTargetedTreeCell;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class DefaultProjectType extends ProjectCreator {
    @Override
    public @NotNull String getName() {
        return "New Project";
    }

    @Override
    public Map<String, Class<?>> getFields() {
        Map<String, Class<?>> fields = new LinkedHashMap<>();
        fields.put("Project Name", String.class);
        fields.put("Project Location", Path.class);
        fields.put("Created@Project Location@Project Name", Label.class);
        return fields;
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates a new project with default structure";
    }

    @Override
    public @NotNull DefaultProject createProject(Map<String, Object> data) {
        Path projectPath = ((Path) data.get("Project Location"))
                .resolve((String) data.get("Project Name"));
        return new DefaultProject(projectPath);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean setupProjectTreeToolWindow(@NotNull ByteLens byteLens, @NotNull ProjectTreeToolWindow toolWindow) {
        var project = byteLens.getCurrentProject().get();
        var contextMenuContainer = byteLens.getResourceManager().getContextMenuContainer("module-context-menus");
        assert contextMenuContainer != null;
        toolWindow.setCellFactory(view -> new MenuTargetedTreeCell(new DefaultStringConverter()));
        toolWindow.setOnMouseClicked(e -> itemSelectionEvent(byteLens, toolWindow, e));
        toolWindow.root().setValue(project.getName());
        toolWindow.root().setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.ModuleGroup"));
        toolWindow.appendTreeItem(toolWindow.root(), project.getProjectFile().getFileName().toString(), item ->
                item.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Json")));
        toolWindow.appendTreeItem(toolWindow.root(), "Sources", item -> {
            item.overrideIcon("AllIcons.Expui.Nodes.Module");
            var icon = renderModule(byteLens, project.getSources(), iti -> {
                if (iti.getExtension().equals("class"))
                    return contextMenuContainer.findById("class-file").get();
                return contextMenuContainer.findById("source-module").get();
            });
            icon.overrideIcon("AllIcons.Expui.Nodes.SourceRoot");
            item.getChildren().add(icon);
        });
        toolWindow.appendTreeItem(toolWindow.root(), "Resources", item -> {
            item.overrideIcon("AllIcons.Expui.Nodes.Module");
            var icon = renderModule(byteLens, project.getResources(), iti -> contextMenuContainer.findById("resource-module").get());
            icon.overrideIcon("AllIcons.Expui.Nodes.ResourcesRoot");
            item.getChildren().add(icon);
        });
        toolWindow.appendTreeItem(toolWindow.root(), "External Libraries", item -> {
            item.overrideIcon("AllIcons.Expui.Nodes.Module");
            var icon = renderModule(byteLens, project.getReferenceLibraries(), iti -> contextMenuContainer.findById("ext-lib-module").get());
            icon.overrideIcon("AllIcons.Expui.Nodes.LibraryFolder");
            item.getChildren().add(icon);
        });
        toolWindow.appendTreeItem(toolWindow.root(), "Workspace", item -> item.overrideIcon("AllIcons.Expui.Nodes.Module"));
        return true;
    }

    private IconifiedTreeItem renderModule(ByteLens byteLens, Path rootPath, Function<IconifiedTreeItem, ContextMenu> selector) {
        IconifiedTreeItem root = null;
        try (Stream<Path> paths = Files.walk(rootPath)) {
            IconifiedTreeItem finalRoot = (root = new IconifiedTreeItem(rootPath));
            AdvancedDirectoryWatcher watcher = new AdvancedDirectoryWatcher();
            watcher.registerDir(rootPath, p -> {
                translatePath(finalRoot, rootPath, p, selector);
                finalRoot.setContextMenu(selector.apply(finalRoot));
            }, p -> reverseTranslatePath(finalRoot, rootPath, p));
            root.setContextMenu(selector.apply(root));
            paths.sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> {
                        if (path.equals(rootPath)) return;
                        if (Files.isDirectory(path)) {
                            watcher.registerDir(path, p -> {
                                translatePath(finalRoot, rootPath, p, selector);
                                finalRoot.setContextMenu(selector.apply(finalRoot));
                            }, p -> {
                                reverseTranslatePath(finalRoot, rootPath, p);
                            });
                        }
                        translatePath(finalRoot, rootPath, path, selector);
                    });
            watcher.start(byteLens.getCachedExecutor());
        } catch (IOException e) {
            byteLens.getLogger().error("An error occurred in initial file lookup:", e);
        }
        return root;
    }

    private void translatePath(IconifiedTreeItem root, Path rootPath, Path added, Function<IconifiedTreeItem, ContextMenu> selector) {
        for (Path path : rootPath.relativize(added)) {
            if (getChildren(root).contains(path.toString())) {
                //noinspection OptionalGetWithoutIsPresent
                root = (IconifiedTreeItem) root.getChildren().stream()
                        .filter(item -> item.getValue().equals(path.toString()))
                        .findFirst().get();
            } else {
                var item = new IconifiedTreeItem(added);
                root.getChildren().add(item);
                root = item;
                root.setContextMenu(selector.apply(root));
            }
        }
    }

    private void reverseTranslatePath(IconifiedTreeItem root, Path rootPath, Path removed) {
        for (int i = 0; i < rootPath.relativize(removed).getNameCount(); i++) {
            Path path = rootPath.relativize(removed).getName(i);
            if (i == rootPath.relativize(removed).getNameCount() - 1) {
                root.getChildren().removeIf(item -> item.getValue().equals(path.toString()));
            } else {
                root = (IconifiedTreeItem) root.getChildren().stream()
                        .filter(item -> item.getValue().equals(path.toString()))
                        .findFirst().get();
            }
        }
    }

    private List<String> getChildren(IconifiedTreeItem item) {
        return item.getChildren().stream().map(TreeItem::getValue).toList();
    }

    private void itemSelectionEvent(ByteLens byteLens, ProjectTreeToolWindow tw, MouseEvent event) {
        IconifiedTreeItem newV = (IconifiedTreeItem) tw.getSelectionModel().getSelectedItem();
        if (newV == null || event.getClickCount() != 2) {
            return;
        }
        ExtendedCodeArea codeArea = new ExtendedCodeArea();
        codeArea.bridge(byteLens);
        if (newV instanceof IconifiedTreeItem iti && !iti.isDirectory() && iti.isFileSystemManaged()) { // This means it's actual file/dir
            String tabName = iti.getPath().getFileName().toString();
            try {
                codeArea.clear();
                switch (iti.getExtension()) {
                    case "class" -> {
                        codeArea.appendText(byteLens.getDecompilationManager().getDecompiler()
                                .decompilePreview(Files.readAllBytes(iti.getPath())));
                        tabName = FilenameUtils.getBaseName(tabName) + ".java";
                    }
                    case null, default -> codeArea.appendText(Files.readString(iti.getPath()));
                }
            } catch (IOException e) {
                byteLens.getLogger().error("Error occurred on file read!", e);
                codeArea.appendText(StringUtils.stackTraceToString(e));
            }
            tw.opener().open(codeArea, tabName);
        }
    }
}
