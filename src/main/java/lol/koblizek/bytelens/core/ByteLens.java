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
import lol.koblizek.bytelens.core.decompiler.DecompilationManager;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ByteLens extends Application {

    public static void main(String[] args) {
        launch();
    }

    private Stage primaryStage;
    private final List<ToolWindow> toolWindows;
    private final Map<Class<? extends DefaultProject>, ProjectCreator> projectTypes;
    private final Logger logger;
    private final ExecutorService cachedExecutor;
    private static final ObjectMapper mapper = new ObjectMapper();
    private final List<DefaultProject> projects;
    private final ResourceManager resourceManager;
    private final DecompilationManager decompilationManager;
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
        projectTypes = new HashMap<>();

        Thread.setDefaultUncaughtExceptionHandler(new ExecutionExceptionHandler());
        resourceManager = ResourceManager.create(this, "/lol/koblizek/bytelens/");
        decompilationManager = DecompilationManager.init(this);
        decompilationManager.setDecompiler(DecompilationManager.Providers.VINEFLOWER, "1.10.1");
        getResourceManager().loadFont("fonts/inter-font.ttf");
        getResourceManager().loadFont("fonts/jetbrains-mono-font.ttf");

        createAppFiles();
        loadAppData();

        projectTypes.put(DefaultProject.class, new DefaultProjectType());
    }

    /**
     * @return The main resource manager used in ByteLens
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * @return The currently primary running stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @return List of all tool windows usable in ByteLens
     */
    @Contract(pure = true)
    public @NotNull List<ToolWindow> getToolWindows() {
        return toolWindows;
    }

    public Optional<ProjectCreator> findProjectType(Class<? extends DefaultProject> projectType) {
        return Optional.ofNullable(projectTypes.get(projectType));
    }

    /**
     * @return List of all project types used in "New Project" dialog
     */
    public Map<Class<? extends DefaultProject>, ProjectCreator> getProjectTypes() {
        return projectTypes;
    }

    /**
     * @return Main ByteLens logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Immediately stops the ByteLens application
     */
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

    /**
     * Returns the executor capable of running background tasks in parallel
     * @return The main cached executor used in ByteLens
     */
    public ExecutorService getCachedExecutor() {
        return cachedExecutor;
    }

    /**
     * Submits task to the cached executor
     * @see ExecutorService#submit(Runnable)
     * @see this#getCachedExecutor()
     * @param runnable Task to submit to the executor
     */
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
     * @return the decompilation manager used in ByteLens
     */
    public DecompilationManager getDecompilationManager() {
        return decompilationManager;
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

    /**
     * @return Path to the ByteLens cache directory
     */
    public static Path getCache() {
        return getUserDataPath().resolve("cache/");
    }
}
