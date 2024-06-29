package lol.koblizek.bytelens.api.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import org.slf4j.LoggerFactory;

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
            LoggerFactory.getLogger(getClass()).error("Failed to load", e);
        }
    }

    @FXML
    public void initialize() {
        
    }
}
