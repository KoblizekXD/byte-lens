package lol.koblizek.bytelens.api.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SearchArea extends StackPane {
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
}