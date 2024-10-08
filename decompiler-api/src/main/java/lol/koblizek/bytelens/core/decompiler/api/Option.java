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

package lol.koblizek.bytelens.core.decompiler.api;

/**
 * Represents a simple decompiler option.
 * @param id The identifier of the option
 * @param name     name of the option
 * @param desc     description of the option
 * @param shortName short name of the option
 * @param defaultValue default value of the option
 */
public record Option(String id, String name, String desc, String shortName, Object defaultValue) {

    public Option(String name, String desc, String shortName) {
        this(shortName, name, desc, shortName, null);
    }

    public Option(String name, String desc, String shortName, Object defaultValue) {
        this(shortName, name, desc, shortName, defaultValue);
    }
}
