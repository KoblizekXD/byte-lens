package lol.koblizek.bytelens.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lol.koblizek.bytelens.api.util.ProjectException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a ByteLens project with default behaviour.
 */
public class DefaultProject {

    @JsonIgnore
    private final ObjectMapper mapper;

    private final Path projectPath;
    private final Path projectFile;

    @JsonAlias({"project_name", "name"})
    private String name;
    @JsonAlias({"jar_sources", "sources", "source_jars"})
    private Path sources;
    @JsonAlias({"reference_libraries", "references"})
    private Path referenceLibraries;
    private Path resources;
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultProject.class);

    /**
     * Loads ByteLens project from the given path.
     * @param projectPath the path to the project directory
     */
    public DefaultProject(@NotNull Path projectPath) {
        logger.info("Attempting to load project from path: {}", projectPath);
        this.mapper = new ObjectMapper();
        mapper.setDefaultMergeable(true);
        if (Files.exists(projectPath) && Files.exists(projectPath.resolve("project.bl.json"))) {
            logger.debug("Project exists and will be loaded");
            this.projectPath = projectPath;
            this.projectFile = projectPath.resolve("project.bl.json");
            loadProject();
        } else {
            logger.warn("Project does not exist, creating default files...");
            try {
                Files.createDirectories(projectPath);
                this.projectPath = projectPath;
                this.projectFile = Files.createFile(projectPath.resolve("project.bl.json"));
                this.name = projectPath.getFileName().toString();
                this.sources = Files.createDirectories(projectPath.resolve("sources"));
                this.resources = Files.createDirectories(projectPath.resolve("resources"));
                this.referenceLibraries = Files.createDirectories(projectPath.resolve("libraries"));
                mapper.writerWithDefaultPrettyPrinter().writeValue(projectFile.toFile(), this);
            } catch (IOException e) {
                throw new ProjectException("Failed to create project file", e);
            }
            logger.info("Project created successfully");
        }
    }

    /**
     * Loads ByteLens project from the given path. It is identical to {@link #DefaultProject(Path)}.
     * @param path the path to the project directory
     * @see #DefaultProject(Path)
     */
    public DefaultProject(@NotNull String path) {
        this(Path.of(path));
    }

    /**
     * Checks if the given path contains a project.
     * @param path the path to the project
     * @return {@code true} if the given path contains a project, {@code false} otherwise
     */
    public static boolean isProject(Path path) {
        return Files.exists(path.resolve("project.bl.json"));
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
     * Returns the directory containing the source files.
     * Sources will be searched recursively.
     * @implNote The source files are expected to be in the form of .jar or .class files, different formats may be ignored.
     * @return the list of source directories
     */
    public @NotNull Path getSources() {
        return sources;
    }

    /**
     * Returns the directory containing the reference libraries.
     * @return the list of reference libraries or directories containing reference libraries
     */
    public @NotNull Path getReferenceLibraries() {
        return referenceLibraries;
    }

    /**
     * Returns the directory containing the resources.
     * @return the list of directories containing resources
     */
    public @NotNull Path getResources() {
        return resources;
    }

    public void setName(String name) {
        this.name = name;
        syncProjectFile();
    }

    /**
     * Adds a source file(By copying it) to the project.
     * @implNote The source files are expected to be in the form of .jar or .class files, different formats may be ignored.
     * @throws IOException if an I/O error occurs during copy
     * @param source the path to the source file
     */
    public void addSource(Path source) throws IOException {
        if (source.getFileName().endsWith(".jar") || source.getFileName().endsWith(".class")) {
            Files.copy(source, sources.resolve(source.getFileName()));
        }
    }

    /**
     * Adds a reference library to the project. It is expected to be a .jar file.
     * @throws IOException if an I/O error occurs during copy
     * @param referenceLibrary the path to the reference library
     */
    public void addReferenceLibrary(Path referenceLibrary) throws IOException {
        Files.copy(referenceLibrary, sources.resolve(referenceLibrary.getFileName()));
    }

    /**
     * Adds a resource to the project. Can be any form of file or directory.
     * @param resource the path to the resource
     * @throws IOException if an I/O error occurs during copy
     */
    public void addResource(Path resource) throws IOException {
        Files.copy(resource, sources.resolve(resource.getFileName()));
    }

    /**
     * Synchronizes the project file with the current project state.
     * (Technically speaking, it just serializes the {@link DefaultProject} instance to the project file)
     */
    private void syncProjectFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(projectFile.toFile(), this);
        } catch (IOException e) {
            logger.error("Failed to update project file", e);
        }
    }

    /**
     * Loads the project from the project file.
     */
    private void loadProject() {
        try {
            // 2024-06-22 Implementation note: Temporary solution, this fucker removed fields before for some reason?
            // It isn't dynamic!!
            var node = mapper.readValue(projectFile.toFile(), JsonNode.class);
            this.name = node.get("name").asText();
            this.sources = Path.of(new URI(node.get("sources").asText()));
            this.resources = Path.of(new URI(node.get("resources").asText()));
            this.referenceLibraries = Path.of(new URI(node.get("referenceLibraries").asText()));
            if (!nonNulls()) {
                logger.warn("Not all project fields are set, possible project corruption");
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
