package lol.koblizek.bytelens.core.decompiler.impl.vineflower;

import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.slf4j.Logger;

public class VineflowerLogger extends IFernflowerLogger {

    private final Logger logger;

    public VineflowerLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void writeMessage(String message, Severity severity) {
        writeMessage(message, severity, null);
    }

    @Override
    public void writeMessage(String message, Severity severity, Throwable t) {
        switch (severity) {
            case INFO:
                logger.info(message, t);
                break;
            case WARN:
                logger.warn(message, t);
                break;
            case ERROR:
                logger.error(message, t);
                break;
            case TRACE:
                logger.trace(message, t);
                break;
        }
    }
}
