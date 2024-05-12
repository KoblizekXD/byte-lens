package lol.koblizek.bytelens.api.ui;

import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.skin.SplitPaneSkin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lol.koblizek.bytelens.api.ToolWindow;

public class SideToolButton extends ToggleButton {

    private Pane controlledPane;
    private double dividerPosition;

    public SideToolButton(ToolWindow tw, Pane controlledPane) {
        super();
        ImageView imageView = new ImageView(tw.icon());
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setGraphic(imageView);
        getStyleClass().add("tool-bar-button");
        this.controlledPane = controlledPane;
    }

    public Pane getControlledPane() {
        return controlledPane;
    }

    public void setControlledPane(Pane controlledPane) {
        this.controlledPane = controlledPane;
    }
}
