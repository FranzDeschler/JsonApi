package dev.systemout.jsonapi.internal;

import dev.systemout.jsonapi.relationship.ToOneRelationship;
import dev.systemout.jsonapi.resource.ResourceObject;

/**
 * Used internally for deserializing {@link ToOneRelationship} objects
 */
class DeserializedToOneRelationship<T extends ResourceObject> extends ToOneRelationship<T> {
    private final Class<T> relatedType;

    DeserializedToOneRelationship(Class<T> relatedType) {
        this.relatedType = relatedType;
    }

    void setRelatedResource(Object resourceObject) {
        setRelatedResource(relatedType.cast(resourceObject));
    }
}
