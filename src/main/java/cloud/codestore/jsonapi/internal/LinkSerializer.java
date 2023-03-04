package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.link.Link;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * A custom Jackson serializer to serialize {@link Link}s.
 */
public class LinkSerializer extends StdSerializer<Link> {
    public LinkSerializer() {
        super(Link.class);
    }

    @Override
    public void serialize(Link link, JsonGenerator json, SerializerProvider serializerProvider) throws IOException {
        if (link.getMeta() == null) {
            json.writeString(link.getHref());
        } else {
            json.writeStartObject();
            json.writeStringField("href", link.getHref());
            json.writeObjectField("meta", link.getMeta());
            json.writeEndObject();
        }
    }
}
