package lol.koblizek.bytelens.core.controllers;

import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.SideToolBar;
import lol.koblizek.bytelens.api.ui.SideToolButton;
import lol.koblizek.bytelens.api.ui.ToolBarButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainViewController implements Controller {

    public MenuBar menubar;
    public SideToolBar leftToolbar;
    public ToolBar leftPanelTop;
    public Label leftPanelTopTitle;
    public AnchorPane leftPanel;

    @Override
    public void initialize() {
        var collected = instance().getToolWindows().stream()
                .collect(Collectors.groupingBy(ToolWindow::placement));
        for (Map.Entry<ToolWindow.Placement, List<ToolWindow>> entry : collected.entrySet()) {
            ToolWindow.Placement placement = entry.getKey();
            for (ToolWindow toolWindow : entry.getValue()) {
                if (placement == ToolWindow.Placement.LEFT) {
                    leftToolbar.addToolButton(new SideToolButton(toolWindow.icon()), 0);
                }
            }
//            if (entry.getKey() == ToolWindow.Placement.LEFT) {
//                for (ToolWindow tw : entry.getValue()) {
//                    ToolBarButton bt = getButton(tw);
//                    if (entry.getValue().get(0).equals(tw)) {
//                        leftPanelTopTitle.setText(tw.name());
//                        bt.setSelected(true);
//                    }
//                    leftToolbar.getItems().add(bt);
//                }
//            } else if (entry.getKey() == ToolWindow.Placement.BOTTOM) {
//                Pane spacer = new Pane();
//                VBox.setVgrow(spacer, Priority.ALWAYS);
//                leftToolbar.getItems().add(spacer);
//                for (ToolWindow tw : entry.getValue()) {
//                    ToolBarButton bt = getButton(tw);
//                    if (entry.getValue().get(0).equals(tw)) {
//                        leftPanelTopTitle.setText(tw.name());
//                        bt.setSelected(true);
//                    }
//                    leftToolbar.getItems().add(bt);
//                }
//            }
        }
    }

    private @NotNull ToolBarButton getButton(ToolWindow tw) {
        ToolBarButton bt = new ToolBarButton(tw.icon());
        bt.setupListening((observable, oldValue, newValue) -> {
            if (newValue) {
                if (leftPanel.getChildren().size() > 1)
                    leftPanel.getChildren().remove(1, leftPanel.getChildren().size() - 1);
                if (tw.node() != null && tw.name() != null) {
                    leftPanel.getChildren().add(tw.node());
                    leftPanelTopTitle.setText(tw.name());

                }
            }
        });
        return bt;
    }
}
