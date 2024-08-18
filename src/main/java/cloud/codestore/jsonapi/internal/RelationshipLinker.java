package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class links included {@link ResourceObject resource objects} to their corresponding
 * {@link Relationship relationships} during deserialization.
 */
class RelationshipLinker {
    /**
     * @param relationships     all {@link Relationship relationships} inside the JSON:API document.
     * @param includedResources all included {@link ResourceObject resource objects} inside the JSON:API document.
     */
    void link(List<Relationship> relationships, List<ResourceObject> includedResources) {
        if (!includedResources.isEmpty()) {
            Map<ResourceIdentifierObject, ResourceObject> resourceMap = toMap(includedResources);
            for (Relationship relationship : relationships) {
                if (relationship instanceof DeserializedToOneRelationship<?> toOneRelationship) {
                    bindIncludedResourcesToRelationship(resourceMap, toOneRelationship);
                } else if (relationship instanceof DeserializedToManyRelationship<?> toManyRelationship) {
                    bindIncludedResourcesToRelationship(resourceMap, toManyRelationship);
                }
            }
        }
    }

    private Map<ResourceIdentifierObject, ResourceObject> toMap(List<ResourceObject> resourceObjects) {
        return resourceObjects.stream().collect(Collectors.toMap(
                ResourceObject::getIdentifier,
                resourceObject -> resourceObject
        ));
    }

    private void bindIncludedResourcesToRelationship(
            Map<ResourceIdentifierObject, ResourceObject> includedResources,
            DeserializedToOneRelationship<?> relationship
    ) {
        ResourceIdentifierObject identifier = relationship.getData();
        ResourceObject resourceObject = includedResources.get(identifier);
        if (resourceObject != null)
            relationship.setRelatedResource(resourceObject);
    }

    private void bindIncludedResourcesToRelationship(
            Map<ResourceIdentifierObject, ResourceObject> includedResources,
            DeserializedToManyRelationship<?> relationship
    ) {
        ResourceIdentifierObject[] resourceIdentifiers = relationship.getData();
        if (resourceIdentifiers != null) {
            List<ResourceObject> relatedObjects = Arrays.stream(resourceIdentifiers)
                    .map(includedResources::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            relationship.setRelatedResource(relatedObjects);
        }
    }
}
