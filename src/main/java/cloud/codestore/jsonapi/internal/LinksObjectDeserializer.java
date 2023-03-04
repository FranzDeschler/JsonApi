package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.link.LinksObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * A custom Jackson deserializer to deserialize {@link LinksObject}s.
 */
public class LinksObjectDeserializer extends StdDeserializer<LinksObject> {
    public LinksObjectDeserializer() {
        super(LinksObject.class);
    }

    @Override
    public LinksObject deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        LinksObject linksObject = new LinksObject();

        for (JsonToken token = jsonParser.nextToken(); token != JsonToken.END_OBJECT; token = jsonParser.nextToken()) {
            if (token == JsonToken.FIELD_NAME) {
                String relation = jsonParser.getText();
                token = jsonParser.nextToken();
                if (token == JsonToken.VALUE_STRING) {
                    String href = jsonParser.getText();
                    linksObject.add(new Link(relation, href));
                } else {
                    Link tmp = jsonParser.readValueAs(Link.class);
                    Link link = new Link(relation, tmp.getHref(), tmp.getMeta());
                    linksObject.add(link);
                }
            }
        }

        return linksObject;
    }
}
