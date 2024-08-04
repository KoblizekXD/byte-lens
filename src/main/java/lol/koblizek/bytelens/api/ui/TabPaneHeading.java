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

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TabPaneHeading extends VBox {

    private IconifiedTreeItem item;

    @FXML private JetBrainsImage headIcon;
    @FXML private Label headLabel;
    @FXML private JetBrainsButton closeTab;
    @FXML private Pane headingSelector;

    public TabPaneHeading(IconifiedTreeItem item) {
        super();
        this.item = item;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/tab-pane-heading.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to load", e);
        }
    }

    @FXML
    public void initialize() {
        if (item.getGraphic() != null)
            headIcon.setImage(((JetBrainsImage) item.getGraphic()).getImage());
        else headIcon.setIcon("AllIcons.Expui.FileTypes.Text");
        headLabel.setText(item.getValue());
    }

    public void setCloseTabHandler(EventHandler<ActionEvent> e) {
        closeTab.setOnAction(e);
    }

    public void setClickTabHandler(EventHandler<MouseEvent> e) {
        setOnMouseClicked(e);
    }

    public void setSelected(boolean state) {
        headingSelector.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), state);
    }

    public boolean isSelected() {
        return headingSelector.getPseudoClassStates().contains(PseudoClass.getPseudoClass("selected"));
    }
}
