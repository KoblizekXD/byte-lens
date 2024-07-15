/*
 * This file is part of byte-lens.
 *
 * Copyright (c) 2024 KoblizekXD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package lol.koblizek.bytelens.api;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import lol.koblizek.bytelens.core.ByteLens;

import java.io.IOException;

public record ToolWindow(String name, Node node, Image icon, Placement placement) {

    public enum Placement {
        LEFT,
        RIGHT,
        BOTTOM
    }

    public ToolWindow(String name, Image icon, Placement placement) {
        this(name, new Label("No content"), icon, placement);
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

    public interface ToolWindowNode {
        Node create(ByteLens byteLens) throws IOException;
    }
}
