package cloud.codestore.jsonapi.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * A custom serializer to serialize the "hreflang" property inside a {@link cloud.codestore.jsonapi.link.Link} object.
 * If the property contains a single value, it´s serialized as String.
 * If it contains multiple values, it´s serialized as Array.
 * An empty value is not serialized.
 */
public class HreflangSerializer extends JsonSerializer<List<String>> {
    @Override
    public void serialize(List<String> values, JsonGenerator json, SerializerProvider serializerProvider) throws IOException {
        if (!values.isEmpty()) {
            if (values.size() == 1) {
                json.writeString(values.get(0));
            } else {
                json.writeStartArray();
                for (String value : values) {
                    json.writeString(value);
                }
                json.writeEndArray();
            }
        }
    }
}
