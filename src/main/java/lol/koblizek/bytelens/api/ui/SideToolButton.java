/*
 * This file is part of byte-lens, licensed under the GNU General Public License v3.0.
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

import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import lol.koblizek.bytelens.api.ToolWindow;
import org.jetbrains.annotations.NotNull;

public class SideToolButton extends ToggleButton {

    private final ToolWindow tw;
    private SidePane controlledPane;
    private PersistentSplitPane splitPane;

    public SideToolButton(ToolWindow tw, SidePane controlledPane) {
        super();
        this.tw = tw;
        ImageView imageView = new ImageView(tw.icon());
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setGraphic(imageView);
        getStyleClass().add("tool-bar-button");

        setOnMouseClicked(e -> {
            if (isSelected()) {
                controlledPane.getSplitPane().showPane(controlledPane);
            } else {
                controlledPane.onHide();
            }
        });

        this.controlledPane = controlledPane;
    }

    public ToolWindow getToolWindow() {
        return tw;
    }

    public SidePane getControlledPane() {
        return controlledPane;
    }

    public void setControlledPane(SidePane controlledPane) {
        this.controlledPane = controlledPane;
    }

    public void setSplitPane(@NotNull PersistentSplitPane splitPane) {
        this.splitPane = splitPane;
    }
}
