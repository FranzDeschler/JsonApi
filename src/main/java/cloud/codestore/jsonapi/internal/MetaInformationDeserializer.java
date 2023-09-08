package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.meta.MetaDeserializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Objects;

/**
 * A custom Jackson deserializer to deserialize {@link MetaInformation} objects.
 * This object is basically a wrapper around an application specific {@link MetaDeserializer}.
 * If there is no application specific {@link MetaDeserializer} registered, this deserializer always returns {@code null}.
 */
public class MetaInformationDeserializer extends StdDeserializer<MetaInformation> {
    private MetaDeserializer metaDeserializer;

    /**
     * Creates a new {@link MetaInformationDeserializer}.
     *
     * @param metaDeserializer an application specific {@link MetaDeserializer}. May be {@code null}.
     */
    public MetaInformationDeserializer(MetaDeserializer metaDeserializer) {
        super(MetaInformation.class);
        this.metaDeserializer = Objects.requireNonNullElse(metaDeserializer, pointer -> null);
    }

    @Override
    public MetaInformation deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonStreamContext parsingContext = jsonParser.getParsingContext();
        String pointer = parsingContext.pathAsPointer().toString();
        pointer = addRelationshipPath(pointer);

        Class<? extends MetaInformation> metaInformationClass = metaDeserializer.getClass(pointer);
        if (metaInformationClass == null) {
            return readFromTree(jsonParser, pointer, jsonParser.readValueAsTree());
        } else {
            return jsonParser.readValueAs(metaInformationClass);
        }
    }

    /**
     * The "relationship" object is unwrapped before deserialization.
     * Here, we add it back to the pointer.
     */
    private String addRelationshipPath(String pointer) {
        if (pointer.startsWith("/data/")) {
            return insertRelationshipAfter("/data/", pointer);
        } else if (pointer.startsWith("/included/")) {
            return insertRelationshipAfter("/included/", pointer);
        }
        return pointer;
    }

    private String insertRelationshipAfter(String start, String pointer) {
        String subPointer = pointer.replaceFirst(start + "(\\d+/)?", "");
        if (!subPointer.startsWith("meta") && !subPointer.startsWith("links")) {
            pointer = pointer.replace(subPointer, "relationships/" + subPointer);
        }
        return pointer;
    }

    private MetaInformation readFromTree(JsonParser jsonParser, String pointer, ObjectNode node) throws JsonParseException {
        try {
            return metaDeserializer.deserialize(pointer, node);
        } catch (Exception exception) {
            throw new JsonParseException(jsonParser, "Failed to deserialize JSON:API meta object.", exception);
        }
    }
}
