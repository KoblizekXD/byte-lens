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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * A simple directory watcher that uses the WatchService API to watch for file events in a directory.
 * These changes then can be processed by the user with {@code onCreate} and {@code onDelete events}.
 */
public class StandardDirectoryWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardDirectoryWatcher.class);

    private final WatchService watcher;
    private final Map<WatchKey, DirectoryEvent> keys;

    public StandardDirectoryWatcher() throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
    }

    public void registerDir(Path dir, Consumer<Path> onCreate, Consumer<Path> onDelete) {
        try {
            keys.put(dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE), new DirectoryEvent(dir, onCreate, onDelete));
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
            DirectoryEvent e = keys.get(key);
            Path path = e.path();

            for (WatchEvent<?> event : key.pollEvents()) {
                LOGGER.trace("New file event has occurred: {}", event);
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
                        registerDir(child, e.onCreate(), e.onDelete());
                    }
                    e.onCreate().accept(child);
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    e.onDelete().accept(child);
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    record DirectoryEvent(Path path, Consumer<Path> onCreate, Consumer<Path> onDelete) {
    }
}
