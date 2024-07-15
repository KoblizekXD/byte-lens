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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SidePane extends AnchorPane {

    @FXML private Label titleLabel;
    @FXML private Pane contentHolder;
    private PersistentSplitPane cachedSplitPane;

    public SidePane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/side-pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to load", e);
        }
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setContents(Node node) {
        contentHolder.getChildren().setAll(node);
    }

    @FXML
    public void onHide() {
        var splitPane = findSplitPane();
        if (splitPane != null) {
            splitPane.hidePane(this);
        }
    }

    private PersistentSplitPane findSplitPane() {
        var parent = getParent();
        while (!(parent instanceof PersistentSplitPane)) {
            parent = parent.getParent();
        }
        cachedSplitPane = (PersistentSplitPane) parent;
        return cachedSplitPane;
    }

    public PersistentSplitPane getSplitPane() {
        return cachedSplitPane;
    }
}
