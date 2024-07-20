package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a JSON:API document containing a list of resource objects as primary data.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.1/#document-top-level">jsonapi.org</a>
 */
@JsonDeserialize //override deserializer from base class to avoid recursive call
public class ResourceCollectionDocument<T extends ResourceObject> extends JsonApiDocument {
    private T[] data;

    /**
     * Used internally for deserialization.
     */
    ResourceCollectionDocument() {}

    /**
     * Creates a new {@link ResourceCollectionDocument} with the given primary data.
     *
     * @param data the primary data of this JSON:API document.
     * @throws NullPointerException if {@code data} is {@code null}.
     */
    public ResourceCollectionDocument(T[] data) {
        Objects.requireNonNull(data);
        setData(data);
    }

    /**
     * Creates a new {@link ResourceCollectionDocument} with the given meta-information.
     * This should be used if the JSON:API document does not contain primary data.
     * According to the JSON:API specification, a JSON:API document must contain at least
     * a "data" or "meta" property as top-level member.
     *
     * @param meta the meta-information of this JSON:API document.
     * @throws NullPointerException if {@code meta} is {@code null}.
     */
    public ResourceCollectionDocument(MetaInformation meta) {
        Objects.requireNonNull(meta);
        setMeta(meta);
    }

    /**
     * Creates a new {@link ResourceCollectionDocument} with the given extension members.
     *
     * @param extensionMembers one or more members defined by an applied extension.
     * @throws NullPointerException if {@code extensionMembers} is {@code null}.
     * @throws IllegalArgumentException if {@code extensionMembers} is empty.
     * @throws IllegalArgumentException if the name of one or more extension members is invalid.
     * @since 1.1
     */
    public ResourceCollectionDocument(Map<String, Object> extensionMembers) {
        Objects.requireNonNull(extensionMembers);
        if (extensionMembers.isEmpty()) {
            throw new IllegalArgumentException("Extension members must not be empty");
        }

        setExtensionMembers(extensionMembers);
    }

    /**
     * @param data the primary data of this JSON:API document.
     * @return this object.
     */
    @JsonSetter("data")
    public ResourceCollectionDocument<T> setData(T[] data) {
        this.data = data;

        if (data != null)
            for (ResourceObject resourceObject : data)
                resourceObject.setParent(this);

        return this;
    }

    /**
     * Return the {@link ResourceObject}s which were set as the primary data of this JSON:API document.
     * <br/><br/>
     * Note that this method may throw a {@link ClassCastException} if the concrete type of the data-array could not
     * be derived at compile time. To prevent this, use {@link #getData(Class)} and specify the expected type of the
     * resource objects explicitly.
     *
     * @return the primary data as array or {@code null} if no primary data was set.
     */
    @JsonGetter("data")
    public T[] getData() {
        return data;
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public T[] getData(Class<T> type) {
        if (data == null) {
            return null;
        }

        return Arrays.stream(data)
                     .map(type::cast)
                     .toArray(size -> (T[]) Array.newInstance(type, size));
    }

    /**
     * @param firstPage a link to the first page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ResourceCollectionDocument<T> setFirstPageLink(String firstPage) {
        return setFirstPageLink(new Link(firstPage));
    }

    /**
     * @param firstPage a link to the first page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ResourceCollectionDocument<T> setFirstPageLink(Link firstPage) {
        Objects.requireNonNull(firstPage);
        getLinks().add(Link.FIRST, firstPage);
        return this;
    }

    /**
     * @return a link to the first page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getFirstPageLink() {
        return getLink(Link.FIRST);
    }

    /**
     * @param lastPage a link to the last page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ResourceCollectionDocument<T> setLastPageLink(String lastPage) {
        return setLastPageLink(new Link(lastPage));
    }

    /**
     * @param lastPage a {@link Link} to the last page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ResourceCollectionDocument<T> setLastPageLink(Link lastPage) {
        Objects.requireNonNull(lastPage);
        getLinks().add(Link.LAST, lastPage);
        return this;
    }

    /**
     * @return a link to the last page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getLastPageLink() {
        return getLink(Link.LAST);
    }

    /**
     * @param previousPage a link to the previous page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ResourceCollectionDocument<T> setPreviousPageLink(String previousPage) {
        return setPreviousPageLink(new Link(previousPage));
    }

    /**
     * @param previousPage a {@link Link} to the previous page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ResourceCollectionDocument<T> setPreviousPageLink(Link previousPage) {
        Objects.requireNonNull(previousPage);
        getLinks().add(Link.PREV, previousPage);
        return this;
    }

    /**
     * @return a link to the previous page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getPreviousPageLink() {
        return getLink(Link.PREV);
    }

    /**
     * @param nextPage a link to the next page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public ResourceCollectionDocument<T> setNextPageLink(String nextPage) {
        return setNextPageLink(new Link(nextPage));
    }

    /**
     * @param nextPage a {@link Link} to the next page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ResourceCollectionDocument<T> setNextPageLink(Link nextPage) {
        Objects.requireNonNull(nextPage);
        getLinks().add(Link.NEXT, nextPage);
        return this;
    }

    /**
     * @return a link to the next page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getNextPageLink() {
        return getLink(Link.NEXT);
    }
}
