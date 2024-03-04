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
                String linkName = jsonParser.getText();
                token = jsonParser.nextToken();
                if (token == JsonToken.VALUE_STRING) {
                    String href = jsonParser.getText();
                    Link link = new Link(href).setRelation(linkName);
                    linksObject.add(linkName, link);
                } else {
                    Link link = jsonParser.readValueAs(Link.class);
                    if (link.getRelation() == null)
                        link.setRelation(linkName);

                    linksObject.add(link.getRelation(), link);
                }
            }
        }

        return linksObject;
    }
}
