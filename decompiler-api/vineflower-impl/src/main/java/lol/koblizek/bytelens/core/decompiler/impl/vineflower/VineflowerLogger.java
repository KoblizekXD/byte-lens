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
