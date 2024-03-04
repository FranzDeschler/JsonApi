package cloud.codestore.jsonapi.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * A custom deserializer to deserialize the "hreflang" property inside a {@link cloud.codestore.jsonapi.link.Link} object.
 */
public class HreflangDeserializer extends JsonDeserializer<String[]> {
    @Override
    public String[] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonToken token = jsonParser.currentToken();
        if (token == JsonToken.VALUE_STRING) {
            return new String[]{jsonParser.getText()};
        } else if (token == JsonToken.START_ARRAY) {
            return jsonParser.readValueAs(String[].class);
        }

        return null;
    }
}
