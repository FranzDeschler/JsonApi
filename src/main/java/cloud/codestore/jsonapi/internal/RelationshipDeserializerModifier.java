package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.document.JsonApiDocument;
import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;

import java.io.IOException;

/**
 * Creates a custom deserializer that parses {@link ToOneRelationship} and {@link ToManyRelationship} objects
 * with the correct generic type of the related {@link ResourceObject}.
 */
public class RelationshipDeserializerModifier extends BeanDeserializerModifier {
    @Override
    public JsonDeserializer<?> modifyDeserializer(
            DeserializationConfig config, BeanDescription beanDescription, JsonDeserializer<?> deserializer
    ) {
        Class<?> beanClass = beanDescription.getBeanClass();
        if (isRelationship(beanClass)) {
            Class<?> relatedType = beanDescription.getType().containedType(0).getRawClass();
            if (ResourceObject.class.isAssignableFrom(relatedType)) {
                return new RelationshipDeserializer(deserializer, beanClass, (Class<? extends ResourceObject>) relatedType);
            } else {
                throw new ClassCastException(relatedType + " is not a subtype of " + ResourceObject.class);
            }
        }
        return deserializer;
    }

    private static boolean isRelationship(Class<?> type) {
        return Relationship.class.isAssignableFrom(type);
    }

    private static class RelationshipDeserializer extends DelegatingDeserializer {
        private final Class<?> beanClass;
        private final Class<? extends ResourceObject> relatedType;

        RelationshipDeserializer(JsonDeserializer<?> deserializer, Class<?> beanClass, Class<? extends ResourceObject> relatedType) {
            super(deserializer);
            this.beanClass = beanClass;
            this.relatedType = relatedType;
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> jsonDeserializer) {
            return new RelationshipDeserializer(jsonDeserializer, beanClass, relatedType);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            Relationship<?> relationship = deserializeRelationship(parser, context);
            JsonApiDocument parent = getParent(parser.getParsingContext().getParent());

            // Note: if the application only defines "Relationship<>" as type in the resource class,
            // the relationship is dynamically parsed in the DynamicRelationshipDeserializer
            // which removes the parent node and thus, parent is null at this point!
            if (parent != null) {
                parent.addRelationshipBacklink(relationship);
            }

            return relationship;
        }

        private Relationship<?> deserializeRelationship(JsonParser parser, DeserializationContext context) throws IOException {
            if (isToManyRelationship(beanClass)) {
                DeserializedToManyRelationship<?> instance = new DeserializedToManyRelationship<>(relatedType);
                return (Relationship<?>) super.deserialize(parser, context, instance);
            } else {
                DeserializedToOneRelationship<?> instance = new DeserializedToOneRelationship<>(relatedType);
                return (Relationship<?>) super.deserialize(parser, context, instance);
            }
        }

        /**
         * @return the {@link JsonApiDocument} which contains the currently parsed {@link Relationship}.
         */
        private JsonApiDocument getParent(JsonStreamContext parentStreamContext) {
            if (parentStreamContext == null) {
                return null;
            }

            Object parent = parentStreamContext.getCurrentValue();
            if (parent instanceof JsonApiDocument jsonApiDocument) {
                return jsonApiDocument;
            }

            return getParent(parentStreamContext.getParent());
        }

        private static boolean isToManyRelationship(Class<?> type) {
            return ToManyRelationship.class.isAssignableFrom(type);
        }
    }
}
