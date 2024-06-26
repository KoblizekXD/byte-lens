package lol.koblizek.bytelens.core.utils;

import com.sun.nio.file.ExtendedWatchEventModifier;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StandardDirectoryWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardDirectoryWatcher.class);

    private final WatchService watcher;
    private final Path dir;
    private final ExecutorService executor;
    private final TreeItem<String> root;
    private final Map<WatchKey, Path> keys;

    public StandardDirectoryWatcher(Path dir, TreeItem<String> root) throws IOException {
        this.root = root;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.dir = dir;
        this.executor = Executors.newSingleThreadExecutor();
        this.keys = new HashMap<>();
        registerDir(dir);
    }

    private void registerDir(Path dir) {
        try {
            keys.put(dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY), dir);
        } catch (IOException e) {
            LOGGER.error("Exception occurred while registering directory to watcher", e);
        }
    }

    public void start() {
        executor.submit(this::processEvents);
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

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path child = path.resolve(fileName);

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerDir(child);
                    }
                    addTreeItem(root, dir.relativize(child));
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {

                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    // Handle modify event if needed
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private void addTreeItem(TreeItem<String> parentItem, Path fullPath) {
        for (Path path : fullPath) {
            var opt = parentItem.getChildren().stream()
                    .filter(it -> it.getValue().equals(path.toString())).findFirst();
            if (opt.isPresent())
                parentItem = opt.get();
            else {
                var nP = new TreeItem<>(path.getFileName().toString());
                parentItem.getChildren().add(nP);
                parentItem = nP;
            }
        }
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public WatchService getWatcher() {
        return watcher;
    }
}
