package lol.koblizek.bytelens.core.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.JetBrainsButton;
import lol.koblizek.bytelens.api.ui.ToolBarButton;

public class MainViewController implements Controller {


    public MenuBar menubar;
    public ToolBar leftToolbar;

    @Override
    public void initialize() {
        for (ToolWindow tw : instance().getToolWindows()) {
            leftToolbar.getItems()
                    .add(new ToolBarButton(tw.icon()));
        }
    }
}
