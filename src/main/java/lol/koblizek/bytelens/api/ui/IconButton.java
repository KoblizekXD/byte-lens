package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends Button {

    private StringProperty iconProperty;

    public IconButton() {
        super();
        ImageView imageView = new ImageView();
        iconProperty = new SimpleStringProperty();
        imageView.imageProperty().bind(iconProperty.map(Image::new));
        setGraphic(imageView);
    }

    public void setIconProperty(String iconProperty) {
        this.iconProperty.set(iconProperty);
    }

    public String getIconProperty() {
        return iconProperty.get();
    }
}
