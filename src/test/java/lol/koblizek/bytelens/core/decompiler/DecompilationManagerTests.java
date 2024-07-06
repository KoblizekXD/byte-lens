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
