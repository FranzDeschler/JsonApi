package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.internal.LinkSerializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

/**
 * Represents a single link inside a {@link LinksObject}.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.0/#document-links">jsonapi.org</a>
 */
@JsonSerialize(using = LinkSerializer.class)
public class Link {

    public static final String SELF = "self";
    public static final String RELATED = "related";
    public static final String FIRST = "first";
    public static final String LAST = "last";
    public static final String PREV = "prev";
    public static final String NEXT = "next";
    public static final String ABOUT = "about";

    private String relation;
    private String href;
    private MetaInformation meta;

    /**
     * Used internally for deserialization.
     */
    @JsonCreator
    Link(@JsonProperty("href") String href, @JsonProperty("meta") MetaInformation meta) {
        this.relation = null;
        this.href = href;
        this.meta = meta;
    }

    /**
     * Creates a new {@link Link} object.
     *
     * @param relation the relation of this link.
     * @param href     the link’s URL.
     */
    public Link(String relation, String href) {
        this(relation, href, null);
    }

    /**
     * Creates a new {@link Link} object.
     *
     * @param relation the relation of this link.
     * @param href     the link’s URL.
     * @param meta     a {@link MetaInformation} object containing non-standard meta-information about the link.
     * @throws IllegalArgumentException if the relation or href is {@code null} or a blank String.
     */
    public Link(String relation, String href, MetaInformation meta) {
        if (relation == null || relation.isBlank())
            throw new IllegalArgumentException("Parameter 'relation' must not be null or blank.");
        if (href == null || href.isBlank())
            throw new IllegalArgumentException("Parameter 'href' must not be null or blank.");

        this.relation = relation;
        this.href = href;
        this.meta = meta;
    }

    /**
     * @return the relation of this {@link Link}.
     */
    public String getRelation() {
        return relation;
    }

    /**
     * @return the link’s URL.
     */
    public String getHref() {
        return href;
    }

    /**
     * @return a {@link MetaInformation} object containing non-standard meta-information about the link.
     */
    public MetaInformation getMeta() {
        return meta;
    }

    /**
     * @param meta a {@link MetaInformation} object containing non-standard meta-information about the link.
     * @return this object.
     */
    @JsonSetter("meta")
    public Link setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Link link = (Link) obj;
        return Objects.equals(relation, link.relation) && Objects.equals(href, link.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation, href);
    }
}
