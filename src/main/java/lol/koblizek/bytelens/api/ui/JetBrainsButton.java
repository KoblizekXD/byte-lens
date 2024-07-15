/*
 * This file is part of byte-lens.
 *
 * Copyright (c) 2024 KoblizekXD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
        getStyleClass().clear();
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
