package lol.koblizek.bytelens.api.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TabPaneHeading extends VBox {

    private IconifiedTreeItem item;

    @FXML private JetBrainsImage headIcon;
    @FXML private Label headLabel;
    @FXML private JetBrainsButton closeTab;

    public TabPaneHeading(IconifiedTreeItem item) {
        super();
        this.item = item;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lol/koblizek/bytelens/components/tab-pane-heading.fxml"));
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
        if (item.getGraphic() != null)
            headIcon.setImage(((JetBrainsImage) item.getGraphic()).getImage());
        else headIcon.setIcon("AllIcons.Expui.FileTypes.Text");
        headLabel.setText(item.getValue());
    }

    public void setCloseTabHandler(EventHandler<ActionEvent> e) {
        closeTab.setOnAction(e);
    }

    public void setClickTabHandler(EventHandler<MouseEvent> e) {
        setOnMouseClicked(e);
    }
}
