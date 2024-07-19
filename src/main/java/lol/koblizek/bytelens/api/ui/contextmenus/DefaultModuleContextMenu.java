package lol.koblizek.bytelens.api.ui.contextmenus;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import lol.koblizek.bytelens.core.ByteLens;

public class DefaultModuleContextMenu extends ContextMenu {
    private final ByteLens byteLens;

    public DefaultModuleContextMenu(ByteLens byteLens) {
        super();
        this.byteLens = byteLens;
    }

    @FXML
    public void initialize() {

    }
}
