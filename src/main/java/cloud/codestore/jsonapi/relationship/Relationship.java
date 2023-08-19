package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.link.LinksObject;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

/**
 * Represents a {@code Relationship Object}.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.0/#document-resource-object-relationships">jsonapi.org</a>
 */
public class Relationship {
    private LinksObject links = new LinksObject();
    private MetaInformation meta;

    /**
     * Creates a new relationship without any links, data or meta information.
     */
    public Relationship() {
    }

    /**
     * Creates a new relationship with the given link as "related" link.
     *
     * @param relatedResourceLink a <a href="https://jsonapi.org/format/1.0/#document-resource-object-related-resource-links">related resource link</a>.
     */
    public Relationship(String relatedResourceLink) {
        setRelatedResourceLink(relatedResourceLink);
    }

    /**
     * @param link the "self" link of this relationship.
     * @return this object.
     */
    public Relationship setSelfLink(String link) {
        links = Objects.requireNonNullElseGet(links, LinksObject::new);
        links.add(new Link(Link.SELF, link));
        return this;
    }

    /**
     * @param relatedResourceLink sets the "related" link of this relationship.
     * @return this object.
     */
    public Relationship setRelatedResourceLink(String relatedResourceLink) {
        links.add(new Link(Link.RELATED, relatedResourceLink));
        return this;
    }

    /**
     * @return the "related" link of this relationship or {@code null} if this relationship does not contain a "related" link.
     */
    @JsonIgnore
    public String getRelatedResourceLink() {
        Link link = links.get(Link.RELATED);
        return link == null ? null : link.getHref();
    }

    /**
     * @return a {@link LinksObject} containing the links of this relationship object.
     * The result is never {@code null} but the {@link LinksObject} may be empty.
     */
    @JsonGetter("links")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    public LinksObject getLinks() {
        return links;
    }

    /**
     * @return a {@link MetaInformation} object that contains non-standard meta-information about this relationship.
     * The object is {@code null} if no meta-information are set.
     */
    @JsonGetter("meta")
    public MetaInformation getMeta() {
        return meta;
    }

    /**
     * @param meta sets the {@link MetaInformation} object that contains non-standard meta-information about this relationship.
     * @return this object.
     */
    @JsonSetter("meta")
    public Relationship setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }

    /**
     * On server side: indicates whether this relationship contains related resources that should be included
     * in the final JSON.
     * <br/><br/>
     * On client side: indicates whether this relationship refers to resource objects that are included in the
     * JSON:API document and can be fetched by calling {@link  ToOneRelationship#getRelatedResource()}
     * or {@link  ToManyRelationship#getRelatedResource()}.
     *
     * @return whether this relationship contains related resources.
     */
    @JsonIgnore
    public boolean isIncluded() {
        return false;
    }
}
