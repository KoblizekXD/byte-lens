package lol.koblizek.bytelens.core.decompiler.impl.vineflower;

import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BytecodeContextSource implements IContextSource {

    private final byte[] bytecode;
    private final String internalName;

    public BytecodeContextSource(byte[] bytecode) {
        this.bytecode = bytecode;
        ClassReader reader = new ClassReader(bytecode);
        this.internalName = reader.getClassName();
    }

    @Override
    public String getName() {
        return Type.getObjectType(internalName).getClassName();
    }

    @Override
    public Entries getEntries() {
        return new Entries(List.of(Entry.atBase(internalName)), List.of(), List.of());
    }

    @Override
    public InputStream getInputStream(String resource) {
        return new ByteArrayInputStream(bytecode);
    }

    @Override
    public IOutputSink createOutputSink(IResultSaver saver) {
        return new IOutputSink() {
            @Override
            public void begin() {

            }

            @Override
            public void acceptClass(String qualifiedName, String fileName, String content, int[] mapping) {
                saver.saveClassFile("", qualifiedName, fileName.substring(0, fileName.length() - CLASS_SUFFIX.length()) + ".java", content, mapping);
            }

            @Override
            public void acceptDirectory(String directory) {

            }

            @Override
            public void acceptOther(String path) {

            }

            @Override
            public void close() throws IOException {

            }
        };
    }
}
