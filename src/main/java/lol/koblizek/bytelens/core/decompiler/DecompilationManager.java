package lol.koblizek.bytelens.core.decompiler;

import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.utils.MavenMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

/**
 * Class used to manage decompiler versioning.
 * Can be obtained by calling {@link ByteLens#getDecompilationManager()}.
 */
public class DecompilationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecompilationManager.class);

    public static DecompilationManager init(ByteLens inst) {
        return new DecompilationManager(inst);
    }

    private final ByteLens byteLens;

    private DecompilationManager(ByteLens inst) {
        this.byteLens = inst;
    }

    public enum Providers {
        VINEFLOWER("org.vineflower:vineflower", "https://repo1.maven.org/maven2/"),
        CFR("org.benf:cfr", "https://repo1.maven.org/maven2/");

        private final String artifact;
        private final String[] repositories;

        Providers(String artifact, String... repositories) {
            this.artifact = artifact;
            this.repositories = repositories;
        }

        public String getArtifact() {
            return artifact;
        }

        public String[] getRepositories() {
            return repositories;
        }

        public String[] getVersions() {
            return Arrays.stream(getRepositories()).map(repo -> MavenMetadata.from(repo, getArtifact()).versions())
                    .flatMap(Collection::stream).map(Object::toString).toArray(String[]::new);
        }

        public void download(String version, Path path) {

        }
    }
}
