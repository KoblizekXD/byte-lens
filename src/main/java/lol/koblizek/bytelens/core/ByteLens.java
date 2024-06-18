package lol.koblizek.bytelens.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import lol.koblizek.bytelens.api.util.ProjectCreator;
import lol.koblizek.bytelens.api.util.xui.MessageBox;
import lol.koblizek.bytelens.core.project.DefaultProjectType;
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

    public static void main(String[] args) {
        launch();
    }

    private Stage primaryStage;
    private final List<ToolWindow> toolWindows;
    private final List<ProjectCreator> projectTypes;
    private final Logger logger;
    private final List<ExecutorService> executors;
    private final ObjectMapper mapper;
    private final List<DefaultProject> projects;
    private final ResourceManager resourceManager;
    private DefaultProject currentProject;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        Scene scene = getResourceManager().getScene("home-view");
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
        projectTypes = new ArrayList<>();

        MessageBox.init(this);

        Thread.setDefaultUncaughtExceptionHandler(new ExecutionExceptionHandler());
        resourceManager = ResourceManager.create(this, "/lol/koblizek/bytelens/");
        getResourceManager().get("fonts/inter-font.ttf")
                .toFont();
        getResourceManager().get("fonts/jetbrains-mono-font.ttf")
                .toFont();

        createAppFiles();
        loadAppData();

        projectTypes.add(new DefaultProjectType());
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * @return The currently primary running stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Contract(pure = true)
    public @NotNull List<ToolWindow> getToolWindows() {
        return toolWindows;
    }

    /**
     * @return List of all project types used in "New Project" dialog
     */
    public List<ProjectCreator> getProjectTypes() {
        return projectTypes;
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
        whenNotExists(blPath.resolve("projects.json"),
                (path) -> mapper.writeValue(Files.createFile(path).toFile(), new ArrayList<String>()));
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

    public void openLast() {
        logger.trace("Attempting to open last project");
        if (projects.isEmpty()) {
            logger.error("No projects to open");
            return;
        }
        currentProject = projects.getLast();
        Stage stage = new Stage();
        stage.setTitle("ByteLens -" + currentProject.getName());
        stage.setScene(getScene("new-project-view"));
        stage.show();
        logger.info("Opened project {}", currentProject.getName());
    }

    private void whenNotExists(Path path, ThrowingConsumer<Path> action) {
        if (!Files.exists(path)) {
            action.run(path);
        }
    }

    public Scene getScene(String scene) {
        return getResourceManager().getScene(scene);
    }
}
