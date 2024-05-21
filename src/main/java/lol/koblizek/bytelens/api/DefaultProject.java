package lol.koblizek.bytelens.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import lol.koblizek.bytelens.api.util.InstanceAccessor;
import lol.koblizek.bytelens.api.util.ProjectException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DefaultProject implements InstanceAccessor {

    private final Path projectPath;
    private final Path projectFile;

    @JsonAlias({"project_name", "name"})
    private String name;
    @JsonAlias({"jar_sources", "sources", "source_jars"})
    private List<Path> sources;
    @JsonAlias({"reference_libraries", "references"})
    private List<Path> referenceLibraries;
    private List<Path> resources;

    /**
     * Loads ByteLens project from the given path.
     * @param projectPath the path to the project directory
     */
    public DefaultProject(Path projectPath) {
        logger().info("Attempting to load project from path: {}", projectPath.toString());
        if (Files.exists(projectPath) && Files.exists(projectPath.resolve("project.bl.json"))) {
            logger().debug("Project exists and will be loaded");
            this.projectPath = projectPath;
            this.projectFile = projectPath.resolve("project.bl.json");
            loadProject();
        } else {
            logger().warn("Project does not exist, use createProject() method to create a new project");
            try {
                this.projectPath = projectPath;
                this.projectFile = Files.createFile(projectPath.resolve("project.bl.json"));
            } catch (IOException e) {
                logger().error("Failed to create project file", e);
                throw new ProjectException();
            }
        }
    }

    /**
     * Returns the default path to the project
     * @return the path to the project
     */
    public Path getProjectPath() {
        return projectPath;
    }

    /**
     * <p>
     *     Returns the path to the project file, always ends with {@code project.bl.json}.
     * </p>
     * @return the path to the project file
     */
    public Path getProjectFile() {
        return projectFile;
    }

    /**
     * Returns the name of the project
     * @return the name of the project
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns the list of directories containing source files.
     * Sources will be searched recursively.
     * @implNote The source files are expected to be in the form of .jar or .class files, different formats may be ignored.
     * @return the list of source directories
     */
    public @NotNull List<Path> getSources() {
        return sources;
    }

    /**
     * Returns the list of reference libraries or directories containing reference libraries.
     * @return the list of reference libraries or directories containing reference libraries
     */
    public @NotNull List<Path> getReferenceLibraries() {
        return referenceLibraries;
    }

    /**
     * Returns the list of directories containing resources.
     * @return the list of directories containing resources
     */
    public @NotNull List<Path> getResources() {
        return resources;
    }

    /**
     * Loads the project from the project file.
     */
    private void loadProject() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readerForUpdating(this).readValue(projectFile.toFile());
            if (!nonNulls()) {
                logger().warn("Not all project fields are set, possible project corruption");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean nonNulls() {
        for (Field field : getClass().getDeclaredFields()) {
            try {
                if (!field.trySetAccessible() && field.get(this) == null
                    && !Modifier.isFinal(field.getModifiers()))
                    return false;
            } catch (IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }
}
