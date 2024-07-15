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

package lol.koblizek.bytelens.core.utils;

/**
 * Utility class for checking preconditions.
 */
public final class Preconditions {

    private Preconditions() {
        throw new UnsupportedOperationException("Preconditions is a utility class and cannot be instantiated");
    }

    /**
     * Checks whether all objects are non-null.
     * @param objects the objects to check
     * @return whether all objects are non-null
     */
    public static boolean isNonNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether any of the objects is null.
     * @param objects the objects to check
     * @return whether any of the objects is null
     */
    public static boolean isAnyNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether all objects are non-null.
     * @param objects the objects to check
     * @throws NullPointerException if any of the objects is null
     */
    public static void nonNull(Object... objects) {
        if (isAnyNull(objects)) {
            throw new NullPointerException("One or more objects are null");
        }
    }
}
