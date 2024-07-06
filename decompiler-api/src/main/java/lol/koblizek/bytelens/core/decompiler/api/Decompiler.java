package lol.koblizek.bytelens.core.decompiler.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Default interface for decompilers.
 */
public interface Decompiler {

    Logger LOGGER = LoggerFactory.getLogger(Decompiler.class);

    /**
     * @return preview of decompilation as string
     */
    default Future<String> decompilePreview(InputStream in) {
        try {
            return decompilePreview(in.readAllBytes());
        } catch (IOException e) {
            LOGGER.error("Failed to read input stream", e);
            return new FutureTask<>(() -> "/*" + NoConflictUtils.stackTraceToString(e) + "*/\n");
        }
    }

    Future<String> decompilePreview(byte[] bytecode);
    Future<String> decompile(byte[] bytecode);
}

