package lol.koblizek.bytelens.core.controllers;

import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.PersistentSplitPane;
import lol.koblizek.bytelens.api.ui.SideToolBar;
import lol.koblizek.bytelens.api.ui.SideToolButton;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainViewController implements Controller {

    public MenuBar menubar;
    public SideToolBar leftToolbar;
    public ToolBar leftPanelTop;
    public Label leftPanelTopTitle;
    public AnchorPane leftPanel;
    public PersistentSplitPane splitPaneInner;
    public AnchorPane bottomPanel;
    public SplitPane splitPaneOuter;

    @Override
    public void initialize() {
        var collected = instance().getToolWindows().stream()
                .collect(Collectors.groupingBy(ToolWindow::placement, LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<ToolWindow.Placement, List<ToolWindow>> entry : collected.entrySet()) {
            ToolWindow.Placement placement = entry.getKey();
            for (ToolWindow toolWindow : entry.getValue()) {
                SideToolButton tb;
                if (placement == ToolWindow.Placement.LEFT) {
                    leftToolbar.addToolButton((tb = new SideToolButton(toolWindow, leftPanel)), 0);
                    tb.setOnMouseClicked(e -> {
                        if (tb.isSelected()) {
                            splitPaneInner.showPane(leftPanel);
                            // splitPaneInner.getItems().add(0, leftPanel);
                            // splitPaneInner.setDividerPosition(0, 0.2);
                        } else {
                            splitPaneInner.hidePane(leftPanel);
                        }
                    });
                } else if (placement == ToolWindow.Placement.BOTTOM) {
                    leftToolbar.addToolButton((tb = new SideToolButton(toolWindow, bottomPanel)), 1);
                    tb.setOnMouseClicked(e -> {
                        if (tb.isSelected()) {
                            splitPaneOuter.getItems().add(1, bottomPanel);
                            splitPaneOuter.setDividerPosition(0, 0.8);
                        } else {
                            splitPaneOuter.getItems().remove(bottomPanel);
                        }
                    });
                }
            }
        }
    }
}
