package lol.koblizek.bytelens.core.project;

import javafx.scene.control.Label;
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
        Map<String, Class<?>> fields = new LinkedHashMap<>();
        fields.put("Project Name", String.class);
        fields.put("Project Location", Path.class);
        fields.put("Created@Project Location@Project Name", Label.class);
        return fields;
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates a new project with default structure";
    }

    @Override
    public @NotNull DefaultProject createProject(Map<String, Object> data) {
        Path projectPath = ((Path) data.get("Project Location"))
                .resolve((String) data.get("Project Name"));
        return new DefaultProject(projectPath);
    }
}
