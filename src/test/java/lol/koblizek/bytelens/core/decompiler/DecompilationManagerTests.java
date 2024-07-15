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

package lol.koblizek.bytelens.core.decompiler;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class DecompilationManagerTests {
    @Test
    void testDownloadFile() {
        Assertions.assertDoesNotThrow(() -> {
            DecompilationManager.Providers.VINEFLOWER.download("1.10.1", Path.of("vineflower.jar"));
        });
        Assertions.assertTrue(Files.exists(Path.of("vineflower.jar")));
    }

    @AfterAll
    static void deleteFile() {
        try {
            Files.deleteIfExists(Path.of("vineflower.jar"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
