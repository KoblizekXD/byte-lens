package lol.koblizek.bytelens.api.ui;

import javafx.beans.NamedArg;
import javafx.scene.control.Menu;

public class IconMenu extends Menu {
    public IconMenu(@NamedArg("icon") String icon) {
        super();
        var x = new JetBrainsImage(icon);
        x.setStyle("-fx-border-color: red");
        setGraphic(x);
    }
}
