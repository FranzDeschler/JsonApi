package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.internal.DeserializedToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a to-one relationship.
 */
@JsonDeserialize(as = DeserializedToOneRelationship.class)
public class ToOneRelationship extends Relationship {
    private ResourceIdentifierObject data;
    private ResourceObject relatedResource;

    /**
     * Creates a new relationship without any links, data or meta information.
     */
    public ToOneRelationship() {}

    /**
     * Creates a new relationship with the given link as "related" link.
     *
     * @param relatedResourceLink a <a href="https://jsonapi.org/format/1.0/#document-resource-object-related-resource-links">related resource link</a>.
     */
    public ToOneRelationship(String relatedResourceLink) {
        super(relatedResourceLink);
    }

    /**
     * Creates a new relationship with the given {@link ResourceObject} as related resource.
     *
     * @param resourceObject the related resource. May be {@code null}.
     */
    public ToOneRelationship(ResourceObject resourceObject) {
        setRelatedResource(resourceObject);
    }

    /**
     * @param resourceObject the related resource of this relationship. May be {@code null}.
     */
    @JsonIgnore
    public ToOneRelationship setRelatedResource(ResourceObject resourceObject) {
        this.relatedResource = resourceObject;
        setData(resourceObject == null ? null : resourceObject.getIdentifier());
        return this;
    }

    /**
     * @return the related resource of this relationship. May be {@code null}.
     */
    @JsonIgnore
    public ResourceObject getRelatedResource() {
        return relatedResource;
    }

    /**
     * Returns the related resource object of a specific type.
     * This method casts the related resource object (if present) to the specified type.
     *
     * @param type the expected type of the resource object.
     * @return the resource object or {@code null} if there is no related resource.
     */
    @JsonIgnore
    public <T extends ResourceObject> T getRelatedResource(Class<T> type) {
        return relatedResource == null ? null : (T) relatedResource;
    }

    @Override
    public boolean isIncluded() {
        return relatedResource != null;
    }

    /**
     * @return a {@link ResourceIdentifierObject} to provide resource linkage.
     */
    @JsonGetter("data")
    public ResourceIdentifierObject getData() {
        return data;
    }

    /**
     * @param data sets the {@link ResourceIdentifierObject} to provide resource linkage.
     * @return this object.
     * @throws IllegalStateException if this relationship contains a related resource object
     *                               but the given resource identifier is {@code null}.
     */
    @JsonSetter("data")
    public ToOneRelationship setData(ResourceIdentifierObject data) {
        if (relatedResource != null && data == null) {
            throw new IllegalStateException("Relationships that contain a related resource must contain a resource identifier object to provide resource linkage.");
        }

        this.data = data;
        return this;
    }
}
