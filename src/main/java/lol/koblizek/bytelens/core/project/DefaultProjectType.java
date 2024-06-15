package lol.koblizek.bytelens.core.project;

import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.api.util.ProjectCreator;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultProjectType extends ProjectCreator {
    @Override
    public @NotNull String getName() {
        return "New Project";
    }

    @Override
    public Map<String, Class<?>> getFields() {
        return new LinkedHashMap<>() {{
            put("Project Name", String.class);
            put("Project Location", Path.class);
        }};
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
