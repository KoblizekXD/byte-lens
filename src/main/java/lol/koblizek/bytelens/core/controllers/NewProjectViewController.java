package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import lol.koblizek.bytelens.api.ui.PathField;
import lol.koblizek.bytelens.api.util.ProjectCreator;
import lol.koblizek.bytelens.core.ByteLens;

import java.nio.file.Path;

import static javafx.scene.layout.Priority.ALWAYS;

public class NewProjectViewController extends Controller {

    @FXML public ListView<ProjectCreator> projectTypeListing;
    @FXML public HBox userdata;

    public NewProjectViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        getByteLens().getProjectTypes().forEach(projectTypeListing.getItems()::add);
        projectTypeListing.getSelectionModel().select(0);
        projectTypeListing.getSelectionModel().selectedItemProperty().subscribe(menu -> {
            if (menu == null) return;
            VBox.setVgrow(generateNode(menu), ALWAYS);
            if (userdata.getChildren().size() == 2) {
                userdata.getChildren().set(1, generateNode(menu));
            } else {
                userdata.getChildren().add(generateNode(menu));
            }
        });
    }

    private Node generateNode(ProjectCreator creator) {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 0, 0, 10));
        gridPane.setHgap(20);
        gridPane.setVgap(5);
        creator.getFields().forEach((name, type) -> {
            Node node = generateNode(type);
            gridPane.addRow(gridPane.getChildren().size(), new Label(name), node);
        });
        return gridPane;
    }

    private Node generateNode(Class<?> type) {
        Region r;
        if (type == String.class || type == int.class) {
            r = new TextField();
            r.getStyleClass().add("new-project-text-field");
        } else if (type == Path.class) {
            r = new PathField();
        } else if (type == Boolean.class) {
            r = new CheckBox();
        } else {
            r = new Label("Unsupported type: " + type);
            r.setStyle("-fx-border-color: red");
        }
        r.prefWidth(200);
        return r;
    }
}
