package dev.systemout.jsonapi.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import dev.systemout.jsonapi.document.JsonApiDocument;
import dev.systemout.jsonapi.relationship.Relationship;
import dev.systemout.jsonapi.resource.ResourceObject;

import java.io.IOException;

/**
 * Creates a deserializer for {@link JsonApiDocument JSON:API documents} that links the included {@link ResourceObject resource objects}
 * to the corresponding {@link Relationship relationships}.
 */
public class JsonApiDeserializerModifier extends BeanDeserializerModifier {
    @Override
    public JsonDeserializer<?> modifyDeserializer(
            DeserializationConfig config, BeanDescription beanDescription, JsonDeserializer<?> deserializer
    ) {
        if (isJsonApiDocument(beanDescription.getBeanClass())) {
            return new DocumentDeserializer(deserializer);
        }
        return deserializer;
    }

    private static boolean isJsonApiDocument(Class<?> type) {
        return JsonApiDocument.class.isAssignableFrom(type);
    }

    private static class DocumentDeserializer extends DelegatingDeserializer {

        DocumentDeserializer(JsonDeserializer<?> deserializer) {
            super(deserializer);
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> jsonDeserializer) {
            return new DocumentDeserializer(jsonDeserializer);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonApiDocument document = (JsonApiDocument) super.deserialize(parser, context);
            new RelationshipLinker().link(document.getRelationshipBacklinks(), document.getIncludedResources());
            return document;
        }
    }
}
