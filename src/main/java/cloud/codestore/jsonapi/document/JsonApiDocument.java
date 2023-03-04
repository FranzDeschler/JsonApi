package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.JsonApiObjectMapper;
import cloud.codestore.jsonapi.internal.JsonApiDocumentDeserializer;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.link.LinksObject;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a JSON:API document.<br/>
 * A JSON:API document must contain at least one of the following top-level members:
 * <ul>
 *     <li>{@code data}: one or more {@link ResourceObject resource objects} as the document’s “primary data”</li>
 *     <li>{@code meta}: a {@link MetaInformation meta object} that contains non-standard meta-information.</li>
 * </ul>
 * See <a href="https://jsonapi.org/format/1.0/#document-top-level">jsonapi.org</a>
 */
@JsonPropertyOrder({"jsonapi", "data", "included", "links", "meta"})
@JsonDeserialize(using = JsonApiDocumentDeserializer.class)
public abstract class JsonApiDocument {
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
        return new SingleResourceDocument<T>(data);
    }

    /**
     * Factory method to create a JSON:API document which contains a list of {@link ResourceObject resource objects} as its primary data.
     *
     * @param data the primary data of the JSON:API document.
     * @return a new {@link JsonApiDocument JSON:API document}.
     * @throws NullPointerException if {@code data} is {@code null}.
     */
    public static <T extends ResourceObject> JsonApiDocument of(T[] data) {
        return new ResourceCollectionDocument<T>(data);
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
     * @throws NullPointerException if {@code self} is {@code null}.
     */
    public JsonApiDocument setSelfLink(String self) {
        return addLink(new Link(Link.SELF, self));
    }

    /**
     * Adds a {@link Link link} to this JSON:API document.
     * Existing links with the same relation will be replaced.
     *
     * @param link a link.
     * @return this object.
     * @throws NullPointerException if {@code link} is {@code null}.
     */
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

    // ===== used internally for serialization =====

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
            if (!alreadyIncluded(resourceObject)) {
                includedResources.add(resourceObject);
            }
        }
    }

    private boolean alreadyIncluded(ResourceObject resourceToInclude) {
        for (ResourceObject includedResource : includedResources) {
            if (Objects.equals(includedResource.getIdentifier(), resourceToInclude.getIdentifier())) {
                return true;
            }
        }

        return false;
    }

    // ===== used internally for deserialization =====

    @JsonSetter("included")
    void setIncludedResources(List<ResourceObject> includedResources) {
        this.includedResources = includedResources;
    }

    @JsonSetter("links")
    void setLinksObject(LinksObject links) {
        this.links = links;
    }
}
