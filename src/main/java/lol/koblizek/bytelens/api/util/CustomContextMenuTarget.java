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

package lol.koblizek.bytelens.api.util;

import javafx.scene.control.ContextMenu;
import lol.koblizek.bytelens.api.ui.DefaultContextMenu;

/**
 * Interface for classes that can provide a custom context menu.
 */
public interface CustomContextMenuTarget {

    default boolean apply() {
        return false;
    }

    default ContextMenu getContextMenu() {
        return new DefaultContextMenu();
    }
}
