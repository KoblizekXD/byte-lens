package lol.koblizek.bytelens.api.ui;

import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import lol.koblizek.bytelens.api.ToolWindow;
import org.jetbrains.annotations.NotNull;

public class SideToolButton extends ToggleButton {

    private final ToolWindow tw;
    private SidePane controlledPane;
    private PersistentSplitPane splitPane;

    public SideToolButton(ToolWindow tw, SidePane controlledPane) {
        super();
        this.tw = tw;
        ImageView imageView = new ImageView(tw.icon());
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setGraphic(imageView);
        getStyleClass().add("tool-bar-button");

        setOnMouseClicked(e -> {
            if (isSelected()) {
                controlledPane.getSplitPane().showPane(controlledPane);
            } else {
                controlledPane.onHide();
            }
        });

        this.controlledPane = controlledPane;
    }

    public ToolWindow getToolWindow() {
        return tw;
    }

    public SidePane getControlledPane() {
        return controlledPane;
    }

    public void setControlledPane(SidePane controlledPane) {
        this.controlledPane = controlledPane;
    }

    public void setSplitPane(@NotNull PersistentSplitPane splitPane) {
        this.splitPane = splitPane;
    }
}
