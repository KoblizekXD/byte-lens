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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;

public class LigmaContextMenu extends ContextMenu {

    private final ObjectProperty<EventHandler<ContextMenuEvent>> onContextMenuRequested = new ObjectPropertyBase<EventHandler<ContextMenuEvent>>() {

        @Override
        protected void invalidated() {
            setEventHandler(CONTEXT_MENU_REQUESTED, get());
        }

        @Override
        public Object getBean() {
            return LigmaContextMenu.this;
        }

        @Override
        public String getName() {
            return "contextMenuRequested";
        }
    };
    private static final EventType<ContextMenuEvent> CONTEXT_MENU_REQUESTED = new EventType<>(ContextMenuEvent.ANY, "CONTEXT_MENU_REQUESTED");

    public LigmaContextMenu() {
        super();
    }

    public ObjectProperty<EventHandler<ContextMenuEvent>> onContextMenuRequestedProperty() {
        return onContextMenuRequested;
    }

    public EventHandler<ContextMenuEvent> getOnContextMenuRequested() {
        return onContextMenuRequested.get();
    }

    public void setOnContextMenuRequested(EventHandler<ContextMenuEvent> value) {
        onContextMenuRequested.set(value);
    }

    public static EventType<ContextMenuEvent> contextMenuRequested() {
        return CONTEXT_MENU_REQUESTED;
    }
}
