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

package lol.koblizek.bytelens.api.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ASMUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ASMUtil.class);

    private ASMUtil() {}

    public static String wrapTextifier(ClassReader reader) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        var tcv = new TraceClassVisitor(null, new Textifier(), printWriter);
        reader.accept(tcv, ClassReader.EXPAND_FRAMES);
        return stringWriter.toString();
    }

    public static String wrapASMifier(ClassReader reader) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        var tcv = new TraceClassVisitor(null, new ASMifier(), printWriter);
        reader.accept(tcv, ClassReader.EXPAND_FRAMES);
        return stringWriter.toString();
    }
}
