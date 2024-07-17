/*
 * This file is part of byte-lens.
 *
 * Copyright (c) 2024 KoblizekXD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import lol.koblizek.bytelens.api.ui.*;
import lol.koblizek.bytelens.api.ui.toolwindows.ProjectTreeToolWindow;
import lol.koblizek.bytelens.core.ByteLens;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// This code will have to be "eventually" rewritten to be more effective and more understandable
// ^^ PS: I got no time for this

public class MainViewController extends Controller implements Opener {

    @FXML private TabPane tabPane;
    @FXML private MenuBar menubar;
    @FXML private SideToolBar leftToolbar;
    @FXML private SideToolBar rightToolbar;
    @FXML private SidePane leftPanel;
    @FXML private SidePane rightPanel;
    @FXML private PersistentSplitPane splitPaneInner;
    @FXML private SidePane bottomPanel;
    @FXML private PersistentSplitPane splitPaneOuter;
    @FXML private Menu menu1;

    public MainViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        ByteLens byteLens = getByteLens();

        if (byteLens.getCurrentProject().isEmpty()) {
            byteLens.getLogger().warn("No project is open, this should not happen. Errors might occur.");
        }

        var tws = new ArrayList<>(byteLens.getToolWindows());
        // Example tool window
        try {
            tws.add(new ToolWindow("Project",
                    new ProjectTreeToolWindow(this).create(byteLens),
                    ResourceManager.getJBIcon("AllIcons.Expui.Toolwindow.Project", true),
                    ToolWindow.Placement.LEFT
            ));
        } catch (IOException e) {
            getLogger().error("Error occurred on tool window creation:", e);
        }
        var collected = tws.stream()
                .collect(Collectors.groupingBy(ToolWindow::placement, LinkedHashMap::new, Collectors.toList()));

        if (!collected.containsKey(ToolWindow.Placement.LEFT)) {
            splitPaneInner.hidePane(leftPanel);
        }
        if (!collected.containsKey(ToolWindow.Placement.BOTTOM)) {
            splitPaneOuter.hidePane(bottomPanel);
        }
        if (!collected.containsKey(ToolWindow.Placement.BOTTOM)) {
            splitPaneInner.hidePane(rightPanel);
        }

        for (Map.Entry<ToolWindow.Placement, List<ToolWindow>> entry : collected.entrySet()) {
            ToolWindow.Placement placement = entry.getKey();
            for (ToolWindow toolWindow : entry.getValue()) {
                SideToolButton tb;
                if (placement == ToolWindow.Placement.LEFT) {
                    leftToolbar.addToolButton((tb = new SideToolButton(toolWindow, leftPanel)), 0);
                    splitPaneInner.addEventHandler(PersistentSplitPane.ON_HIDE, e -> {
                        if (e.getHiding().equals(leftPanel)) {
                            tb.setSelected(false);
                        }
                    });
                } else if (placement == ToolWindow.Placement.BOTTOM) {
                    leftToolbar.addToolButton((tb = new SideToolButton(toolWindow, bottomPanel)), 1);
                    splitPaneOuter.addEventHandler(PersistentSplitPane.ON_HIDE, e -> {
                        if (e.getHiding().equals(bottomPanel)) {
                            tb.setSelected(false);
                        }
                    });
                } else if (placement == ToolWindow.Placement.RIGHT) {
                    rightToolbar.addToolButton((tb = new SideToolButton(toolWindow, rightPanel)), 0);
                    splitPaneInner.addEventHandler(PersistentSplitPane.ON_HIDE, e -> {
                        if (e.getHiding().equals(rightPanel)) {
                            tb.setSelected(false);
                        }
                    });
                }
            }
        }
        var dm = getByteLens().getDecompilationManager();
        getByteLens().getLogger().info("Powered by {}, version {}", dm.getProvider(), dm.getVersion());
    }

    @Override
    public void open(Node node, @Nullable String name) {
        Tab tab = new Tab(name);
        tab.setContent(node);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
