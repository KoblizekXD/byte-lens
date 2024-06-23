package lol.koblizek.bytelens.api.ui.toolwindows;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import lol.koblizek.bytelens.core.ByteLens;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ProjectToolWindow extends TreeView<String> implements ToolWindow.ToolWindowNode {

    @FXML public TreeItem<String> root;

    private ByteLens byteLens;

    @Override
    public Node create(ByteLens byteLens) throws IOException {
        this.byteLens = byteLens;
        var loader = byteLens.getResourceManager().get("components/project-tw.fxml")
                .toLoader();
        loader.setRoot(this);
        loader.setController(this);
        return loader.load();
    }

    @FXML
    public void initialize() {
        Optional<DefaultProject> optionalProject = byteLens.getCurrentProject();
        optionalProject.ifPresentOrElse(project -> {
            root.setValue(project.getName());
            root.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.ModuleGroup"));
            appendTreeItem(root, project.getProjectFile().getFileName().toString(), item -> {
                item.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Json"));
            });
            appendTreeItem(root, "Sources", item -> {
                item.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Module"));
                item.getChildren().add(getModule(project.getSources()));
            });
            appendTreeItem(root, "Resources", item -> {
                item.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Module"));
                item.getChildren().add(getModule(project.getResources()));
            });
            appendTreeItem(root, "External Libraries", item -> {
                item.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Module"));
                item.getChildren().add(getModule(project.getReferenceLibraries()));
            });
            appendTreeItem(root, "Workspace", item -> item.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Module")));
        }, () -> {
            root.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.ErrorIntroduction"));
            root.setValue("No module/project is open");
        });
    }

    private TreeItem<String> appendTreeItem(TreeItem<String> parent, String value) {
        var item = new TreeItem<>(value);
        parent.getChildren().add(item);
        return item;
    }

    private TreeItem<String> appendTreeItem(TreeItem<String> parent, String value, Consumer<TreeItem<String>> configurator) {
        var item = new TreeItem<>(value);
        configurator.accept(item);
        parent.getChildren().add(item);
        return item;
    }

    private void modifyFileNode(Path p, TreeItem<String> child) {
        if (Files.isDirectory(p)) {
            child.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Folder"));
            return;
        }
        String ext = FilenameUtils.getExtension(p.toString());
        switch (ext) {
            case "java" -> child.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Java"));
            case "class" -> child.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Class"));
            case "jar" -> child.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Archive"));
            default -> child.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Text"));
        }
    }

    private TreeItem<String> getModule(Path rootPath) {
        TreeItem<String> rootItem = new TreeItem<>(rootPath.getFileName().toString());
        modifyFileNode(rootPath, rootItem);
        try {
            Files.list(rootPath)
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            rootItem.getChildren().add(getModule(path));
                        } else {
                            rootItem.getChildren().add(new TreeItem<>(path.getFileName().toString()));
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rootItem;
    }
}
