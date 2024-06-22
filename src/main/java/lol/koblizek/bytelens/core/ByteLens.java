package lol.koblizek.bytelens.core;

import com.fasterxml.jackson.core.type.TypeReference;
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
        projects = new ArrayList<>() {
            @Override
            public boolean add(DefaultProject project) {
                return super.add(project);
            }

            // PLEASE CALL ADD LAST ONLY WHEN NEW PROJECT IS CREATED, NOT WHENEVER IT IS BEING LOADED. IT MAY BREAK THINGS!
            @Override
            public void addLast(DefaultProject project) {
                Path projects = Path.of(System.getProperty("user.home"), ".bytelens", "projects.json");
                whenNotExists(projects, Files::createFile);
                try {
                    List<String> arr = mapper.readValue(projects.toFile(), ArrayList.class);
                    arr.add(project.getProjectPath().toString());
                    mapper.writeValue(projects.toFile(), arr);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                super.add(project);
            }
        };
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
        openProject(currentProject);
    }

    public void openProject(DefaultProject project) {
        logger.trace("Attempting to open project {}", project.getName());
        currentProject = project;
        Stage stage = new Stage();
        stage.setTitle("ByteLens -" + project.getName());
        stage.setScene(getScene("main-view"));
        stage.show();
        if (primaryStage != null)
            primaryStage.close();
        primaryStage = stage;
        logger.info("Opened project {}", project.getName());
    }

    public Optional<DefaultProject> getProjectByName(String name) {
        return projects.stream().filter(p -> p.getName().equals(name)).findFirst();
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
