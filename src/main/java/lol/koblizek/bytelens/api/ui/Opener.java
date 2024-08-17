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
import javafx.scene.Node;

/**
 * Interface, whose implementations are responsible for opening a given node.
 */
public interface Opener {
    /**
     * Opens the given node. This operation can open the node in a new window, tab, etc.
     * It all depends on the implementation.
     * @param node Node to be opened.
     * @param bindableName Optional name of the node. It might or might not be used by the implementation.
     */
    void open(Property<String> bindableName, Node node);
}
