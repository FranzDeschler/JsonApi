package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceObject;

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
