/*
Copyright (c) 2024 KoblizekXD

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package lol.koblizek.bytelens.core.decompiler.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Default interface for decompilers.
 */
public abstract class Decompiler {

    protected Map<String, Object> options;
    protected static final Logger LOGGER = LoggerFactory.getLogger(Decompiler.class);

    protected Decompiler(Map<String, Object> options) {
        this.options = options;
    }

    /**
     * @return preview of decompilation as string
     */
    public String decompilePreview(InputStream in) {
        try {
            return decompilePreview(in.readAllBytes());
        } catch (IOException e) {
            LOGGER.error("Failed to read input stream", e);
            return "/*" + NoConflictUtils.stackTraceToString(e) + "*/\n";
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close input stream", e);
            }
        }
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public abstract String decompilePreview(byte[] bytecode);
    public abstract void decompile(Path in, Path out);
    public abstract List<Option> getSupportedOptions();
}

