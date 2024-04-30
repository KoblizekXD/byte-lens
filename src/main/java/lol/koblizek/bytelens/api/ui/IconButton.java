package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends Button {

    private StringProperty icon;

    public IconButton() {
        super();
        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        icon = new SimpleStringProperty();
        imageView.imageProperty().bind(icon.map(Image::new));
        setGraphic(imageView);
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    public String getIcon() {
        return icon.get();
    }
}
