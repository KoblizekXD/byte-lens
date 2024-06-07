package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import static lol.koblizek.bytelens.api.resource.ResourceManager.getJBIcon;

public class JetBrainsButton extends Button {

    private final StringProperty icon;

    public JetBrainsButton() {
        this(new SimpleStringProperty());
    }

    public JetBrainsButton(StringProperty icon) {
        getStyleClass().add("jetbrains-button");
        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        this.icon = icon;
        imageView.imageProperty().bind(icon.map(x -> getJBIcon(x, true)));
        setGraphic(imageView);
    }

    public JetBrainsButton(String icon) {
        this(new SimpleStringProperty(icon));
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    public String getIcon() {
        return icon.get();
    }
}
