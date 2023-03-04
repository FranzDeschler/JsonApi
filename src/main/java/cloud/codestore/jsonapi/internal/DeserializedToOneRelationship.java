package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.ToOneRelationship;

/**
 * Used internally for deserializing {@link ToOneRelationship} objects
 */
public class DeserializedToOneRelationship extends ToOneRelationship {
    public DeserializedToOneRelationship() {
        RelationshipHolder.getRelationships().add(this);
    }
}
