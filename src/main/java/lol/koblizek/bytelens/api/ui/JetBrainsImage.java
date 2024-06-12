package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import lol.koblizek.bytelens.api.resource.ResourceManager;

public class JetBrainsImage extends ImageView {

    private StringProperty icon;
    private IntegerProperty iconSize;

    public JetBrainsImage(String icon, int width, int height) {
        if (!icon.isBlank())
            this.icon = new SimpleStringProperty(icon);
        else this.icon = new SimpleStringProperty();
        this.iconSize = new SimpleIntegerProperty(16);

        fitWidthProperty().bindBidirectional(iconSize);
        fitHeightProperty().bindBidirectional(iconSize);
        setFitWidth(width);
        setFitHeight(height);
        imageProperty().bind(this.icon.map(s -> ResourceManager.getJBIcon(s, true, iconSize.get(), iconSize.get())));
    }

    public JetBrainsImage(String icon) {
        this(icon, 16, 16);
    }

    public JetBrainsImage() {
        this("");
    }

    public String getIcon() {
        return icon.get();
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    public IntegerProperty iconSizeProperty() {
        return iconSize;
    }

    public StringProperty iconProperty() {
        return icon;
    }

    public int getIconSize() {
        return iconSize.get();
    }

    public void setIconSize(int iconSize) {
        this.iconSize.set(iconSize);
    }
}
