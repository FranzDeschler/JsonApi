package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.ExtensionBase;
import cloud.codestore.jsonapi.JsonApiObjectMapper;
import cloud.codestore.jsonapi.internal.JsonApiDocumentDeserializer;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.link.LinksObject;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

/**
 * Represents a JSON:API document.<br/>
 * A JSON:API document must contain at least one of the following top-level members:
 * <ul>
 *     <li>{@code data}: one or more {@link ResourceObject resource objects} as the document’s “primary data”</li>
 *     <li>{@code meta}: a {@link MetaInformation meta object} that contains non-standard meta-information.</li>
 * </ul>
 * See <a href="https://jsonapi.org/format/1.1/#document-top-level">jsonapi.org</a>
 */
@JsonPropertyOrder({"jsonapi", "data", "included", "links", "meta"})
@JsonDeserialize(using = JsonApiDocumentDeserializer.class)
public abstract class JsonApiDocument extends ExtensionBase<JsonApiDocument> {
    /**
     * The JSON:API media type.<br/>
     * See <a href="http://www.iana.org/assignments/media-types/application/vnd.api+json">www.iana.org</a>
     */
    public static final String MEDIA_TYPE = "application/vnd.api+json";

    private JsonApiObject jsonapi;
    private List<ResourceObject> includedResources = new LinkedList<>();
    private LinksObject links = new LinksObject();
    private MetaInformation meta;

    /**
     * Factory method to create a JSON:API document which contains a single {@link ResourceObject resource object} as its primary data.
     *
     * @param data the primary data of the JSON:API document.
     * @return a new {@link JsonApiDocument JSON:API document}.
     * @throws NullPointerException if {@code data} is {@code null}.
     */
    public static <T extends ResourceObject> JsonApiDocument of(T data) {
        return new SingleResourceDocument<>(data);
    }

    /**
     * Factory method to create a JSON:API document which contains a list of {@link ResourceObject resource objects} as its primary data.
     *
     * @param data the primary data of the JSON:API document.
     * @return a new {@link JsonApiDocument JSON:API document}.
     * @throws NullPointerException if {@code data} is {@code null}.
     */
    public static <T extends ResourceObject> JsonApiDocument of(T[] data) {
        return new ResourceCollectionDocument<>(data);
    }

    /**
     * Factory method to create a JSON:API document which does not contain primary data.
     *
     * @param meta a {@link MetaInformation meta object} that contains non-standard meta-information about the JSON:API document.
     * @return a new {@link JsonApiDocument JSON:API document}.
     * @throws NullPointerException if {@code meta} is {@code null}.
     */
    public static JsonApiDocument of(MetaInformation meta) {
        return new SingleResourceDocument<>(meta);
    }

    /**
     * Factory method to create a JSON:API document which only contains extension members.
     *
     * @param extensionMembers one or more members defined by an applied extension.
     * @return a new {@link JsonApiDocument JSON:API document}.
     * @throws NullPointerException     if {@code extensionMembers} is {@code null}.
     * @throws IllegalArgumentException if {@code extensionMembers} is empty.
     * @throws IllegalArgumentException if the name of one or more extension members is invalid.
     */
    public static JsonApiDocument of(Map<String, Object> extensionMembers) {
        return new SingleResourceDocument<>(extensionMembers);
    }

    /**
     * @param jsonapi a {@link JsonApiObject JSON:API Object} which contains information about the implementation of this JSON:API document.
     * @return this object.
     */
    @JsonSetter("jsonapi")
    public JsonApiDocument setJsonapiObject(JsonApiObject jsonapi) {
        this.jsonapi = jsonapi;
        return this;
    }

    /**
     * @return a {@link JsonApiObject JSON:API Object} which contains information about the implementation of this JSON:API document.
     */
    @JsonGetter("jsonapi")
    public JsonApiObject getJsonApiObject() {
        return jsonapi;
    }

    /**
     * @return a list of {@link ResourceObject resource objects} which are related to the primary data of this JSON:API document.
     */
    @JsonGetter("included")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<ResourceObject> getIncludedResources() {
        return Collections.unmodifiableList(includedResources);
    }

    /**
     * @param self the link that generated this JSON:API document.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public JsonApiDocument setSelfLink(String self) {
        return setSelfLink(new Link(self));
    }

    /**
     * @param self the link that generated this JSON:API document.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public JsonApiDocument setSelfLink(Link self) {
        Objects.requireNonNull(self);
        return addLink(Link.SELF, self);
    }

    /**
     * @return the link that generated this JSON:API document. May be {@code null}.
     */
    @JsonIgnore
    public Link getSelfLink() {
        return links.get(Link.SELF);
    }

    /**
     * @param related a related resource link when the primary data represents a resource relationship.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public JsonApiDocument setRelatedLink(String related) {
        return setRelatedLink(new Link(related));
    }

    /**
     * @param related a related resource link when the primary data represents a resource relationship.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public JsonApiDocument setRelatedLink(Link related) {
        Objects.requireNonNull(related);
        return addLink(Link.RELATED, related);
    }

    /**
     * @return a related resource link when the primary data represents a resource relationship. May be {@code null}.
     */
    @JsonIgnore
    public Link getRelatedLink() {
        return links.get(Link.RELATED);
    }

    /**
     * @param describedby a link to a description document.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     * @since 1.1
     */
    public JsonApiDocument setDescribedbyLink(String describedby) {
        return setDescribedbyLink(new Link(describedby));
    }

    /**
     * @param describedby a link to a description document.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     * @since 1.1
     */
    public JsonApiDocument setDescribedbyLink(Link describedby) {
        Objects.requireNonNull(describedby);
        return addLink(Link.DESCRIBEDBY, describedby);
    }

    /**
     * @return a link to a description document. May be {@code null}.
     */
    @JsonIgnore
    public Link getDescribedbyLink() {
        return links.get(Link.DESCRIBEDBY);
    }

    /**
     * @param firstPage a link to the first page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public JsonApiDocument setFirstPageLink(String firstPage) {
        return setFirstPageLink(new Link(firstPage));
    }

    /**
     * @param firstPage a link to the first page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public JsonApiDocument setFirstPageLink(Link firstPage) {
        Objects.requireNonNull(firstPage);
        getLinks().add(Link.FIRST, firstPage);
        return this;
    }

    /**
     * @return a link to the first page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getFirstPageLink() {
        return links.get(Link.FIRST);
    }

    /**
     * @param lastPage a link to the last page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public JsonApiDocument setLastPageLink(String lastPage) {
        return setLastPageLink(new Link(lastPage));
    }

    /**
     * @param lastPage a {@link Link} to the last page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public JsonApiDocument setLastPageLink(Link lastPage) {
        Objects.requireNonNull(lastPage);
        getLinks().add(Link.LAST, lastPage);
        return this;
    }

    /**
     * @return a link to the last page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getLastPageLink() {
        return links.get(Link.LAST);
    }

    /**
     * @param previousPage a link to the previous page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public JsonApiDocument setPreviousPageLink(String previousPage) {
        return setPreviousPageLink(new Link(previousPage));
    }

    /**
     * @param previousPage a {@link Link} to the previous page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public JsonApiDocument setPreviousPageLink(Link previousPage) {
        Objects.requireNonNull(previousPage);
        getLinks().add(Link.PREV, previousPage);
        return this;
    }

    /**
     * @return a link to the previous page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getPreviousPageLink() {
        return links.get(Link.PREV);
    }

    /**
     * @param nextPage a link to the next page of the primary data.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or empty.
     */
    public JsonApiDocument setNextPageLink(String nextPage) {
        return setNextPageLink(new Link(nextPage));
    }

    /**
     * @param nextPage a {@link Link} to the next page of the primary data.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public JsonApiDocument setNextPageLink(Link nextPage) {
        Objects.requireNonNull(nextPage);
        getLinks().add(Link.NEXT, nextPage);
        return this;
    }

    /**
     * @return a link to the next page of the primary data. May be {@code null}.
     */
    @JsonIgnore
    public Link getNextPageLink() {
        return links.get(Link.NEXT);
    }

    /**
     * Adds a {@link Link link} to this JSON:API document.
     * Existing links with the same name will be replaced.
     *
     * @param linkName the name of the link.
     * @param link     a link.
     * @return this object.
     * @throws NullPointerException if {@code linkName} or {@code link} is {@code null}.
     */
    public JsonApiDocument addLink(String linkName, Link link) {
        links.add(linkName, link);
        return this;
    }

    /**
     * Adds a {@link Link link} to this JSON:API document.
     *
     * @deprecated use {@link #addLink(String, Link)} instead.
     */
    @Deprecated(since = "1.1")
    public JsonApiDocument addLink(Link link) {
        links.add(link);
        return this;
    }

    /**
     * @return a {@link LinksObject} containing the links of this JSON:API document.
     */
    @JsonGetter("links")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    public LinksObject getLinks() {
        return links;
    }

    /**
     * @param meta a {@link MetaInformation meta object} containing non-standard meta-information about this JSON:API document.
     * @return this object.
     */
    @JsonSetter("meta")
    public JsonApiDocument setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }

    /**
     * @return a {@link MetaInformation meta object} containing non-standard meta-information about this JSON:API document.
     */
    @JsonGetter("meta")
    public MetaInformation getMeta() {
        return meta;
    }

    /**
     * Convenient method to serialize this JSON:API document.
     * Usually used for testing.
     *
     * @return this JSON:API document as JSON string.
     * @throws JsonProcessingException if this object could not be serialized.
     */
    public String toJson() throws JsonProcessingException {
        return new JsonApiObjectMapper().writeValueAsString(this);
    }

    // ===== used internally for serialization / deserialization =====

    /**
     * Only for internal use.<br/>
     * Includes the given {@link ResourceObject resource objects} into this JSON:API document.
     * Duplicate objects (those which have the same type and id) are ignored.
     *
     * @param resourceObjects one or more {@link ResourceObject}s.
     * @throws NullPointerException if {@code resourceObjects} is null or contains {@code null} values.
     */
    public void include(ResourceObject... resourceObjects) {
        Objects.requireNonNull(resourceObjects);

        for (ResourceObject resourceObject : resourceObjects) {
            Objects.requireNonNull(resourceObject);
            if (notAlreadyIncluded(resourceObject)) {
                includedResources.add(resourceObject);
            }
        }
    }

    private boolean notAlreadyIncluded(ResourceObject resourceToInclude) {
        return includedResources.stream().noneMatch(includedResource ->
                Objects.equals(includedResource.getIdentifier(), resourceToInclude.getIdentifier()));
    }

    /**
     * Sets the included resource objects and filters duplicates based on type and id.
     */
    @JsonSetter("included")
    private void setIncludedResources(List<ResourceObject> includedResources) {
        for (ResourceObject resourceObject : includedResources) {
            if (notAlreadyIncluded(resourceObject)) {
                this.includedResources.add(resourceObject);
            }
        }
    }

    @JsonSetter("links")
    private void setLinksObject(LinksObject links) {
        this.links = links;
    }

    private final List<Relationship> relationships = new LinkedList<>();

    /**
     * Only for internal use.
     * Adds a backlink to deserialized, nested relationship.
     * They will be used for linking relationships to included resources.
     */
    public void addRelationshipBacklink(Relationship relationship) {
        relationships.add(relationship);
    }

    /**
     * Only for internal use.
     */
    @JsonIgnore
    public List<Relationship> getRelationshipBacklinks() {
        return relationships;
    }
}
