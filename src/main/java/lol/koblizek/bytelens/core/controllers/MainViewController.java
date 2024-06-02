package lol.koblizek.bytelens.core.controllers;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import lol.koblizek.bytelens.api.ui.*;
import lol.koblizek.bytelens.core.ByteLens;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// This code will have to be "eventually" rewritten to be more effective and more understandable

public class MainViewController extends Controller {

    public MenuBar menubar;
    public SideToolBar leftToolbar;
    public SidePane leftPanel;
    public PersistentSplitPane splitPaneInner;
    public SidePane bottomPanel;
    public PersistentSplitPane splitPaneOuter;
    public ExtendedCodeArea codeArea;
    public Menu menu1;

    public MainViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        ByteLens byteLens = getByteLens();
        ResourceManager rm = byteLens.getResourceManager();

        if (byteLens.getCurrentProject().isEmpty())
            byteLens.getLogger().warn("No project is open, this should not happen. Errors might occur.");

        var tws = new ArrayList<>(byteLens.getToolWindows());
        // Example tool window
        tws.add(new ToolWindow("Project",
                null,
                ResourceManager.getJBIcon("AllIcons.Expui.Toolwindow.Project", true).toSVG(),
                ToolWindow.Placement.BOTTOM
        ));
        var collected = tws.stream()
                .collect(Collectors.groupingBy(ToolWindow::placement, LinkedHashMap::new, Collectors.toList()));

        if (!collected.containsKey(ToolWindow.Placement.LEFT))
            splitPaneInner.hidePane(leftPanel);
        if (!collected.containsKey(ToolWindow.Placement.BOTTOM))
            splitPaneOuter.hidePane(bottomPanel);

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
                }
            }
        }
        codeArea.bridge(byteLens);
    }
}
