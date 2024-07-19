package lol.koblizek.bytelens.api.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;

public class IconifiedMenuItem extends MenuItem {

    private final StringProperty icon;

    public IconifiedMenuItem() {
        super();
        this.icon = new SimpleStringProperty();
        graphicProperty().bind(icon.map(JetBrainsImage::new));
    }

    public StringProperty iconProperty() {
        return icon;
    }

    public String getIcon() {
        return icon.get();
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }
}
