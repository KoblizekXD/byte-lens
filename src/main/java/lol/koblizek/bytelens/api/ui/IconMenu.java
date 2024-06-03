package lol.koblizek.bytelens.api.ui;

import javafx.beans.NamedArg;
import javafx.scene.control.Menu;

public class IconMenu extends Menu {
    public IconMenu(@NamedArg("icon") String icon) {
        getStyleClass().add("icon-menu");
        var jb = new JetBrainsButton(icon);
        jb.getStyleClass().clear();
        setGraphic(jb);
    }
}
