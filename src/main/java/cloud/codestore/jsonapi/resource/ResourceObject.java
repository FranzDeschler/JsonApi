package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.document.JsonApiDocument;
import cloud.codestore.jsonapi.internal.VirtualAttributesWriter;
import cloud.codestore.jsonapi.internal.VirtualRelationshipsWriter;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.link.LinksObject;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonAppend;

/**
 * Represents a JSON:API resource object.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.0/#document-resource-objects">jsonapi.org</a>
 */
@JsonAppend(props = {
        @JsonAppend.Prop(value = VirtualAttributesWriter.class, name = "attributes"),
        @JsonAppend.Prop(value = VirtualRelationshipsWriter.class, name = "relationships")
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
public abstract class ResourceObject {
    private String type;
    private String id;
    private LinksObject links = new LinksObject();
    private MetaInformation meta;

    private JsonApiDocument parent;

    /**
     * Creates a new {@link ResourceObject} with the given type.
     *
     * @param type the type of this resource object.
     * @throws IllegalArgumentException if the type is {@code null} or blank.
     */
    public ResourceObject(String type) {
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Parameter 'type' must not be null or blank.");

        this.type = type;
    }

    /**
     * Creates a new {@link ResourceObject}.
     *
     * @param type the type of the resource object.
     * @param id   the id of the resource object.
     * @throws IllegalArgumentException if the type or id is {@code null} or blank.
     */
    public ResourceObject(String type, String id) {
        this(type);

        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Parameter 'id' must not be null or blank.");

        this.id = id;
    }

    /**
     * @return the type of this resource object.
     */
    @JsonGetter("type")
    public String getType() {
        return type;
    }

    /**
     * @return the id of this resource object.
     */
    @JsonGetter("id")
    public String getId() {
        return id;
    }

    /**
     * @param id sets the id of this resource object.
     */
    @JsonSetter("id")
    void setId(String id) {
        this.id = id;
    }

    /**
     * @param self the link that generated this resource object.
     * @return this object.
     * @throws IllegalArgumentException if {@code self} is {@code null} or blank.
     */
    public ResourceObject setSelfLink(String self) {
        links.add(new Link(Link.SELF, self));
        return this;
    }

    /**
     * Convenient method to get the "self" link of this resource object.
     *
     * @return the "self" link if present - otherwise {@code null}.
     */
    @JsonIgnore
    public String getSelfLink() {
        return links == null ? null : links.getSelfLink();
    }

    /**
     * @return a {@link LinksObject} containing the links of this resource object.
     */
    @JsonGetter("links")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    public LinksObject getLinks() {
        return links;
    }

    /**
     * @return a {@link MetaInformation meta object} containing non-standard meta-information about this resource object.
     */
    @JsonGetter("meta")
    public MetaInformation getMeta() {
        return meta;
    }

    /**
     * @param meta a {@link MetaInformation meta object} containing non-standard meta-information about this resource object.
     */
    @JsonSetter("meta")
    public ResourceObject setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }

    /**
     * Convenient method to create a {@link JsonApiDocument} including this object as primary data.
     *
     * @return a new {@link JsonApiDocument}.
     */
    public JsonApiDocument asDocument() {
        return JsonApiDocument.of(this);
    }

    /**
     * Convenient method to create a {@link ToOneRelationship} for the given {@link ResourceObject} that will be
     * included in the resulting JSON:API document.
     *
     * @param resourceObject a resource object.
     * @return a {@link ToOneRelationship} which contains the given resource object as related resource.
     */
    public static <T extends ResourceObject> ToOneRelationship<T> asRelationship(T resourceObject) {
        return new ToOneRelationship<>(resourceObject);
    }

    /**
     * Convenient method to create a {@link ToManyRelationship} for the given {@link ResourceObject}s that will be
     * included in the resulting JSON:API document.
     *
     * @param resourceObjects one or more resource objects.
     * @return a {@link ToManyRelationship} which contains the given resource objects as related resources.
     */
    public static <T extends ResourceObject> ToManyRelationship<T> asRelationship(T[] resourceObjects) {
        return new ToManyRelationship<>(resourceObjects);
    }

    /**
     * @return a {@link ResourceIdentifierObject} which contains the type and id of this resource object.
     */
    @JsonIgnore
    public ResourceIdentifierObject getIdentifier() {
        return new ResourceIdentifierObject(type, id);
    }

    /**
     * @return the parent {@link JsonApiDocument} of this resource object.
     */
    @JsonIgnore
    public JsonApiDocument getParent() {
        return parent;
    }

    /**
     * @param parent the parent {@link JsonApiDocument} of this resource object.
     */
    @JsonIgnore
    public void setParent(JsonApiDocument parent) {
        this.parent = parent;
    }
}
