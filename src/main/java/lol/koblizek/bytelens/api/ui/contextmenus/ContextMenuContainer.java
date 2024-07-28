package lol.koblizek.bytelens.api.ui.contextmenus;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

import java.util.Optional;

@DefaultProperty("items")
public class ContextMenuContainer extends Node {
    private final ListProperty<ContextMenu> items;

    public ContextMenuContainer() {
        items = new SimpleListProperty<>();
    }

    public ListProperty<ContextMenu> itemsProperty() {
        return items;
    }

    public ObservableList<ContextMenu> getItems() {
        return items.get();
    }

    public Optional<ContextMenu> findById(String id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst();
    }
}
