package lol.koblizek.bytelens.api.ui;

import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lol.koblizek.bytelens.api.util.InstanceAccessor;

import java.util.ArrayList;
import java.util.List;

public class SideToolBar extends ToolBar implements InstanceAccessor {

    private final List<ToggleGroup> groups;

    public SideToolBar() {
        super();
        this.groups = new ArrayList<>();
    }

    public void addToolButton(SideToolButton button, int group) {
        ToggleGroup gp;
        if (group >= this.groups.size()) {
            gp = new ToggleGroup();
            this.groups.add(gp);
            if (group > 0) {
                Pane pane = new Pane();
                VBox.setVgrow(pane, Priority.ALWAYS);
                this.getItems().add(pane);
            }
        } else
            gp = this.groups.get(group);
        button.setToggleGroup(gp);
        if (gp.getToggles().size() == 1) {
            button.setSelected(true);
            button.getControlledPane().setTitle(button.getToolWindow().name());
        }
        this.getItems().add(button);
    }
}
