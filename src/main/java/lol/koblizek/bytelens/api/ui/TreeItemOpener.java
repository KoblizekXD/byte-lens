package lol.koblizek.bytelens.api.ui;

import javafx.scene.Node;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import org.jetbrains.annotations.Nullable;

public interface TreeItemOpener extends Opener {
    void open(IconifiedTreeItem item, Node content);

    @Override
    default void open(Node node, @Nullable String name) {
        open(new IconifiedTreeItem(name), node);
    }
}
