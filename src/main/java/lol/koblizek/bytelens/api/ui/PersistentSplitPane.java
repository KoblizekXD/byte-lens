package lol.koblizek.bytelens.api.ui;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link SplitPane} that persists its divider positions
 * and allows to hide certain panes.
 */
public class PersistentSplitPane extends SplitPane {

    private final List<RemovedNode> removedNodes;

    public static final EventType<ElementHideEvent> ON_HIDE = new EventType<>("ON_HIDE");

    public static class ElementHideEvent extends Event {

        private final transient Node hiding;

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
        removedNodes = new ArrayList<>();
    }

    public int hidePane(Node n) {
        int index = getItems().indexOf(n);
        double j = getDividerPositions()[index - 1 == -1 ? 0 : index - 1];
        Node removed = getItems().remove(index);
        ElementHideEvent event = new ElementHideEvent(removed);
        fireEvent(event);
        removedNodes.add(new RemovedNode(
                index, index - 1 == -1 ? 0 : index - 1,
                removed,
                j
        ));
        return index;
    }

    public void showPane(Node n) {
        var entry = findEntry(n);
        if (entry == null) {
            LoggerFactory.getLogger(PersistentSplitPane.class).error("Node not found in removed nodes, cannot show it.");
            return;
        }
        removedNodes.remove(entry);
        getItems().add(entry.index, entry.node);
        setDividerPosition(entry.dividerIndex, entry.d);
    }

    private RemovedNode findEntry(Node n) {
        for (RemovedNode entry : removedNodes) {
            if (entry.node.equals(n)) {
                return entry;
            }
        }
        return null;
    }

    protected record RemovedNode(int index, int dividerIndex, Node node, double d) { }
}
