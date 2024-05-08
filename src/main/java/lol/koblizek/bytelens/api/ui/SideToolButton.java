package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SideToolButton extends ToggleButton {

    private IntegerProperty toggleGroupIndex;

    public SideToolButton(Image img) {
        super();
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setGraphic(imageView);
        getStyleClass().add("tool-bar-button");
    }

    public int getToggleGroupIndex() {
        return toggleGroupIndex.get();
    }

    public void setToggleGroupIndex(int toggleGroupIndex) {
        this.toggleGroupIndex.set(toggleGroupIndex);
    }

    public IntegerProperty toggleGroupIndexProperty() {
        if (toggleGroupIndex == null) {
            toggleGroupIndex = new SimpleIntegerProperty(0);
        }
        return toggleGroupIndex;
    }
}
