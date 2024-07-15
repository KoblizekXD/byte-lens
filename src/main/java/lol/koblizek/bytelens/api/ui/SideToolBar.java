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

import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SideToolBar extends ToolBar {

    private final List<ToggleGroup> groups;

    public SideToolBar() {
        super();
        this.groups = new ArrayList<>();
    }

    public void addToolButton(SideToolButton button, int group) {
        ToggleGroup gp;
        if (group >= this.groups.size()) {
            gp = new ToggleGroup();
            this.groups.add(gp);
            if (group > 0) {
                Pane pane = new Pane();
                VBox.setVgrow(pane, Priority.ALWAYS);
                this.getItems().add(pane);
            }
        } else {
            gp = this.groups.get(group);
        }
        button.setToggleGroup(gp);
        if (gp.getToggles().size() == 1) {
            button.setSelected(true);
            button.getControlledPane().setTitle(button.getToolWindow().name());
            button.getControlledPane().setContents(button.getToolWindow().node());
        }
        this.getItems().add(button);
    }
}
