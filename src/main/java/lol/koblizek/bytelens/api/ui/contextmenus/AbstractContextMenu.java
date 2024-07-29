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

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Wrapper class for generic context menu holders
 */
public abstract class AbstractContextMenu extends ContextMenu {
    protected AbstractContextMenu(String fxmlPath) {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to load context menu: ", e);
        }
    }

    /**
     * Create a new instance of the context menu
     * @param fxmlPath Path to the FXML file
     * @return A new instance of the context menu
     */
    public static AbstractContextMenu create(String fxmlPath) {
        return new AbstractContextMenu(fxmlPath) {};
    }
}
