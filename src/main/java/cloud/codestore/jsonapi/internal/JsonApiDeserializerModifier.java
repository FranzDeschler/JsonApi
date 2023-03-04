package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.document.ResourceCollectionDocument;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Creates a deserializer for {@link SingleResourceDocument} and {@link ResourceCollectionDocument} documents
 * that passes the included {@link ResourceObject resource objects} to the corresponding {@link Relationship relationships}.
 */
public class JsonApiDeserializerModifier extends BeanDeserializerModifier {
    @Override
    public JsonDeserializer<?> modifyDeserializer(
            DeserializationConfig config, BeanDescription beanDescription, JsonDeserializer<?> deserializer
    ) {
        Class<?> beanClass = beanDescription.getBeanClass();
        if (isSingleResourceDocument(beanClass) || isResourceCollectionDocument(beanClass)) {
            return new DocumentDeserializer(deserializer);
        }
        return deserializer;
    }

    private static boolean isSingleResourceDocument(Class<?> type) {
        return SingleResourceDocument.class.isAssignableFrom(type);
    }

    private static boolean isResourceCollectionDocument(Class<?> type) {
        return ResourceCollectionDocument.class.isAssignableFrom(type);
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
            RelationshipHolder.reset();

            List<ResourceObject> includedResources = Collections.emptyList();
            Object object = super.deserialize(parser, context);
            if (object instanceof SingleResourceDocument<?> document) {
                includedResources = document.getIncludedResources();
            } else if (object instanceof ResourceCollectionDocument<?> document) {
                includedResources = document.getIncludedResources();
            }

            if (!includedResources.isEmpty()) {
                for (Relationship relationship : RelationshipHolder.getRelationships()) {
                    bindIncludedResourcesToRelationships(includedResources, relationship);
                }
            }

            return object;
        }

        private void bindIncludedResourcesToRelationships(
                List<ResourceObject> includedResources,
                Relationship relationship
        ) {
            if (relationship instanceof ToOneRelationship toOneRelationship) {
                bindIncludedResourcesToRelationship(includedResources, toOneRelationship);
            } else if (relationship instanceof ToManyRelationship toManyRelationship) {
                bindIncludedResourcesToRelationship(includedResources, toManyRelationship);
            }
        }

        private void bindIncludedResourcesToRelationship(
                List<ResourceObject> includedResources,
                ToOneRelationship relationship
        ) {
            ResourceIdentifierObject identifier = relationship.getData();
            for (ResourceObject resourceObject : includedResources) {
                if (Objects.equals(identifier, resourceObject.getIdentifier())) {
                    relationship.setRelatedResource(resourceObject);
                    return;
                }
            }
        }

        private void bindIncludedResourcesToRelationship(
                List<ResourceObject> includedResources,
                ToManyRelationship relationship
        ) {
            List<ResourceObject> relatedObjects = new ArrayList<>();

            for (ResourceIdentifierObject identifier : relationship.getData()) {
                for (ResourceObject resourceObject : includedResources) {
                    if (Objects.equals(identifier, resourceObject.getIdentifier())) {
                        relatedObjects.add(resourceObject);
                    }
                }
            }

            relationship.setRelatedResource(relatedObjects.toArray(new ResourceObject[0]));
        }
    }
}
