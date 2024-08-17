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

package lol.koblizek.bytelens.api.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class IconifiedTreeItem extends TreeItem<String> implements CustomContextMenuTarget {

    private ObjectProperty<Path> path;
    private ContextMenu contextMenu;

    /**
     * @param value Path, has to be file name otherwise it will look ugly
     */
    public IconifiedTreeItem(Path value) {
        super();
        this.path = new SimpleObjectProperty<>(value);
        valueProperty().addListener((observable, oldValue, newValue) -> updateGraphics(getPath()));
        setValue(value.getFileName().toString());
    }

    public IconifiedTreeItem(String value) {
        super();
        setValue(value);
    }

    public IconifiedTreeItem() {
        super();
    }

    public void updateGraphics() {
        updateGraphics(getPath());
    }

    public void updateGraphics(Path p) {
        if (Files.isDirectory(p)) {
            setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Folder"));
            return;
        }
        var targetIcon = iconFor(p.getFileName().toString());
        path.set(p);
        setGraphic(new JetBrainsImage(targetIcon));
    }

    public static String iconFor(String fileName) {
        String ext = FilenameUtils.getExtension(fileName);
        if (ext.isEmpty()) return "AllIcons.Expui.FileTypes.Text";
        return switch (ext) {
            case "class" -> "AllIcons.Expui.Nodes.Class";
            case "java" -> "AllIcons.Expui.FileTypes.Java";
            case "jar" -> "AllIcons.Expui.FileTypes.Archive";
            case "json" -> "AllIcons.Expui.FileTypes.Json";
            case "csv", "tiny", "tsrg", "mappings", "proguard" -> "AllIcons.Expui.Nodes.DataTables";
            default -> "AllIcons.Expui.FileTypes.Text";
        };
    }

    public void overrideIcon(String icon) {
        setGraphic(new JetBrainsImage(icon));
    }

    public Path getPath() {
        return path.get();
    }

    public boolean isDirectory() {
        return path != null && Files.isDirectory(getPath());
    }

    public String getExtension() {
        return FilenameUtils.getExtension(getPath().getFileName().toString());
    }

    public boolean isFileSystemManaged() {
        return path != null;
    }

    @Override
    public ContextMenu getContextMenu() {
        return (contextMenu == null) ? CustomContextMenuTarget.super.getContextMenu() : contextMenu;
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public ObjectProperty<Path> pathProperty() {
        return path;
    }

    public void setPath(Path path) {
        this.path.set(path);
    }
}
