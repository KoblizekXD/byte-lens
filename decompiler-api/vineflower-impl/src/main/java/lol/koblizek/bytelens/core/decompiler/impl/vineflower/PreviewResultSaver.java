package lol.koblizek.bytelens.core.decompiler.impl.vineflower;

import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.slf4j.Logger;

import java.io.StringWriter;
import java.util.jar.Manifest;

public class PreviewResultSaver implements IResultSaver {

    private final Logger logger;
    private final StringWriter writer;

    public PreviewResultSaver(Logger logger, StringWriter writer) {
        this.logger = logger;
        this.writer = writer;
    }

    @Override
    public void saveFolder(String path) {
        logger.warn("Calling saveFolder from incompatible result saver");
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
        logger.warn("Calling copyFile from incompatible result saver");
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        writer.write("/* ByteLens analyzer ~ " + qualifiedName + " */\n");
        writer.write(content);
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
        logger.warn("Calling createArchive from incompatible result saver");
    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {
        logger.warn("Calling saveDirEntry from incompatible result saver");
    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {
        logger.warn("Calling copyEntry from incompatible result saver");
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
        writer.write("/* ByteLens analyzer ~ " + qualifiedName + " */\n");
        writer.write(content);
    }

    @Override
    public void closeArchive(String path, String archiveName) {
        logger.warn("Calling closeArchive from incompatible result saver");
    }
}
