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

                if (isExtensionMember(linkName)) {
                    Object object = jsonParser.readValueAs(Object.class);
                    linksObject.setExtensionMember(linkName, object);
                } else if (token == JsonToken.VALUE_STRING) {
                    parseAsString(linkName, jsonParser, linksObject);
                } else {
                    parseAsLinkObject(linkName, jsonParser, linksObject);
                }
            }
        }

        return linksObject;
    }

    private void parseAsString(String linkName, JsonParser jsonParser, LinksObject linksObject) throws IOException {
        String href = jsonParser.getText();
        if (href != null && !href.isBlank()) {
            Link link = new Link(href).setRelation(linkName);
            linksObject.add(linkName, link);
        }
    }

    private void parseAsLinkObject(String linkName, JsonParser jsonParser, LinksObject linksObject) throws IOException {
        Link link = jsonParser.readValueAs(Link.class);
        if (link != null && link.getHref() != null && !link.getHref().isBlank()) {
            if (link.getRelation() == null)
                link.setRelation(linkName);

            linksObject.add(linkName, link);
        }
    }

    private boolean isExtensionMember(String linkName) {
        return linkName.contains(":");
    }
}
