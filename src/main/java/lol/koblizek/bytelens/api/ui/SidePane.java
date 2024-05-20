package lol.koblizek.bytelens.api.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lol.koblizek.bytelens.api.util.InstanceAccessor;

import java.io.IOException;

public class SidePane extends AnchorPane implements InstanceAccessor {

    @FXML private Label titleLabel;

    public SidePane() {
        var loader = resource("/lol/koblizek/bytelens/components/side-pane.fxml").toLoader();
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
