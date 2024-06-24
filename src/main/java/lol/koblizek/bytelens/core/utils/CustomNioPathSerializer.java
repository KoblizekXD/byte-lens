package lol.koblizek.bytelens.core.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;
import java.nio.file.Path;

public class CustomNioPathSerializer extends StdScalarSerializer<Path> {
    public CustomNioPathSerializer() {
        super(Path.class);
    }

    @Override
    public void serialize(Path value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }
}
