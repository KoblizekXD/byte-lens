package lol.koblizek.bytelens.api.ui.toolwindows;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.JetBrainsButton;
import lol.koblizek.bytelens.core.ByteLens;

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
            appendTreeItem(root, "Sources");
            appendTreeItem(root, "Resources");
            appendTreeItem(root, "External Libraries");
            appendTreeItem(root, "Workspace");
        }, () -> {
            root.setGraphic(new JetBrainsButton("AllIcons.Expui.Nodes.ErrorIntroduction"));
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
                item.setGraphic(new JetBrainsButton("AllIcons.Expui.Nodes.Module"));
                item.getChildren().add(new TreeItem<>("Loading..."));
                item.setExpanded(true);
            } else {
                item.setGraphic(new JetBrainsButton("AllIcons.Expui.Nodes.Module"));
            }
            return item;
        }).toList();
    }
}
