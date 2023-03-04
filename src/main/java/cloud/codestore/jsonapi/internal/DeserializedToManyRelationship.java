package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.ToManyRelationship;

/**
 * Used internally for deserializing {@link ToManyRelationship} objects
 */
public class DeserializedToManyRelationship extends ToManyRelationship {
    public DeserializedToManyRelationship() {
        RelationshipHolder.getRelationships().add(this);
    }
}
