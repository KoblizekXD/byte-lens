package lol.koblizek.bytelens.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.nio.file.Path;

public class CustomNioPathDeserializer extends StdScalarDeserializer<Path> {

    public CustomNioPathDeserializer() {
        super(Path.class);
    }

    @Override
    public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Path.of(p.getText());
    }
}
