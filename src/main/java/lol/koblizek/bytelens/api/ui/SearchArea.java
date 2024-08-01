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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SearchArea extends StackPane {

    @FXML private TextField actualTextField;

    private final ObjectProperty<EventHandler<KeyEvent>> onAction = new ObjectPropertyBase<>() {

        @Override
        protected void invalidated() {
            setEventHandler(KeyEvent.KEY_TYPED, get());
        }

        @Override
        public Object getBean() {
            return SearchArea.this;
        }

        @Override
        public String getName() {
            return "onKeyTyped";
        }
    };

    public SearchArea() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/search-area.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to load", e);
        }
    }

    @FXML
    private void initialize() {
        actualTextField.onKeyTypedProperty().bind(onAction);
    }

    public String getText() {
        return actualTextField.getText();
    }

    public final ObjectProperty<EventHandler<KeyEvent>> onActionProperty() { return onAction; }
    public final EventHandler<KeyEvent> getOnAction() { return onActionProperty().get(); }
    public final void setOnAction(EventHandler<KeyEvent> value) { onActionProperty().set(value); }
}