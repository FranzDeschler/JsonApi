package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.document.JsonApiDocument;
import cloud.codestore.jsonapi.document.ResourceCollectionDocument;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * Deserializes a JSON:API document based on the type of the "data" property.
 * If it is an object or missing, this deserializer returns a {@link SingleResourceDocument}.
 * If it is an array, this deserializer returns a {@link ResourceCollectionDocument}.
 */
public class JsonApiDocumentDeserializer extends StdDeserializer<JsonApiDocument> {
    public JsonApiDocumentDeserializer() {
        super(JsonApiDocument.class);
    }

    @Override
    public JsonApiDocument deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        ObjectNode documentNode = mapper.readTree(jsonParser);

        if (documentNode.has("data")) {
            JsonNode dataNode = documentNode.get("data");
            if (dataNode.isObject()) {
                return mapper.treeToValue(documentNode, SingleResourceDocument.class);
            } else if (dataNode.isArray()) {
                return mapper.treeToValue(documentNode, ResourceCollectionDocument.class);
            }
        }

        return mapper.treeToValue(documentNode, SingleResourceDocument.class);
    }
}
