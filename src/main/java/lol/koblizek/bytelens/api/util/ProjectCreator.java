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
