package lol.koblizek.bytelens.api.ui;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import lol.koblizek.bytelens.api.util.InstanceAccessor;

import java.util.ArrayList;
import java.util.List;

public class SideToolBar extends ToolBar implements InstanceAccessor {

    private final List<ToggleGroup> toggleGroups;

    public SideToolBar() {
        super();
        toggleGroups = new ArrayList<>();
        setOrientation(Orientation.VERTICAL);
        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(node -> {
                        if (node instanceof SideToolButton button) {
                            int i = button.getToggleGroupIndex();
                            if (i > toggleGroups.size() - 1) {
                                ToggleGroup group = new ToggleGroup();
                                button.setToggleGroup(group);
                                toggleGroups.add(group);
                            } else {
                                button.setToggleGroup(toggleGroups.get(i));
                            }
                        } else {
                            logger().warn("SideToolBar only supports SideToolButton children");
                            getChildren().remove(node);
                        }
                    });
                } else if (c.wasRemoved() || c.wasReplaced()) {
                    c.getRemoved().forEach(node -> {
                        if (node instanceof SideToolButton button) {
                            button.setToggleGroup(toggleGroups.get(0));
                            button.setToggleGroupIndex(0);
                        }
                    });
                }
            }
        });
    }
}
