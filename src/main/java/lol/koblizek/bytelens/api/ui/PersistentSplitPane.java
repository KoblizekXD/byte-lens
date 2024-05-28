package lol.koblizek.bytelens.api.ui;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link SplitPane} that persists its divider positions
 * and allows to hide certain panes.
 */
public class PersistentSplitPane extends SplitPane {

    private final Map<Position, Pair<Node, Double>> removedNodes;

    public static final EventType<ElementHideEvent> ON_HIDE = new EventType<>("ON_HIDE");

    public static class ElementHideEvent extends Event {

        private final Node hiding;

        public ElementHideEvent(Node hiding) {
            super(ON_HIDE);
            this.hiding = hiding;
        }

        public Node getHiding() {
            return hiding;
        }
    }

    public PersistentSplitPane() {
        super();
        removedNodes = new HashMap<>();
    }

    public int hidePane(Node n) {
        int index = getItems().indexOf(n);
        double j = getDividerPositions()[index - 1 == -1 ? 0 : index - 1];
        Node removed = getItems().remove(index);
        ElementHideEvent event = new ElementHideEvent(removed);
        fireEvent(event);
        removedNodes.put(
                new Position(index, index - 1 == -1 ? 0 : index - 1),
                new Pair<>(
                        removed,
                        j
                )
        );
        return index;
    }

    public void showPane(Node n) {
        var entry = findEntry(n);
        removedNodes.remove(entry.getKey());
        Position index = entry.getKey();
        Pair<Node, Double> pair = entry.getValue();
        if (pair != null) {
            getItems().add(index.index, pair.getKey());
            setDividerPosition(index.dividerIndex, pair.getValue());
        }
    }

    private Map.Entry<Position, Pair<Node, Double>> findEntry(Node n) {
        for (Map.Entry<Position, Pair<Node, Double>> entry : removedNodes.entrySet()) {
            if (entry.getValue().getKey().equals(n)) {
                return entry;
            }
        }
        return null;
    }


    protected record Position(int index, int dividerIndex) {

    }
}
