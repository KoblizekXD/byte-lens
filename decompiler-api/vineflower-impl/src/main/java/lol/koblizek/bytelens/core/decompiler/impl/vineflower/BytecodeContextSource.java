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
                // Not used
            }

            @Override
            public void acceptClass(String qualifiedName, String fileName, String content, int[] mapping) {
                saver.saveClassFile("", qualifiedName, fileName.substring(0, fileName.length() - CLASS_SUFFIX.length()) + ".java", content, mapping);
            }

            @Override
            public void acceptDirectory(String directory) {
                // Not used
            }

            @Override
            public void acceptOther(String path) {
                // Not used
            }

            @Override
            public void close() throws IOException {
                // Not used
            }
        };
    }
}
