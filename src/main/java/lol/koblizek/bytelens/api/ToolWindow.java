package lol.koblizek.bytelens.api;

import javafx.scene.Node;
import javafx.scene.image.Image;
import lol.koblizek.bytelens.api.resource.ResourceManager;

import java.io.IOException;

public record ToolWindow(Node node, Image icon, Placement placement) {

    public enum Placement {
        LEFT,
        RIGHT,
        BOTTOM;
    }

    public static final class ToolWindowBuilder {
        private Node node;
        private Image icon;
        private Placement placement;

        ToolWindowBuilder() {
        }

        public ToolWindowBuilder node(Node node) {
            this.node = node;
            return this;
        }

        public ToolWindowBuilder fxml(String path) {
            try {
                this.node = ResourceManager.getInstance().get(path).toLoader()
                        .load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            return new ToolWindow(node, icon, placement);
        }
    }

    public static ToolWindowBuilder builder() {
        return new ToolWindowBuilder();
    }
}
