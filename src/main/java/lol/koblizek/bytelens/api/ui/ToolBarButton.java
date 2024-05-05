package lol.koblizek.bytelens.api.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ToolBarButton extends ToggleButton {
    public ToolBarButton(Image image) {
        super();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setGraphic(imageView);
        getStyleClass().add("tool-bar-button");

        onMouseClickedProperty().set(e -> {
            getParent().getChildrenUnmodifiable().forEach(node -> {
                if (node instanceof ToolBarButton b && b != this) {
                    b.setSelected(false);
                } else {
                    setSelected(true);
                }
            });
        });
    }
}
