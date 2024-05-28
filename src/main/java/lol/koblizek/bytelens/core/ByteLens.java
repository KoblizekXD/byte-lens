package lol.koblizek.bytelens.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import lol.koblizek.bytelens.core.utils.ThrowingConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public final class ByteLens extends Application {

    private static ByteLens instance;

    /**
     * Obtains the currently running instance of the application.
     * It is never null.
     *
     * @return The singleton instance of the application.
     */
    public static @NotNull ByteLens getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    private Stage currentStage;
    private final List<ToolWindow> toolWindows;
    private final Logger logger;
    private final List<ExecutorService> executors;
    private final ObjectMapper mapper;
    private final List<DefaultProject> projects;
    private DefaultProject currentProject;

    @Override
    public void start(Stage stage) throws Exception {
        currentStage = stage;
        Scene scene = getResourceManager().getScene("/lol/koblizek/bytelens/views/main-view.fxml");
        stage.setTitle("ByteLens");
        stage.setScene(scene);
        stage.show();
    }

    public ByteLens() {
        logger = LoggerFactory.getLogger(getClass());
        mapper = new ObjectMapper();
        executors = new ArrayList<>();
        projects = new ArrayList<>();
        toolWindows = new ArrayList<>();

        Thread.setDefaultUncaughtExceptionHandler(new ExecutionExceptionHandler());
        ResourceManager.init();
        getResourceManager().get("/lol/koblizek/bytelens/fonts/inter-font.ttf")
                .toFont();

        createAppFiles();
        loadAppData();

        instance = this;
    }

    public ResourceManager getResourceManager() {
        return ResourceManager.getInstance();
    }

    /**
     * @return The currently running stage
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    @Contract(pure = true)
    public @NotNull List<ToolWindow> getToolWindows() {
        return toolWindows;
    }

    /**
     * @return Main ByteLens logger
     */
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void stop() {
        logger.warn("Stopping...");
        executors.forEach((k -> {
            logger.info("\tStopping executor {}", k.toString());
            k.shutdown();
        }));
        Platform.exit();
    }

    public List<ExecutorService> getExecutors() {
        return executors;
    }

    /**
     * Returns a list of all projects successfully loaded by ByteLens
     * @return List of all read projects
     */
    public List<DefaultProject> getProjects() {
        return projects;
    }

    /**
     * Returns a currently opened project as workspace.
     * @return currently opened project
     */
    public Optional<DefaultProject> getCurrentProject() {
        return Optional.ofNullable(currentProject);
    }

    private void createAppFiles() {
        Path blPath = Path.of(System.getProperty("user.home"), ".bytelens");
        whenNotExists(blPath, (path) ->
                Files.createDirectories(blPath));
        whenNotExists(blPath.resolve("projects.json"), (path) -> {
            mapper.writeValue(Files.createFile(path).toFile(), new ArrayList<String>());
        });
    }

    private void loadAppData() {
        Path blPath = Path.of(System.getProperty("user.home"), ".bytelens");
        Path projectsPath = blPath.resolve("projects.json");
        try {
            mapper.readerForUpdating(projects).readValue(projectsPath.toFile());
            logger.info("Loaded {} project/s", projects.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void whenNotExists(Path path, ThrowingConsumer<Path> action) {
        if (!Files.exists(path)) {
            action.run(path);
        }
    }
}
