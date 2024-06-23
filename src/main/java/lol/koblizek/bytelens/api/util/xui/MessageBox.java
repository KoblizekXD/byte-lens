package lol.koblizek.bytelens.api.util.xui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import lol.koblizek.bytelens.core.ByteLens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility message box class for displaying messages to the user.
 * Works as replacement for JavaFX's Alert class.
 *
 * @see Status
 * @see Button
 */
public class MessageBox extends Dialog<MessageBox.Button> {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageBox.class);
    private static ByteLens byteLens;
    private final Window owner;
    private final String title;
    private final String description;
    private final Status status;
    private final Button[] buttons;
    private Stage stage;

    /**
     * Different types of message box statuses. Each of the statuses
     * has its own icon.
     */
    public enum Status {
        INFO,
        WARNING,
        ERROR;

        /**
         * @return the default status for the message box if none is specified
         */
        public static Status getDefault() {
            return INFO;
        }
    }

    /**
     * Different types of buttons that can be displayed in the message box.
     */
    public enum Button {
        OK,
        CANCEL,
        YES,
        NO;

        /**
         * @return the default button for the message box if none is specified
         */
        public static Button getDefault() {
            return OK;
        }
    }

    public static void init(ByteLens byteLens) {
        if (MessageBox.byteLens != null) {
            throw new IllegalStateException("ByteLens has already been initialized");
        }
        MessageBox.byteLens = byteLens;
    }

    private MessageBox(Window owner, String title, String description, Status status, Button... buttons) {
        super();
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.status = status;
        this.buttons = buttons;

        try {
            this.stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/views/message-box.fxml"));
            loader.setController(this);
            this.stage.setScene(new Scene(loader.load()));
            if (owner != null)
                this.stage.initOwner(owner);
            else if (byteLens.getPrimaryStage() != null)
                this.stage.initOwner(byteLens.getPrimaryStage());
            this.stage.setAlwaysOnTop(true);
            this.stage.initModality(Modality.WINDOW_MODAL);
            this.stage.setTitle(title);
        } catch (Exception e) {
            LOGGER.error("Failed to load message box FXML", e);
        }
    }

    public static Button show(String title, String description, Status status, Button... buttons) {
        return show(null, title, description, status, buttons);
    }

    public static Button show(String title, String description, Status status) {
        return show(title, description, status, Button.getDefault());
    }

    public static Button show(String title, String description) {
        return show(title, description, Status.getDefault());
    }

    public static Button show(String description) {
        return show("Message", description);
    }

    public static Button show(Window owner, String title, String description, Status status, Button... buttons) {
        LOGGER.debug("Showing message box with title: {}, description: {}, status: {}, buttons: {}", title, description, status, buttons);
        return new MessageBox(owner, title, description, status, buttons).showNow();
    }

    public static Button show(Window owner, String title, String description, Status status) {
        return show(owner, title, description, status, Button.getDefault());
    }

    public static Button show(Window owner, String title, String description) {
        return show(owner, title, description, Status.getDefault());
    }

    public static Button show(Window owner, String description) {
        return show(owner, "Message", description);
    }

    @FXML
    public JetBrainsImage icon;
    @FXML
    public Label message;

    @FXML
    public void initialize() {
        // icon.setIcon("AllIcons.Expui.General.Settings");
        stage.setOnCloseRequest(e -> {

        });
    }

    public Button showNow() {
        stage.showAndWait();
        return getResult();
    }
}
