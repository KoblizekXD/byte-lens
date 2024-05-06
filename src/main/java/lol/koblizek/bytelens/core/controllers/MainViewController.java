package lol.koblizek.bytelens.core.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import lol.koblizek.bytelens.api.ui.ToolBarButton;

public class MainViewController implements Controller {


    public MenuBar menubar;
    public ToolBar leftToolbar;
    public ToolBar leftPanelTop;
    public Label leftPanelTopTitle;
    public AnchorPane leftPanel;

    @Override
    public void initialize() {
        for (int i = 0; i < instance().getToolWindows().size(); i++) {
            ToolBarButton bt = new ToolBarButton(instance().getToolWindows().get(i).icon());
            int finalI = i;
            bt.setupListening((observable, oldValue, newValue) -> {
                if (newValue) {
                    if (leftPanel.getChildren().size() > 1)
                        leftPanel.getChildren().remove(1, leftPanel.getChildren().size() - 1);
                    leftPanel.getChildren().add(instance().getToolWindows().get(finalI).node());
                }
            });
            if (i == 0)
                bt.setSelected(true);
            leftToolbar.getItems().add(bt);
        }
    }
}
