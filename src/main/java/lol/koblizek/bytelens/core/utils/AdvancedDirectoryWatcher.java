package lol.koblizek.bytelens.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class AdvancedDirectoryWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedDirectoryWatcher.class);

    private final WatchService watcher;
    private final Map<WatchKey, DirectoryEvent> keys;

    public AdvancedDirectoryWatcher() throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
    }

    public void registerDir(Path dir, Runnable onCreate, Runnable onDelete) {
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
                    e.onCreate().run();
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    e.onDelete().run();
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    record DirectoryEvent(Path path, Runnable onCreate, Runnable onDelete) {
    }
}
