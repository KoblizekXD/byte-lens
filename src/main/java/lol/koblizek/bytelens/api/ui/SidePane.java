package lol.koblizek.bytelens.api.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SidePane extends AnchorPane {

    @FXML private Label titleLabel;
    @FXML private Pane contentHolder;
    private PersistentSplitPane cachedSplitPane;

    public SidePane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/side-pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setContents(Node node) {
        contentHolder.getChildren().setAll(node);
    }

    @FXML
    public void onHide() {
        var splitPane = findSplitPane();
        if (splitPane != null) {
            splitPane.hidePane(this);
        }
    }

    private PersistentSplitPane findSplitPane() {
        var parent = getParent();
        while (parent != null && !(parent instanceof PersistentSplitPane)) {
            parent = parent.getParent();
        }
        cachedSplitPane = (PersistentSplitPane) parent;
        return cachedSplitPane;
    }

    public PersistentSplitPane getSplitPane() {
        return cachedSplitPane;
    }
}
