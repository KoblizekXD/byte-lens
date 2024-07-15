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

package lol.koblizek.bytelens.core.utils;

@FunctionalInterface
public interface ThrowingConsumer<T> {

    void accept(T t) throws Exception;

    default ThrowingConsumer<T> andThen(ThrowingConsumer<? super T> after) {
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }

    default void run(T t) {
        try {
            accept(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
