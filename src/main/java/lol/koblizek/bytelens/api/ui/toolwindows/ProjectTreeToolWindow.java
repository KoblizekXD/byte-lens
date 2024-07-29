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

package lol.koblizek.bytelens.api.ui.toolwindows;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.ui.JetBrainsImage;
import lol.koblizek.bytelens.api.ui.Opener;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import lol.koblizek.bytelens.core.ByteLens;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public class ProjectTreeToolWindow extends TreeView<String> implements ToolWindow.ToolWindowNode {

    private final Opener opener;
    @FXML private IconifiedTreeItem root;

    private ByteLens byteLens;

    public ProjectTreeToolWindow(Opener opener) {
        this.opener = opener;
    }

    @Override
    public Node create(ByteLens byteLens) throws IOException {
        this.byteLens = byteLens;
        var loader = byteLens.getResourceManager().getFXML("components/project-tw.fxml");
        loader.setRoot(this);
        loader.setController(this);
        return loader.load();
    }

    @FXML
    public void initialize() {
        Optional<DefaultProject> optionalProject = byteLens.getCurrentProject();
        optionalProject.ifPresentOrElse(project -> {
            byteLens.findProjectType(project.getClass()).ifPresentOrElse(creator -> {
                creator.setupProjectTreeToolWindow(byteLens, this);
            }, () ->
                    byteLens.getLogger().error("No project type found for project: {}, was it loaded?", project.getClass().getSimpleName()));
        }, () -> {
            root.setGraphic(new JetBrainsImage("AllIcons.Expui.Nodes.ErrorIntroduction"));
            root.setValue("No module/project is open");
        });
    }

    public void appendTreeItem(TreeItem<String> parent, String value, Consumer<IconifiedTreeItem> configurator) {
        var item = new IconifiedTreeItem(value);
        configurator.accept(item);
        parent.getChildren().add(item);
    }


    public IconifiedTreeItem root() {
        return root;
    }

    public Opener opener() {
        return opener;
    }
}
