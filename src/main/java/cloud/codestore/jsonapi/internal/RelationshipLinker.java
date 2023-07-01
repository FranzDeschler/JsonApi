package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class links included {@link ResourceObject resource objects} to their corresponding {@link Relationship relationships}.
 */
class RelationshipLinker {
    /**
     * @param relationships     all {@link Relationship relationships} inside the JSON:API document.
     * @param includedResources all included {@link ResourceObject resource objects} inside the JSON:API document.
     */
    void link(List<Relationship> relationships, List<ResourceObject> includedResources) {
        if (!includedResources.isEmpty()) {
            for (Relationship relationship : relationships) {
                linkIncludedResourcesToRelationships(includedResources, relationship);
            }
        }
    }

    private void linkIncludedResourcesToRelationships(
            List<ResourceObject> includedResources,
            Relationship relationship
    ) {
        if (relationship instanceof DeserializedToOneRelationship<?> toOneRelationship) {
            bindIncludedResourcesToRelationship(includedResources, toOneRelationship);
        } else if (relationship instanceof DeserializedToManyRelationship<?> toManyRelationship) {
            bindIncludedResourcesToRelationship(includedResources, toManyRelationship);
        }
    }

    private void bindIncludedResourcesToRelationship(
            List<ResourceObject> includedResources,
            DeserializedToOneRelationship<?> relationship
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
            DeserializedToManyRelationship<?> relationship
    ) {
        List<ResourceObject> relatedObjects = new ArrayList<>();

        ResourceIdentifierObject[] resourceIdentifiers = relationship.getData();
        if (resourceIdentifiers != null) {
            for (ResourceIdentifierObject identifier : resourceIdentifiers) {
                for (ResourceObject resourceObject : includedResources) {
                    if (Objects.equals(identifier, resourceObject.getIdentifier())) {
                        relatedObjects.add(resourceObject);
                    }
                }
            }
        }

        relationship.setRelatedResource(relatedObjects);
    }
}
