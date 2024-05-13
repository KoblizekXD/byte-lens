package lol.koblizek.bytelens.api.ui;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.util.Pair;
import lol.koblizek.bytelens.api.util.InstanceAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link SplitPane} that persists its divider positions
 * and allows to hide certain panes.
 */
public class PersistentSplitPane extends SplitPane implements InstanceAccessor {

    private final Map<Integer, Pair<Node, Double>> removedNodes;

    public PersistentSplitPane() {
        super();
        removedNodes = new HashMap<>();
    }

    public void hidePane(int index) {
        removedNodes.put(
            index,
            new Pair<>(
                    getItems().remove(index),
                    getDividerPositions()[index]
            )
        );
    }

    public int hidePane(Node n) {
        int index = getItems().indexOf(n);
        logger().warn("Hiding pane with divider {}", getDividerPositions()[index]);
        double pos = getDividerPositions()[index];
        removedNodes.put(
                index,
                new Pair<>(
                        getItems().remove(index),
                        pos
                )
        );
        return index;
    }

    public void showPane(Node n) {
        var entry = findEntry(n);
        removedNodes.remove(entry.getKey());
        int index = entry.getKey();
        Pair<Node, Double> pair = entry.getValue();
        if (pair != null) {
            logger().warn("Showing pane at index {} with value {}", index, pair.getValue());
            getItems().add(index, pair.getKey());
            setDividerPosition(index, pair.getValue());
        }
    }

    public void showPane(int index) {
        Pair<Node, Double> pair = removedNodes.remove(index);
        if (pair != null) {
            getItems().add(index, pair.getKey());
            setDividerPosition(index, pair.getValue());
        }
    }

    private Map.Entry<Integer, Pair<Node, Double>> findEntry(Node n) {
        for (Map.Entry<Integer, Pair<Node, Double>> entry : removedNodes.entrySet()) {
            if (entry.getValue().getKey().equals(n)) {
                return entry;
            }
        }
        return null;
    }
}
