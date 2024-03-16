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
        return setFirstPageLink(new Link(firstPage));
    }

    /**
     * @param firstPage a link to the first page of the relationship data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ToManyRelationship<T> setFirstPageLink(Link firstPage) {
        getLinks().add(Link.FIRST, firstPage);
        return this;
    }

    /**
     * @return a link to the first page of the relationship data. May be {@code null}.
     */
    @JsonIgnore
    public Link getFirstPageLink() {
        return getLinks().get(Link.FIRST);
    }

    /**
     * @param lastPage a link to the last page of the relationship data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ToManyRelationship<T> setLastPageLink(String lastPage) {
        return setLastPageLink(new Link(lastPage));
    }

    /**
     * @param lastPage a {@link Link} to the last page of the relationship data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ToManyRelationship<T> setLastPageLink(Link lastPage) {
        getLinks().add(Link.LAST, lastPage);
        return this;
    }

    /**
     * @return a link to the last page of the relationship data. May be {@code null}.
     */
    @JsonIgnore
    public Link getLastPageLink() {
        return getLinks().get(Link.LAST);
    }

    /**
     * @param previousPage a link to the previous page of the relationship data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ToManyRelationship<T> setPreviousPageLink(String previousPage) {
        return setPreviousPageLink(new Link(previousPage));
    }

    /**
     * @param previousPage a {@link Link} to the previous page of the relationship data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ToManyRelationship<T> setPreviousPageLink(Link previousPage) {
        getLinks().add(Link.PREV, previousPage);
        return this;
    }

    /**
     * @return a link to the previous page of the relationship data. May be {@code null}.
     */
    @JsonIgnore
    public Link getPreviousPageLink() {
        return getLinks().get(Link.PREV);
    }

    /**
     * @param nextPage a link to the next page of the relationship data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ToManyRelationship<T> setNextPageLink(String nextPage) {
        return setNextPageLink(new Link(nextPage));
    }

    /**
     * @param nextPage a {@link Link} to the next page of the relationship data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ToManyRelationship<T> setNextPageLink(Link nextPage) {
        getLinks().add(Link.NEXT, nextPage);
        return this;
    }

    /**
     * @return a link to the next page of the relationship data. May be {@code null}.
     */
    @JsonIgnore
    public Link getNextPageLink() {
        return getLinks().get(Link.NEXT);
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
