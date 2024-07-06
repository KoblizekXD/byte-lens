package lol.koblizek.bytelens.core.decompiler;

import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.decompiler.api.Decompiler;
import lol.koblizek.bytelens.core.utils.MavenMetadata;
import lol.koblizek.bytelens.core.utils.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class used to manage decompiler versioning.
 * Can be obtained by calling {@link ByteLens#getDecompilationManager()}.
 */
public class DecompilationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecompilationManager.class);

    public static DecompilationManager init(ByteLens inst) {
        Preconditions.nonNull(inst);
        return new DecompilationManager(inst);
    }

    private final ByteLens byteLens;
    private Decompiler decompiler;

    private DecompilationManager(ByteLens inst) {
        this.byteLens = inst;
    }

    public enum Providers {
        VINEFLOWER("org.vineflower:vineflower", "https://repo1.maven.org/maven2/"),
        CFR("org.benf:cfr", "https://repo1.maven.org/maven2/");

        private final String artifact;
        private final String repository;

        Providers(String artifact, String repository) {
            this.artifact = artifact;
            this.repository = repository;
        }

        public String getArtifact() {
            return artifact;
        }

        public String getRepositories() {
            return repository;
        }

        public String[] getVersions() {
            return MavenMetadata.from(repository, getArtifact()).versions()
                    .stream().map(Object::toString).toArray(String[]::new);
        }

        public void download(String version, Path out) {
            try {
                Files.copy(new URI(repository + artifact.replaceAll("[:.]", "/") + "/" + version + "/" + artifact.split(":")[1] + "-" + version + ".jar")
                        .toURL().openStream(), out);
            } catch (IOException | URISyntaxException e) {
                LOGGER.error("Failed to download decompiler", e);
            }
        }

        @Nullable
        Path getInternalImplementationPath() {
            try {
                return Path.of(getClass().getResource("/libs/" + toString().toLowerCase() + "-impl.jar").toURI());
            } catch (URISyntaxException e) {
                LOGGER.error("Failed to get internal implementation path", e);
                return null;
            }
        }
    }

    private Path fetchDecompiler(Providers provider, String version) {
        Path jar = getDecompilerCache().resolve(provider.getArtifact().split(":")[1] + "-" + version + ".jar");
        if (!Files.exists(jar)) {
            provider.download(version, jar);
        }
        return jar;
    }

    public void setDecompiler(Providers provider, String version) {
        var jar = fetchDecompiler(provider, version);
        var impl = provider.getInternalImplementationPath();
        if (jar == null || impl == null) {
            LOGGER.error("Failed to set decompiler");
            return;
        }
    }

    public Decompiler getDecompiler() {
        return decompiler;
    }

    public Path getDecompilerCache() {
        return ByteLens.getCache().resolve("decompilers");
    }
}
