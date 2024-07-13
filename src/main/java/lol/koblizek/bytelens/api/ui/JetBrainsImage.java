/*
Copyright (c) 2024 KoblizekXD

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

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
        if (!icon.isBlank()) {
            this.icon = new SimpleStringProperty(icon);
        } else {
            this.icon = new SimpleStringProperty();
        }
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
