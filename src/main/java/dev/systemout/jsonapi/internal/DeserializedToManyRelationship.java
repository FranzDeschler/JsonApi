package dev.systemout.jsonapi.internal;

import dev.systemout.jsonapi.relationship.ToManyRelationship;
import dev.systemout.jsonapi.resource.ResourceObject;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Used internally for deserializing {@link ToManyRelationship} objects
 */
class DeserializedToManyRelationship<T extends ResourceObject> extends ToManyRelationship<T> {
    private final Class<T> relatedType;

    DeserializedToManyRelationship(Class<T> relatedType) {
        this.relatedType = relatedType;
    }

    @SuppressWarnings("unchecked")
    void setRelatedResource(List<ResourceObject> relatedObjects) {
        if (relatedObjects.isEmpty()) {
            super.setRelatedResource(null);
        } else {
            T[] array = relatedObjects.stream()
                                      .map(relatedType::cast)
                                      .toArray(size -> (T[]) Array.newInstance(relatedType, size));

            setRelatedResource(array);
        }
    }
}
