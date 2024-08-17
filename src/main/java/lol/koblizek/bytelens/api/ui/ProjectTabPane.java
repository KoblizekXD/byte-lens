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

import javafx.beans.property.Property;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectTabPane extends VBox {

    @FXML private HBox itemHolder;
    @FXML private AnchorPane freeSpace;

    private final List<Tab> tabs = new ArrayList<>();

    public ProjectTabPane() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/project-tab-pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to load", e);
        }
    }

    public void addTab(Property<String> title, Node content) {
        var heading = new TabPaneHeading(title);

        heading.setCloseTabHandler(e ->
                removeTab(tabs.indexOf(tabs.stream().filter(t -> t.heading == heading).findFirst().orElse(null))));
        heading.setClickTabHandler(e ->
                setActiveTab(tabs.indexOf(tabs.stream().filter(t -> t.heading == heading).findFirst().orElse(null))));

        itemHolder.getChildren().add(heading);

        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);

        tabs.addLast(new Tab(heading, content));
        setActiveTab(tabs.size() - 1);
    }

    public void removeTab(int i) {
        itemHolder.getChildren().remove(i);
        tabs.remove(i);
        if (!tabs.isEmpty() && getCurrentTab() == null) {
            setActiveTab(0);
        } else {
            if (tabs.isEmpty()) {
                freeSpace.getChildren().clear();
                setItemHolderShowing(false);
            }
        }
    }

    private Tab getCurrentTab() {
        return tabs.stream().filter(Tab::isActive).findFirst().orElse(null);
    }

    public int getActiveTab() {
        return tabs.indexOf(getCurrentTab());
    }

    public void setActiveTab(int i) {
        for (int j = 0; j < tabs.size(); j++) {
            tabs.get(j).setActive(i == j);
            if (i == j) {
                freeSpace.getChildren().setAll(tabs.get(j).content);
                setItemHolderShowing(true);
            }
        }
    }

    private void setItemHolderShowing(boolean state) {
        itemHolder.pseudoClassStateChanged(PseudoClass.getPseudoClass("showing"), state);
    }

    record Tab(TabPaneHeading heading, Node content) {
        public void setActive(boolean state) {
            heading.setSelected(state);
        }
        public boolean isActive() {
            return heading.isSelected();
        }
    }
}
