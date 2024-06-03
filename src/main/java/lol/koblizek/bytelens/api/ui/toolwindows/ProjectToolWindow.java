package lol.koblizek.bytelens.api.ui.toolwindows;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.core.ByteLens;

import java.io.IOException;

public class ProjectToolWindow extends AnchorPane implements ToolWindow.ToolWindowNode {
    @Override
    public Node create(ByteLens byteLens) throws IOException {
        var loader = byteLens.getResourceManager().get("/lol/koblizek/bytelens/components/project-tw.fxml")
                .toLoader();
        loader.setRoot(this);
        loader.setController(this);
        return loader.load();
    }
}
