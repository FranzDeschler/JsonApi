package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.internal.DeserializedToManyRelationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Represents a to-many relationship.
 */
@JsonDeserialize(as = DeserializedToManyRelationship.class)
public class ToManyRelationship extends Relationship {
    private ResourceIdentifierObject[] data;
    private ResourceObject[] relatedResource;

    /**
     * Creates a new relationship without any links, data or meta information.
     */
    public ToManyRelationship() {
    }

    /**
     * Creates a new relationship with the given link as "related" link.
     *
     * @param relatedResourceLink a <a href="https://jsonapi.org/format/1.0/#document-resource-object-related-resource-links">related resource link</a>.
     */
    public ToManyRelationship(String relatedResourceLink) {
        super(relatedResourceLink);
    }

    /**
     * Creates a new relationship with the given {@link ResourceObject}s as related resource.
     *
     * @param resourceObject the related resource. May be {@code null}.
     */
    public ToManyRelationship(ResourceObject[] resourceObject) {
        setRelatedResource(resourceObject);
    }

    /**
     * @param resourceObjects the related resources of this relationship. May be {@code null}.
     */
    @JsonIgnore
    public ToManyRelationship setRelatedResource(ResourceObject[] resourceObjects) {
        this.relatedResource = resourceObjects;
        if (isIncluded()) {
            ResourceIdentifierObject[] resourceIdentifiers = Arrays.stream(resourceObjects)
                    .map(ResourceObject::getIdentifier)
                    .toArray(ResourceIdentifierObject[]::new);

            setData(resourceIdentifiers);
        } else {
            setData(null);
        }

        return this;
    }

    /**
     * @return the related resources. May be {@code null}.
     */
    @JsonIgnore
    public ResourceObject[] getRelatedResource() {
        return relatedResource;
    }

    /**
     * Returns the related resource objects of a specific type.
     * This method casts the related resource objects (if present) to the specified type.
     *
     * @param type the expected type of the resource objects.
     * @return the resource objects or {@code null} if there are no related resources.
     */
    @JsonIgnore
    public <T extends ResourceObject> T[] getRelatedResource(Class<T> type) {
        if (relatedResource == null) {
            return null;
        } else {
            return Arrays.stream(relatedResource)
                    .map(resourceObject -> {
                        if (resourceObject.getClass().isAssignableFrom(type)) {
                            return (T) resourceObject;
                        } else {
                            throw new ClassCastException(resourceObject.getClass() + " cannot be cast to " + type);
                        }
                    })
                    .toArray(value -> (T[]) Array.newInstance(type, relatedResource.length));
        }
    }

    /**
     * @return an array of {@link ResourceIdentifierObject} to provide resource linkage.
     */
    @JsonGetter("data")
    public ResourceIdentifierObject[] getData() {
        return data;
    }

    /**
     * @param data sets the {@link ResourceIdentifierObject}s to provide resource linkage.
     * @return this object.
     * @throws IllegalStateException if this relationship contains related resource objects but the given array of
     *                               resource identifiers is {@code null} or doesn´t have the same length.
     */
    @JsonSetter("data")
    public ToManyRelationship setData(ResourceIdentifierObject[] data) {
        if (relatedResource != null && !relatedResourceCountMatchesResourceIdentifierCount(data)) {
            throw new IllegalStateException("Relationships that contain related resources must contain a resource identifier objects to provide resource linkage.");
        }

        this.data = data;
        return this;
    }

    @Override
    public boolean isIncluded() {
        return relatedResource != null && relatedResource.length > 0;
    }

    private boolean relatedResourceCountMatchesResourceIdentifierCount(ResourceIdentifierObject[] resourceIdentifiers) {
        return relatedResource != null &&
                resourceIdentifiers != null &&
                relatedResource.length == resourceIdentifiers.length;
    }
}
