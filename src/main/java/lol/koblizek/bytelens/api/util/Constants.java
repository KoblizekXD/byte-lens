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

/**
 * String and other primitive type constants.
 * @implNote This is utility class, which should be eventually migrated into a configuration/lang file.
 */
public class Constants {

    private Constants() { throw new IllegalStateException("Utility class"); }

    /**
     * FXML file extension
     */
    public static final String FXML_EXT      = ".fxml";
    /**
     * Name of the file which contains the list of projects
     */
    public static final String PROJECTS_FILE = "projects.json";
    /**
     * Message which will be displayed when a procedure to decompile a file fails
     */
    public static final String ERROR_FAILED_TO_DECOMPILE = "Failed to decompile file";
    public static final String LOG_TASK_SUBMIT = "Submitted new task for parallel execution";
}
