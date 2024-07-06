package lol.koblizek.bytelens.core.decompiler.api;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

class NoConflictUtils {
    public static String stackTraceToString(Exception e) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ex) {
            LoggerFactory.getLogger(NoConflictUtils.class).error("Failed to convert stack trace to string", ex);
            return "";
        }
    }
}
