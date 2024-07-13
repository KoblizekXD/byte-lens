/*
Copyright (c) 2024 KoblizekXD

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package lol.koblizek.bytelens.api.util;

import javafx.scene.control.TreeItem;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class IconifiedTreeItem extends TreeItem<String> {

    private Path path;

    /**
     * @param value Path, has to be file name otherwise it will look ugly
     */
    public IconifiedTreeItem(Path value) {
        super();
        this.path = value;
        valueProperty().addListener((observable, oldValue, newValue) -> updateGraphics(path));
        setValue(value.getFileName().toString());
    }

    public void updateGraphics() {
        updateGraphics(path);
    }

    public void updateGraphics(Path p) {
        if (Files.isDirectory(p)) {
            setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Folder"));
            return;
        }
        var targetIcon = switch (FilenameUtils.getExtension(p.getFileName().toString())) {
            case "class" -> "AllIcons.Expui.Nodes.Class";
            case "java" -> "AllIcons.Expui.FileTypes.Java";
            case "jar" -> "AllIcons.Expui.FileTypes.Archive";
            case "json" -> "AllIcons.Expui.FileTypes.Json";
            case "tiny", "tsrg", "mappings", "proguard" -> "AllIcons.Expui.Nodes.DataTables";
            default -> "AllIcons.Expui.FileTypes.Text";
        };
        path = p;
        setGraphic(new JetBrainsImage(targetIcon));
    }

    public void overrideIcon(String icon) {
        setGraphic(new JetBrainsImage(icon));
    }

    public Path getPath() {
        return path;
    }

    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    public String getExtension() {
        return FilenameUtils.getExtension(path.getFileName().toString());
    }
}
