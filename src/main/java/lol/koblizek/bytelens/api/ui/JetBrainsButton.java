package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import lol.koblizek.bytelens.api.resource.ResourceManager;

public class JetBrainsButton extends Button {

    private final StringProperty icon;

    public JetBrainsButton() {
        super();
        var res = ResourceManager.getInstance();
        getStyleClass().add("jetbrains-button");
        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        icon = new SimpleStringProperty();
        imageView.imageProperty().bind(icon.map(x -> res.getJBIcon(x, true).toSVG()));
        setGraphic(imageView);
    }

    public JetBrainsButton(StringProperty icon) {
        super();
        var res = ResourceManager.getInstance();
        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        this.icon = icon;
        imageView.imageProperty().bind(icon.map(x -> res.getJBIcon(x, true).toSVG()));
        setGraphic(imageView);
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    public String getIcon() {
        return icon.get();
    }
}
