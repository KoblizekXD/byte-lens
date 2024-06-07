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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ProjectToolWindow extends TreeView<String> implements ToolWindow.ToolWindowNode {

    @FXML public TreeItem<String> root;

    private ByteLens byteLens;

    @Override
    public Node create(ByteLens byteLens) throws IOException {
        this.byteLens = byteLens;
        var loader = byteLens.getResourceManager().get("/lol/koblizek/bytelens/components/project-tw.fxml")
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
            appendTreeItem(root, project.getProjectFile().getFileName().toString());
            appendTreeItem(root, "Sources", item -> {
                project.getSources();
            });
            appendTreeItem(root, "Resources");
            appendTreeItem(root, "External Libraries");
            appendTreeItem(root, "Workspace");
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

    private List<TreeItem<String>> getModule(List<Path> paths) {
        return paths.stream().map(path -> {
            var item = new TreeItem<>(path.getFileName().toString());
            if (Files.isDirectory(path)) {
                item.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Folder"));
                item.setExpanded(true);
                try (var stream = Files.list(path)) {
                    stream.forEach(p -> {
                        var child = new TreeItem<>(p.getFileName().toString());
                        if (Files.isDirectory(p)) {
                            child.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.Folder"));
                            child.setExpanded(true);
                        } else {
                            modifyFileNode(p, child);
                        }
                        item.getChildren().add(child);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                modifyFileNode(path, item);
            }
            return item;
        }).toList();
    }

    private void modifyFileNode(Path p, TreeItem<String> child) {
        String ext = FilenameUtils.getExtension(p.toString());
        switch (ext) {
            case "java" -> child.setGraphic(new JetBrainsImage("AllIcons.FileTypes.Java"));
            case "class" -> child.setGraphic(new JetBrainsImage("AllIcons.FileTypes.Class"));
            case "jar" -> child.setGraphic(new JetBrainsImage("AllIcons.FileTypes.Archive"));
            default -> child.setGraphic(new JetBrainsImage("AllIcons.FileTypes.Text"));
        }
    }
}
