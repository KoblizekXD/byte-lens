package lol.koblizek.bytelens.api.ui;

import javafx.scene.Node;
import org.jetbrains.annotations.Nullable;

/**
 * Interface, whose implementations are responsible for opening a given node.
 */
public interface Opener {
    /**
     * Opens the given node. This operation can open the node in a new window, tab, etc.
     * It all depends on the implementation.
     * @param node Node to be opened.
     * @param name Optional name of the node. It might or might not be used by the implementation.
     */
    void open(Node node, @Nullable String name);
}
