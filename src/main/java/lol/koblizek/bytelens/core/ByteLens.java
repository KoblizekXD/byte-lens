package lol.koblizek.bytelens.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import lol.koblizek.bytelens.api.util.ProjectCreator;
import lol.koblizek.bytelens.core.project.DefaultProjectType;
import lol.koblizek.bytelens.core.utils.CustomNioPathDeserializer;
import lol.koblizek.bytelens.core.utils.CustomNioPathSerializer;
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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ByteLens extends Application {

    public static void main(String[] args) {
        launch();
    }

    private Stage primaryStage;
    private final List<ToolWindow> toolWindows;
    private final List<ProjectCreator> projectTypes;
    private final Logger logger;
    private final ExecutorService cachedExecutor;
    private static final ObjectMapper mapper = new ObjectMapper();
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
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SimpleModule module = new SimpleModule();
        module.addSerializer(new CustomNioPathSerializer());
        module.addDeserializer(Path.class, new CustomNioPathDeserializer());
        mapper.registerModule(module);
        cachedExecutor = Executors.newCachedThreadPool();
        projects = new ArrayList<>() {
            // PLEASE CALL ADD LAST ONLY WHEN NEW PROJECT IS CREATED, NOT WHENEVER IT IS BEING LOADED. IT MAY BREAK THINGS!
            @Override
            public void addLast(DefaultProject project) {
                Path projectsFile = getUserDataPath().resolve("projects.json");
                whenPathNotExists(projectsFile, Files::createFile);
                try {
                    List<String> arr = mapper.readValue(projectsFile.toFile(), new TypeReference<>() {});
                    arr.add(project.getProjectPath().toString());
                    mapper.writeValue(projectsFile.toFile(), arr);
                } catch (IOException e) {
                    logger.error("IO Error:", e);
                }
                super.add(project);
            }
        };
        toolWindows = new ArrayList<>();
        projectTypes = new ArrayList<>();

        Thread.setDefaultUncaughtExceptionHandler(new ExecutionExceptionHandler());
        resourceManager = ResourceManager.create(this, "/lol/koblizek/bytelens/");
        getResourceManager().loadFont("fonts/inter-font.ttf");
        getResourceManager().loadFont("fonts/jetbrains-mono-font.ttf");

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
        logger.info("Stopping...");
        cachedExecutor.shutdown();
        try {
            if (!cachedExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                cachedExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cachedExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ExecutorService getExecutor() {
        return cachedExecutor;
    }

    public void submitTask(Runnable runnable) {
        cachedExecutor.submit(runnable);
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
        Path blPath = getUserDataPath();
        whenPathNotExists(blPath, path ->
                Files.createDirectories(blPath));
        whenPathNotExists(blPath.resolve("projects.json"),
                path -> mapper.writeValue(Files.createFile(path).toFile(), new ArrayList<String>()));
    }

    private void loadAppData() {
        Path projectsPath = getUserDataPath().resolve("projects.json");
        try {
            mapper.readValue(projectsPath.toFile(), new TypeReference<ArrayList<Path>>() {})
                    .stream().distinct().filter(p -> {
                        if (!DefaultProject.isProject(p)) {
                            logger.warn("Invalid project \"{}\", ignoring", p);
                            return false;
                        } else {
                            return true;
                        }
                    }).map(DefaultProject::new).forEach(projects::add);
            logger.info("Loaded {} project/s", projects.size());
        } catch (IOException e) {
            logger.error("IO Error:", e);
        }
    }

    /**
     * Opens the last project opened in ByteLens
     */
    public void openLast() {
        logger.trace("Attempting to open last project");
        if (projects.isEmpty()) {
            logger.error("No projects to open");
            return;
        }
        currentProject = projects.getLast();
        openProject(currentProject);
    }

    /**
     * Opens a project in a new window
     * @param project Project to open
     */
    public void openProject(DefaultProject project) {
        logger.trace("Attempting to open project {}", project.getName());
        currentProject = project;
        Stage stage = new Stage();
        stage.setTitle("ByteLens -" + project.getName());
        stage.setScene(getScene("main-view"));
        stage.show();
        if (primaryStage != null) {
            primaryStage.close();
        }
        primaryStage = stage;
        logger.info("Opened project {}", project.getName());
    }

    /**
     * Finds a project by its name in loaded projects
     * @param name Name of the project to find
     * @return Project with the given name or empty if not found
     */
    public Optional<DefaultProject> findProjectByName(String name) {
        return projects.stream().filter(p -> p.getName().equals(name)).findFirst();
    }

    private void whenPathNotExists(Path path, ThrowingConsumer<Path> action) {
        if (!Files.exists(path)) {
            action.run(path);
        }
    }

    /**
     * Returns a scene by its name
     * @param scene Scene name to get
     * @return Scene with the given name
     */
    public Scene getScene(String scene) {
        return getResourceManager().getScene(scene);
    }

    /**
     * @return The main ObjectMapper used in ByteLens
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * @return Path to the ByteLens user data directory
     */
    public static Path getUserDataPath() {
        return Path.of(System.getProperty("user.home"), ".bytelens/");
    }
}
