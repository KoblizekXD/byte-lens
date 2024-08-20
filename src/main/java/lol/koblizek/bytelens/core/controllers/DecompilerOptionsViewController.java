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

package lol.koblizek.bytelens.core.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.decompiler.api.Decompiler;
import lol.koblizek.bytelens.core.decompiler.api.Option;
import lol.koblizek.bytelens.core.utils.StringUtils;

public class DecompilerOptionsViewController extends Controller {

    @FXML private Label editingLabel;
    @FXML private TableView<ObservableBasedOption> optionGrid;
    @FXML private TableColumn<ObservableBasedOption, String> nameCol;
    @FXML private TableColumn<ObservableBasedOption, String> shortNameCol;
    @FXML private TableColumn<ObservableBasedOption, String> descCol;
    @FXML private TableColumn<ObservableBasedOption, String> valueCol;

    public DecompilerOptionsViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        editingLabel.setText("Editing options for "
                + getByteLens().getDecompilationManager().getProvider()
                + " "
                + getByteLens().getDecompilationManager().getVersion());

        nameCol.setCellValueFactory(tv -> tv.getValue().name());
        nameCol.setCellFactory(tc -> createDefault());
        shortNameCol.setCellValueFactory(tv -> tv.getValue().id());
        shortNameCol.setCellFactory(tc -> createDefault());
        descCol.setCellValueFactory(tv -> tv.getValue().desc());
        descCol.setCellFactory(tc -> createDefault());
        valueCol.setCellFactory(tc -> createModifiable());
        valueCol.setCellValueFactory(tv -> tv.getValue().defaultValue().asString());
        Decompiler decompiler = getByteLens().getDecompilationManager().getDecompiler();
        for (Option supportedOption : decompiler.getSupportedOptions()) {
            optionGrid.getItems().add(new ObservableBasedOption(supportedOption));
        }
    }

    private TableCell<ObservableBasedOption, String> createDefault() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTooltip(new Tooltip(item));
            }
        };
    }

    private TextFieldTableCell<ObservableBasedOption, String> createModifiable() {
        return new TextFieldTableCell<>(new DefaultStringConverter()) {
            @Override
            public void commitEdit(String newValue) {
                super.commitEdit(newValue);
                postCommitUpdate(newValue);
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (StringUtils.isNumber(item)) {
                    CheckBox cb = new CheckBox();
                    cb.setSelected(StringUtils.stringNumberToBoolean(item));
                    cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        commitEdit(Boolean.TRUE.equals(newVal) ? "1" : "0");
                    });
                    setGraphic(cb);
                    setText(null);
                } else setText(item);
            }

            private void postCommitUpdate(String item) {
                getLogger().debug("Post commit update with item: {}", item);
                if (item.equals("1")) {
                    ((CheckBox) getGraphic()).setSelected(true);
                } else if (item.equals("0")) {
                    ((CheckBox) getGraphic()).setSelected(false);
                } else {
                    super.updateItem(item, false);
                    setText(item);
                    setGraphic(null);
                }
            }
        };
    }

    @FXML
    public void saveConfiguration(ActionEvent event) {
        getByteLens().getDecompilationManager().saveConfiguration();
        editingLabel.getScene().getWindow().hide();
    }

    record ObservableBasedOption(StringProperty id, StringProperty name, StringProperty desc, StringProperty shortName, ObjectProperty<?> defaultValue) {
        public ObservableBasedOption(Option dOption) {
            this(new SimpleStringProperty(dOption.id()),
                    new SimpleStringProperty(dOption.name()),
                    new SimpleStringProperty(dOption.desc()),
                    new SimpleStringProperty(dOption.shortName()),
                    new SimpleObjectProperty<>(dOption.defaultValue() == null ? "" : dOption.defaultValue())
            );
        }
    }
}
