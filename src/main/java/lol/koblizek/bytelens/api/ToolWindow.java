package lol.koblizek.bytelens.api;

import javafx.scene.Node;
import javafx.scene.image.Image;
import lol.koblizek.bytelens.api.resource.ResourceManager;

import java.io.IOException;

public record ToolWindow(String name, Node node, Image icon, Placement placement) {

    public enum Placement {
        LEFT,
        RIGHT,
        BOTTOM;
    }

    public static final class ToolWindowBuilder {
        private Node node;
        private Image icon;
        private Placement placement;
        private String name;

        ToolWindowBuilder() {
            name = "Untitled Tool Window";
        }

        public ToolWindowBuilder node(Node node) {
            this.node = node;
            return this;
        }

        public ToolWindowBuilder icon(Image icon) {
            this.icon = icon;
            return this;
        }

        public ToolWindowBuilder placement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public ToolWindow build() {
            return new ToolWindow(name, node, icon, placement);
        }

        public ToolWindowBuilder name(String name) {
            this.name = name;
            return this;
        }
    }

    public static ToolWindowBuilder builder() {
        return new ToolWindowBuilder();
    }
}
