/*
 * This file is part of byte-lens, licensed under the GNU General Public License v3.0.
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
