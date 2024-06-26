package lol.koblizek.bytelens.api.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;

import java.io.IOException;

public class DefaultContextMenu extends ContextMenu {

    public DefaultContextMenu() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/default-context-menu.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        
    }
}
