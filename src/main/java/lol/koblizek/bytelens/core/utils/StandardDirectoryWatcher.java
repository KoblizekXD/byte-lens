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

package lol.koblizek.bytelens.core.utils;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import lol.koblizek.bytelens.api.util.IconifiedTreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for watching directory for changes and updating tree view accordingly.
 * <p>
 *     If a directories already exists in the tree, they won't be registered for watching.
 * </p>
 */
public class StandardDirectoryWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardDirectoryWatcher.class);

    private final WatchService watcher;
    private final Path dir;
    private final ExecutorService executor;
    private final IconifiedTreeItem root;
    private final ContextMenu contextMenu;
    private final Map<WatchKey, Path> keys;

    public StandardDirectoryWatcher(Path dir, IconifiedTreeItem root, ContextMenu contextMenu) throws IOException {
        this.root = root;
        this.contextMenu = contextMenu;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.dir = dir;
        this.executor = Executors.newSingleThreadExecutor();
        this.keys = new HashMap<>();
        registerDir(dir);
    }

    private void registerDir(Path dir) {
        try {
            keys.put(dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE), dir);
        } catch (IOException e) {
            LOGGER.error("Exception occurred while registering directory to watcher", e);
        }
    }

    public void start(ExecutorService service) {
        service.submit(this::processEvents);
    }

    private void processEvents() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            Path path = keys.get(key);

            for (WatchEvent<?> event : key.pollEvents()) {
                LOGGER.trace("New file event");
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW || !(event.context() instanceof Path)) {
                    continue;
                }

                /* Suppressed because of above statement */
                @SuppressWarnings("unchecked") WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path child = path.resolve(fileName);

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerDir(child);
                    }
                    addTreeItem(root, dir.relativize(child), child);
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    removeTreeItem(root, dir.relativize(child));
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private void addTreeItem(IconifiedTreeItem parentItem, Path relative, Path full) {
        for (Path path : relative) {
            var opt = parentItem.getChildren().stream()
                    .filter(it -> it.getValue().equals(path.toString())).findFirst();
            if (opt.isPresent() && opt.get() instanceof IconifiedTreeItem treeItem) {
                parentItem = treeItem;
            } else {
                var nP = new IconifiedTreeItem(full);
                nP.setContextMenu(contextMenu);
                parentItem.getChildren().add(nP);
                parentItem = nP;
            }
        }
    }

    private void removeTreeItem(TreeItem<String> parentItem, Path relative) {
        for (Path path : relative) {
            parentItem = parentItem.getChildren().stream().filter(it -> it.getValue().equals(path.toString()))
                    .findFirst().orElseThrow();
        }
        parentItem.getParent().getChildren().remove(parentItem);
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
