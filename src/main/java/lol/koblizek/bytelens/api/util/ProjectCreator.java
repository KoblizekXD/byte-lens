package lol.koblizek.bytelens.api.util;

import lol.koblizek.bytelens.api.DefaultProject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a project type that can be created by the user in "New Project" dialog
 */
public abstract class ProjectCreator {
    /**
     * @return The name of the project type
     */
    public abstract @NotNull String getName();

    /**
     * @return Optional description of the project type
     */
    public @NotNull String getDescription() {
        return "";
    }

    /**
     * Returns the fields that the project type requires and are later retrieved
     * by {@link #createProject(Map)}
     * @implNote It is recommended to return a {@link java.util.LinkedHashMap} to keep the order of the fields
     * @return Fields that the project type requires
     */
    public Map<String, Class<?>> getFields() {
        return Map.of();
    }

    /**
     * Creates a project based on the data that was filled in by the user
     * @param data Data that was filled in by the user
     * @return The created project
     */
    public abstract @NotNull DefaultProject createProject(Map<String, Object> data);

    @Override
    public final String toString() {
        return getName();
    }
}
