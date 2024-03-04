package cloud.codestore.jsonapi.document;

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
    private Map<String, Object> extensionMembers = new HashMap<>();

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
     * @throws NullPointerException if {@code self} is {@code null}.
     */
    public JsonApiDocument setSelfLink(String self) {
        return addLink(Link.SELF, new Link(self));
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
     * @param extensionMembers the extension members to set on this JSON:API document.
     * @return this object.
     * @throws NullPointerException     if {@code extensionMembers} is {@code null}.
     * @throws IllegalArgumentException if the name of one or more members is invalid.
     */
    public JsonApiDocument setExtensionMembers(Map<String, Object> extensionMembers) {
        Objects.requireNonNull(extensionMembers);
        this.extensionMembers.clear();
        for (var entry : extensionMembers.entrySet()) {
            setExtensionMember(entry.getKey(), entry.getValue());
        }

        return this;
    }

    /**
     * Adds an extension member to this JSON:API document.
     *
     * @param memberName the full name of the extension member including the extension-namespace. Must not be {@code null}.
     * @param value      the corresponding value of the member.
     * @return this object.
     * @throws NullPointerException     if {@code memberName} is {@code null}.
     * @throws IllegalArgumentException if {@code memberName} is invalid.
     */
    public JsonApiDocument setExtensionMember(String memberName, Object value) {
        Objects.requireNonNull(memberName);
        if (!memberName.contains(":")) {
            throw new IllegalArgumentException("Invalid extension member '" + memberName + "'. " +
                                               "Extension member names must follow the pattern <namespace>:<name>");
        }

        extensionMembers.put(memberName, value);
        return this;
    }

    /**
     * Returns the value of the given extension member name.
     *
     * @param memberName the full name of the extension member including the extension-namespace. Must not be {@code null}.
     * @return the associated value or {@code null}.
     * @throws NullPointerException if {@code memberName} is {@code null}.
     */
    @JsonIgnore
    public Object getExtensionMember(String memberName) {
        Objects.requireNonNull(memberName);
        return extensionMembers.get(memberName);
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

    // ===== used internally for serialization / deserialization =====

    @JsonSetter("included")
    void setIncludedResources(List<ResourceObject> includedResources) {
        this.includedResources = includedResources;
    }

    @JsonSetter("links")
    void setLinksObject(LinksObject links) {
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

    /**
     * Used to serialize extension members.
     */
    @JsonAnyGetter
    private Map<String, Object> getExtensionMembers() {
        return extensionMembers;
    }

    /**
     * Adds any top level property as extension member if the key is a valid extension member name.
     *
     * @param key   a valid extension member name.
     * @param value the corresponding value.
     */
    @JsonAnySetter
    private void setDeserializedExtensionMember(String key, Object value) {
        if (!key.startsWith("@") && key.contains(":")) {
            setExtensionMember(key, value);
        }
    }
}
