package lol.koblizek.bytelens.core.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.decompiler.api.Decompiler;
import lol.koblizek.bytelens.core.decompiler.api.Option;

public class DecompilerOptionsViewController extends Controller {

    @FXML private TableView<Option> optionGrid;
    @FXML private TableColumn<Option, String> nameCol;
    @FXML private TableColumn<Option, String> shortNameCol;
    @FXML private TableColumn<Option, String> descCol;
    @FXML private TableColumn<Option, String> valueCol;

    public DecompilerOptionsViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        nameCol.setCellValueFactory(tv -> new SimpleStringProperty(tv.getValue().name()));
        nameCol.setCellFactory(tc -> createDefault());
        shortNameCol.setCellValueFactory(tv -> new SimpleStringProperty(tv.getValue().shortName()));
        shortNameCol.setCellFactory(tc -> createDefault());
        descCol.setCellValueFactory(tv -> new SimpleStringProperty(tv.getValue().desc()));
        descCol.setCellFactory(tc -> createDefault());
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setCellValueFactory(tv -> new SimpleStringProperty(
                tv.getValue().defaultValue() == null ?
                        "" :
                        tv.getValue().defaultValue().toString()));
        Decompiler decompiler = getByteLens().getDecompilationManager().getDecompiler();
        for (Option supportedOption : decompiler.getSupportedOptions()) {
            optionGrid.getItems().add(supportedOption);
        }
    }

    private TableCell<Option, String> createDefault() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTooltip(new Tooltip(item));
            }
        };
    }
}
