package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import static lol.koblizek.bytelens.api.resource.ResourceManager.getJBIcon;

public class JetBrainsButton extends Button {

    private final StringProperty icon;
    private final IntegerProperty iconSize;

    public JetBrainsButton() {
        this(new SimpleStringProperty(), new SimpleIntegerProperty(16));
    }

    public JetBrainsButton(StringProperty icon, IntegerProperty iconSize) {
        getStyleClass().add("jetbrains-button");
        ImageView imageView = new ImageView();
        this.icon = icon;
        this.iconSize = iconSize;
        imageView.imageProperty().bind(icon.map(x -> getJBIcon(x, true, iconSize.get(), iconSize.get())));
        setGraphic(imageView);
    }

    public JetBrainsButton(String icon) {
        this(new SimpleStringProperty(icon), new SimpleIntegerProperty(16));
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    public String getIcon() {
        return icon.get();
    }

    public IntegerProperty iconSizeProperty() {
        return iconSize;
    }

    public int getIconSize() {
        return iconSize.get();
    }

    public void setIconSize(int iconSize) {
        this.iconSize.set(iconSize);
    }
}
