package lol.koblizek.bytelens.api.ui.toolwindows;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.ExtendedCodeArea;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import lol.koblizek.bytelens.api.ui.Opener;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.StandardDirectoryWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

public class ProjectTreeToolWindow extends TreeView<String> implements ToolWindow.ToolWindowNode {

    private final Opener opener;
    @FXML private TreeItem<String> root;

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
            this.getSelectionModel().selectedItemProperty().addListener(this::itemSelectionEvent);
            root.setValue(project.getName());
            root.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.ModuleGroup"));
            appendTreeItem(root, project.getProjectFile().getFileName().toString(), item ->
                    item.setGraphic(new JetBrainsImage("AllIcons.Expui.FileTypes.Json")));
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

    private void appendTreeItem(TreeItem<String> parent, String value, Consumer<TreeItem<String>> configurator) {
        var item = new TreeItem<>(value);
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

    private void itemSelectionEvent(ObservableValue<? extends TreeItem<String>> observableValue, TreeItem<String> oldV, TreeItem<String> newV) {
        if (newV == null) {
            return;
        }
        ExtendedCodeArea codeArea = new ExtendedCodeArea();
        codeArea.bridge(byteLens);
        if (newV instanceof IconifiedTreeItem iti && !iti.isDirectory()) { // This means it's actual file/dir
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
