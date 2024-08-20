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

import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.decompiler.api.Decompiler;
import lol.koblizek.bytelens.core.utils.MavenMetadata;
import lol.koblizek.bytelens.core.utils.Preconditions;
import lol.koblizek.bytelens.core.utils.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

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
    private String provider;
    private String version;

    private DecompilationManager(ByteLens inst) {
        this.byteLens = inst;
    }

    public enum Providers {
        VINEFLOWER("org.vineflower:vineflower", "lol.koblizek.bytelens.core.decompiler.impl.vineflower", "https://repo1.maven.org/maven2/"),
        CFR("org.benf:cfr", null, "https://repo1.maven.org/maven2/");

        private final String artifact;
        private final String internalPackage;
        private final String repository;

        Providers(String artifact, String internalPackage, String repository) {
            this.artifact = artifact;
            this.internalPackage = internalPackage;
            this.repository = repository;
        }

        public String getInternalPackage() {
            return internalPackage;
        }

        public String getArtifact() {
            return artifact;
        }

        public String getName() {
            return StringUtils.capitalize(toString().toLowerCase());
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
                if (out.getParent() != null && !Files.exists(out.getParent()))
                    Files.createDirectories(out.getParent());
                Files.copy(new URI(repository + artifact.replaceAll("[:.]", "/") + "/" + version + "/" + artifact.split(":")[1] + "-" + version + ".jar")
                        .toURL().openStream(), out);
            } catch (IOException | URISyntaxException e) {
                LOGGER.error("Failed to download decompiler", e);
            }
        }

        private Path extractInternalImplIfNotYet() throws IOException {
            var target = ByteLens.getCache().resolve("decompiler-impls").resolve(toString().toLowerCase() + "-impl.jar");
            /* Check for hash difference because why not? */
            var internalImpl = getClass().getResource("/libs/" + toString().toLowerCase() + "-impl.jar");
            Files.createDirectories(target.getParent());
            if (!Files.exists(target) || (Files.exists(target) && !StringUtils.hashOf(internalImpl).equals(StringUtils.hashOf(target)))) {
                if (Files.exists(target))
                    Files.delete(target);
                LOGGER.warn("Hash changed or file does not exist, extracting new internal implementation jar");
                Files.copy(internalImpl.openStream(), target);
            }
            return target;
        }

        @Nullable
        Path getInternalImplementationPath() {
            try {
                return extractInternalImplIfNotYet();
            } catch (IOException e) {
                LOGGER.error("Failed to get internal implementation path", e);
                return null;
            }
        }
    }

    private Path fetchDecompiler(Providers provider, String version) {
        Path jar = getDecompilerCache().resolve(provider.getArtifact().split(":")[1] + "-" + version + ".jar");
        if (!Files.exists(jar)) {
            LOGGER.info("Downloading decompiler version {} from {}", version, provider.getRepositories());
            provider.download(version, jar);
        }
        return jar;
    }

    /**
     * Returns all decompilers, that are cached - their jar files are fetched from remote repositories.
     * @return Array of cached decompilers
     */
    public String[] getCachedDecompilers() {
        try (Stream<Path> files = Files.list(getDecompilerCache())) {
            return files.map(Path::getFileName)
                    .map(Path::toString)
                    .map(str -> FilenameUtils.getBaseName(str).replace('-', ' '))
                    .map(StringUtils::capitalize)
                    .toArray(String[]::new);
        } catch (IOException e) {
            LOGGER.error("Failed to get cached decompilers", e);
            return new String[0];
        }
    }

    /**
     * Sets the decompiler to the specified version. If the decompiler is not cached, it will be downloaded
     * from remote repository.
     * @param provider Decompiler provider
     * @param version Decompiler version
     */
    public void setDecompiler(Providers provider, String version) {
        var jar = fetchDecompiler(provider, version);
        var impl = provider.getInternalImplementationPath();
        if (impl == null) {
            LOGGER.error("Failed to set decompiler");
            return;
        }
        var optDecompiler = fetchInternal(provider, jar);
        if (optDecompiler.isPresent()) {
            decompiler = optDecompiler.get();
            this.provider = provider.getName();
            this.version = version;

        } else {
            LOGGER.error("Failed to set decompiler");
        }
    }

    private Optional<Decompiler> fetchInternal(Providers provider, Path jar) {
        ModuleFinder moduleFinder = ModuleFinder.of(jar, provider.getInternalImplementationPath());
        ModuleLayer parent = ModuleLayer.boot();
        Configuration cf = parent.configuration().resolve(
                moduleFinder,
                ModuleFinder.of(),
                Set.of(provider.getInternalPackage())
        );
        var loader = parent.defineModulesWithOneLoader(cf, ByteLens.class.getClassLoader());
        return ServiceLoader.load(loader, Decompiler.class)
                .findFirst();
    }

    /**
     * @return Current decompiler
     */
    public Decompiler getDecompiler() {
        return decompiler;
    }

    /**
     * @return Current decompiler provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @return Current decompiler version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return Path to decompiler cache
     */
    public Path getDecompilerCache() {
        return ByteLens.getCache().resolve("decompilers");
    }

    public void saveConfiguration() {
        Preconditions.nonNull(decompiler);

        Path p = getDecompilerCache().resolve(getProvider().toLowerCase() + "-" + version + ".json");
        try {
            ByteLens.getMapper().writerWithDefaultPrettyPrinter().writeValue(p.toFile(), decompiler.getOptions());
        } catch (IOException e) {
            LOGGER.error("Failed to save decompiler configuration", e);
        }
    }
}
