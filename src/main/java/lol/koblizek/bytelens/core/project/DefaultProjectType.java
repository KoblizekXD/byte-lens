package lol.koblizek.bytelens.core.project;

import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.util.ProjectCreator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DefaultProjectType extends ProjectCreator {
    @Override
    public @NotNull String getName() {
        return "New Project";
    }

    @Override
    public Map<String, Class<?>> getFields() {
        return Map.of(
                "Project Name", String.class,
                "Project Location", String.class
        );
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates a new project with default structure";
    }

    @Override
    public @NotNull DefaultProject createProject(Map<String, Object> data) {
        return null;
    }
}
