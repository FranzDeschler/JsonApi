package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Arrays;

/**
 * Represents a to-many relationship.
 */
public class ToManyRelationship<T extends ResourceObject> extends Relationship {
    private ResourceIdentifierObject[] data;
    private T[] relatedResource;

    /**
     * Creates a new relationship without any links, data or meta information.
     */
    public ToManyRelationship() {
    }

    /**
     * Creates a new relationship with the given link as "related" link.
     *
     * @param relatedResourceLink a <a href="https://jsonapi.org/format/1.1/#document-resource-object-related-resource-links">related resource link</a>.
     */
    public ToManyRelationship(String relatedResourceLink) {
        super(relatedResourceLink);
    }

    /**
     * Creates a new relationship with the given {@link ResourceObject}s as related resource.
     *
     * @param resourceObject the related resource. May be {@code null}.
     */
    public ToManyRelationship(T[] resourceObject) {
        setRelatedResource(resourceObject);
    }

    /**
     * @param resourceObjects the related resources of this relationship. May be {@code null}.
     */
    @JsonIgnore
    public ToManyRelationship<T> setRelatedResource(T[] resourceObjects) {
        this.relatedResource = resourceObjects;
        if (isIncluded()) {
            ResourceIdentifierObject[] resourceIdentifiers = Arrays.stream(resourceObjects)
                                                                   .map(ResourceObject::getIdentifier)
                                                                   .toArray(ResourceIdentifierObject[]::new);

            setData(resourceIdentifiers);
        } else {
            this.data = null;
        }

        return this;
    }

    /**
     * Returns the related resource objects if available.
     *
     * @return the resource objects or {@code null} if there are no related resources.
     */
    @JsonIgnore
    public T[] getRelatedResource() {
        return relatedResource;
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
    public ToManyRelationship<T> setData(ResourceIdentifierObject... data) {
        if (relatedResource != null && !relatedResourceCountMatchesResourceIdentifierCount(data)) {
            throw new IllegalStateException("Relationships that contain related resources must contain a resource identifier objects to provide resource linkage.");
        }

        this.data = data;
        return this;
    }

    /**
     * @param firstPage a link to the first page of the relationship data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ToManyRelationship<T> setFirstPageLink(String firstPage) {
        getLinks().add(Link.FIRST, new Link(firstPage));
        return this;
    }

    /**
     * @param lastPage a link to the last page of the relationship data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ToManyRelationship<T> setLastPageLink(String lastPage) {
        getLinks().add(Link.LAST, new Link(lastPage));
        return this;
    }

    /**
     * @param previousPage a link to the previous page of the relationship data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ToManyRelationship<T> setPreviousPageLink(String previousPage) {
        getLinks().add(Link.PREV, new Link(previousPage));
        return this;
    }

    /**
     * @param nextPage a link to the next page of the relationship data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ToManyRelationship<T> setNextPageLink(String nextPage) {
        getLinks().add(Link.NEXT, new Link(nextPage));
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
