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

package lol.koblizek.bytelens.api.ui.contextmenus;

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

import java.util.Optional;

/**
 * Container which can hold multiple context menus.
 */
@DefaultProperty("items")
public class ContextMenuContainer extends Node {

    private final ObservableList<ContextMenu> items;

    public ContextMenuContainer() {
        items = FXCollections.observableArrayList();
    }

    public ObservableList<ContextMenu> itemsProperty() {
        return items;
    }

    public ObservableList<ContextMenu> getItems() {
        return items;
    }

    public Optional<ContextMenu> findById(String id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst();
    }
}
