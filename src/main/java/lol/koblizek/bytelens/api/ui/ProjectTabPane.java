package lol.koblizek.bytelens.api.ui;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectTabPane extends VBox {

    @FXML private HBox itemHolder;
    @FXML private AnchorPane freeSpace;

    private final List<Tab> tabs = new ArrayList<>();

    public ProjectTabPane() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/project-tab-pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to load", e);
        }
    }

    public void addTab(IconifiedTreeItem treeItem, Node node) {
        var heading = new TabPaneHeading(treeItem);

        heading.setCloseTabHandler(e ->
                removeTab(tabs.indexOf(tabs.stream().filter(t -> t.item == treeItem).findFirst().orElse(null))));
        heading.setClickTabHandler(e ->
                setActiveTab(tabs.indexOf(tabs.stream().filter(t -> t.item == treeItem).findFirst().orElse(null))));

        itemHolder.getChildren().add(heading);

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        tabs.addLast(new Tab(treeItem, heading, node));
        setActiveTab(tabs.size() - 1);
    }

    private void removeTab(int i) {
        itemHolder.getChildren().remove(i);
        tabs.remove(i);
        if (!tabs.isEmpty() && getCurrentTab() == null) {
            setActiveTab(0);
        } else {
            freeSpace.getChildren().clear();
        }
    }

    private Tab getCurrentTab() {
        return tabs.stream().filter(Tab::isActive).findFirst().orElse(null);
    }

    public void setActiveTab(int i) {
        for (int j = 0; j < tabs.size(); j++) {
            tabs.get(j).setActive(i == j);
            if (i == j) {
                freeSpace.getChildren().setAll(tabs.get(j).content);
            }
        }
    }

    record Tab(IconifiedTreeItem item, TabPaneHeading heading, Node content) {
        public void setActive(boolean state) {
            heading.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), state);
        }
        public boolean isActive() {
            return heading.getPseudoClassStates().contains(PseudoClass.getPseudoClass("selected"));
        }
    }
}
