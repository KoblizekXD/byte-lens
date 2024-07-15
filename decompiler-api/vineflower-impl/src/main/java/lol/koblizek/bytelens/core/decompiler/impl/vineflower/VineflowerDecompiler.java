/*
 * This file is part of byte-lens, licensed under the GNU General Public License v3.0.
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

import lol.koblizek.bytelens.core.decompiler.api.Decompiler;
import lol.koblizek.bytelens.core.decompiler.api.Option;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver;
import org.jetbrains.java.decompiler.main.decompiler.SingleFileSaver;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VineflowerDecompiler extends Decompiler {

    public VineflowerDecompiler(Map<String, Object> options) {
        super(options);
    }

    public VineflowerDecompiler() {
        super(IFernflowerPreferences.DEFAULTS);
    }

    private Fernflower construct(IResultSaver saver) {
        return new Fernflower(saver, options, new VineflowerLogger(LOGGER));
    }

    @Override
    public String decompilePreview(byte[] bytecode) {
        try (StringWriter writer = new StringWriter()) {
            Fernflower ff = construct(new PreviewResultSaver(LOGGER, writer));
            ff.addSource(new BytecodeContextSource(bytecode));
            ff.decompileContext();
            return writer.toString();
        } catch (IOException e) {
            LOGGER.error("Failed to decompile preview", e);
            return "/*" + e.getMessage() + "*/\n";
        }
    }

    @Override
    public void decompile(Path in, Path out) {
        IResultSaver saver;
        if (Files.isDirectory(out))
            saver = new DirectoryResultSaver(out.toFile());
        else {
            saver = new SingleFileSaver(out.toFile());
        }
        Fernflower fernflower = construct(saver);
        fernflower.addSource(in.toFile());
        fernflower.decompileContext();
    }

    @Override
    public List<Option> getSupportedOptions() {
        List<Option> opts = new ArrayList<>();
        for (Field field : IFernflowerPreferences.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(IFernflowerPreferences.Name.class)) {
                String name = field.getAnnotation(IFernflowerPreferences.Name.class).value();
                String desc = field.getAnnotation(IFernflowerPreferences.Description.class).value();
                String shortName = field.getAnnotation(IFernflowerPreferences.ShortName.class).value();
                opts.add(new Option(name, desc, shortName));
            }
        }
        return opts;
    }
}
