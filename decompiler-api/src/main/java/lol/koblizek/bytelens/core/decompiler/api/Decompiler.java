package lol.koblizek.bytelens.core.decompiler.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Default interface for decompilers.
 */
public interface Decompiler {

    Logger LOGGER = LoggerFactory.getLogger(Decompiler.class);

    /**
     * @return preview of decompilation as string
     */
    default String decompilePreview(InputStream in) {
        try {
            return decompilePreview(in.readAllBytes());
        } catch (IOException e) {
            LOGGER.error("Failed to read input stream", e);
            return "/*" + NoConflictUtils.stackTraceToString(e) + "*/\n";
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close input stream", e);
            }
        }
    }

    String decompilePreview(byte[] bytecode);
    String decompile(byte[] bytecode);
    void decompile(Path in, Path out);
}

