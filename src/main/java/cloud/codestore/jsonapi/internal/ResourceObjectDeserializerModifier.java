package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.document.JsonApiDocument;
import cloud.codestore.jsonapi.meta.MetaDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Unwraps the "attributes" and "relationship" objects of all resource objects in a JSON:API document.
 * This is done once for the entire document. Otherwise, the path info which is needed by the
 * {@link MetaDeserializer} would get lost.
 */
public class ResourceObjectDeserializerModifier extends BeanDeserializerModifier {
    @Override
    public JsonDeserializer<?> modifyDeserializer(
            DeserializationConfig config, BeanDescription beanDescription, JsonDeserializer<?> deserializer
    ) {
        if (isJsonApiDocument(beanDescription.getBeanClass())) {
            return new FieldUnwrappingDeserializer(deserializer);
        } else {
            return deserializer;
        }
    }

    private static boolean isJsonApiDocument(Class<?> type) {
        return JsonApiDocument.class.isAssignableFrom(type);
    }

    /**
     * A deserializer which unwraps the "attributes" and "relationships" object.
     */
    private static class FieldUnwrappingDeserializer extends DelegatingDeserializer {

        FieldUnwrappingDeserializer(JsonDeserializer<?> deserializer) {
            super(deserializer);
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> jsonDeserializer) {
            return new FieldUnwrappingDeserializer(jsonDeserializer);
        }

        @Override
        public Object deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return super.deserialize(unwrapAttributesAndRelationships(jsonParser), context);
        }

        private JsonParser unwrapAttributesAndRelationships(JsonParser jsonParser) throws IOException {
            ObjectNode rootNode = jsonParser.readValueAsTree();
            visit(rootNode);
            JsonParser newJsonParser = jsonParser.getCodec().treeAsTokens(rootNode);
            newJsonParser.nextToken();
            return newJsonParser;
        }

        private void visit(JsonNode node) {
            if (node.isObject()) {
                visit((ObjectNode) node);
            } else if (node.isArray()) {
                visit((ArrayNode) node);
            }
        }

        private void visit(ObjectNode node) {
            if (node.has("attributes") || node.has("relationships")) {
                unwrap(node, "attributes");
                unwrap(node, "relationships");
            } else {
                Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    if (!"meta".equals(field.getKey())) { //don´t modify "meta" objects
                        visit(field.getValue());
                    }
                }
            }
        }

        private void visit(ArrayNode jsonArray) {
            for (JsonNode field : jsonArray) {
                visit(field);
            }
        }

        private void unwrap(ObjectNode node, String key) {
            if (node.has(key)) {
                Iterator<Map.Entry<String, JsonNode>> fields = node.get(key).fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    node.set(entry.getKey(), entry.getValue());
                }
                node.remove(key);
            }
        }
    }
}
