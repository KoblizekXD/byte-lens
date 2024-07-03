package lol.koblizek.bytelens.core.decompiler;

import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.MavenMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Class used to manage decompiler versioning.
 * Can be obtained by calling {@link ByteLens#getDecompilationManager()}.
 */
public class DecompilationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecompilationManager.class);
    private static final String[] PROVIDERS = {"vineflower", "cfr"};

    public static DecompilationManager init(ByteLens inst) {
        return new DecompilationManager(inst);
    }

    private final ByteLens byteLens;

    private DecompilationManager(ByteLens inst) {
        this.byteLens = inst;
    }

    /**
     * Get list of ready decompilers, which are stored in cache.
     * If the method fails by any reason, it returns empty list.
     * @return Array of ready decompilers
     */
    public String[] getReadyDecompilers() {
        try (var stream = Files.list(ByteLens.getCache().resolve("decompilers/"))) {
            return stream.map(path -> path.getFileName().toString().replace(".jar", ""))
                    .toArray(String[]::new);
        } catch (IOException e) {
            LOGGER.error("Failed to list decompilers", e);
            return new String[0];
        }
    }

    /**
     * @return Array of decompilers available for download from remote repositories
     */
    public String[] getAvailableDecompilers() {
        var metadata = MavenMetadata.fromMavenCentral("org.vineflower:vineflower");
        return metadata.versions().stream().map(version -> metadata.artifact().toString() + "-" + version.toString())
                .toArray(String[]::new);
    }

    /**
     * Selects decompiler by its name.
     * <p>
     *     Decompiler will be either downloaded from remote repository or loaded from cache.
     * </p>
     * @param decompiler Decompiler to select(e.g. vineflower-1.10.1)
     * @return Decompiler instance
     */
    public Decompiler selectDecompiler(String decompiler) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static String[] getProviders() {
        return PROVIDERS;
    }
}
