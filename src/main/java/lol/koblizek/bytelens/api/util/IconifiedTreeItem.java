package lol.koblizek.bytelens.api.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        valueProperty().addListener((observable, oldValue, newValue) -> {
            updateGraphics(path);
        });
        setValue(value.getFileName().toString());
    }

    public void updateGraphics() {
        updateGraphics(path);
    }

    public void updateGraphics(Path p) {
        // Do the caching trickery here as well, so we save some resources
        // if (FilenameUtils.getExtension(p.getFileName().toString()).equals(FilenameUtils.getExtension(path.getFileName().toString())))
        //            return;
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
}
