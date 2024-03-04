package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.internal.HreflangDeserializer;
import cloud.codestore.jsonapi.internal.HreflangSerializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single link inside a {@link LinksObject}.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.1/#document-links">jsonapi.org</a>
 */
public class Link {

    public static final String SELF = "self";
    public static final String RELATED = "related";
    public static final String FIRST = "first";
    public static final String LAST = "last";
    public static final String PREV = "prev";
    public static final String NEXT = "next";
    public static final String ABOUT = "about";
    public static final String DESCRIBEDBY = "describedby";

    private String href;
    private String relation;
    private MetaInformation meta;
    private Link describedby;
    private String title;
    private String type;
    private List<String> hreflang;

    /**
     * Creates a new {@link Link} object.
     *
     * @param href the link’s URL.
     * @throws IllegalArgumentException if the href is {@code null} or a blank String.
     */
    @JsonCreator
    public Link(@JsonProperty("href") String href) {
        if (href == null || href.isBlank())
            throw new IllegalArgumentException("Parameter 'href' must not be null or blank.");

        this.href = href;
    }

    /**
     * @deprecated use {@link #Link(String)} instead.
     */
    @Deprecated(since = "1.1")
    public Link(String relation, String href) {
        this(href);
        this.relation = relation;
    }

    /**
     * @deprecated use {@link #Link(String)} instead.
     */
    @Deprecated(since = "1.1")
    public Link(String relation, String href, MetaInformation meta) {
        this(relation, href);
        this.meta = meta;
    }

    /**
     * @return the relation type of this {@link Link}.
     */
    @JsonGetter("rel")
    public String getRelation() {
        return relation;
    }

    /**
     * @param relation a string indicating the link’s relation type.
     *                 The string MUST be a <a href="https://datatracker.ietf.org/doc/html/rfc8288#section-2.1">valid link relation type</a>.
     * @return this object.
     */
    @JsonSetter("rel")
    public Link setRelation(String relation) {
        this.relation = relation;
        return this;
    }

    /**
     * @return a link to a description document (e.g. OpenAPI or JSON Schema) for the link target.
     */
    @JsonGetter(DESCRIBEDBY)
    public Link getDescribedby() {
        return describedby;
    }

    /**
     * @param describedby a link to a description document (e.g. OpenAPI or JSON Schema) for the link target.
     * @return this object.
     */
    @JsonSetter(DESCRIBEDBY)
    public Link setDescribedby(Link describedby) {
        this.describedby = describedby;
        return this;
    }

    /**
     * @return a string which serves as a label for the destination of a link such that it can be used as a human-readable identifier (e.g., a menu entry).
     */
    @JsonGetter("title")
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title a string which serves as a label for the destination of a link such that it can be used as a human-readable identifier (e.g., a menu entry).
     * @return this object.
     */
    @JsonSetter("title")
    public Link setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @return the media type of the link’s target.
     */
    @JsonGetter("type")
    public String getType() {
        return type;
    }

    /**
     * @param type the media type of the link’s target.
     * @return this object.
     */
    @JsonSetter("type")
    public Link setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * @return a List, containing one or more Strings indicating the language(s) of the link’s target.
     */
    @JsonGetter("hreflang")
    @JsonSerialize(using = HreflangSerializer.class)
    public List<String> getHreflang() {
        return hreflang;
    }

    /**
     * @param hreflang one or more Strings indicating the language(s) of the link’s target.
     *                 Each string MUST be a valid language tag [<a href="https://datatracker.ietf.org/doc/html/rfc5646">RFC5646</a>].
     * @return this object.
     */
    @JsonSetter("hreflang")
    @JsonDeserialize(using = HreflangDeserializer.class)
    public Link setHreflang(String... hreflang) {
        this.hreflang = hreflang == null ? null : List.of(hreflang);
        return this;
    }

    /**
     * @return the link’s URL.
     */
    @JsonGetter("href")
    public String getHref() {
        return href;
    }

    /**
     * @return a {@link MetaInformation} object containing non-standard meta-information about the link.
     */
    @JsonGetter("meta")
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
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Link link = (Link) obj;
        return Objects.equals(relation, link.relation) && Objects.equals(href, link.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation, href);
    }
}
