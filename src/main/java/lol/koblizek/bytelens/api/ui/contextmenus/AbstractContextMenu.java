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
