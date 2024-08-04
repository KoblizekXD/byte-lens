package lol.koblizek.bytelens.api.util;

import org.objectweb.asm.ClassReader;
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
}
