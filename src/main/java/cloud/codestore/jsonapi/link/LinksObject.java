package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.internal.LinksObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

/**
 * Represents a {@code Links} object.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.0/#document-links">jsonapi.org</a>
 */
@JsonDeserialize(using = LinksObjectDeserializer.class)
public class LinksObject {
    private final Map<String, Link> links = new HashMap<>();

    /**
     * Creates an empty {@link LinksObject}.
     */
    public LinksObject() {}

    /**
     * Adds a {@link Link} object to this {@link LinksObject}.
     * Links with the same relation will be replaced.
     *
     * @deprecated use {@link #add(String, Link)} instead.
     */
    @Deprecated(since = "1.1")
    public LinksObject add(Link link) {
        Objects.requireNonNull(link);
        this.links.put(link.getRelation(), link);
        link.setRelation(null);
        return this;
    }

    /**
     * Adds a {@link Link} object to this {@link LinksObject}.
     * Links with the same name will be replaced.
     *
     * @param name the name of the link. May be different from the link's relation type.
     * @param link the link to add.
     * @return this object.
     * @throws NullPointerException if {@code name} or {@code link} is {@code null}.
     */
    public LinksObject add(String name, Link link) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(link);
        this.links.put(name, link);
        return this;
    }

    /**
     * Returns the {@link Link} object associated with the given relation.
     * @param relation the relation of the link to get.
     * @return the associated link or {@code null} if there is no link with such a relation.
     */
    public Link get(String relation) {
        return links.get(relation);
    }

    /**
     * Returns the link associated with the given relation as String.
     * @param relation the relation of a link.
     * @return the associated link or {@code null} if there is no link with such a relation.
     */
    public String getHref(String relation) {
        return links.containsKey(relation) ? links.get(relation).getHref() : null;
    }

    /**
     * @return the {@link Link} objects of this {@link LinksObject} as list.
     * The list is empty, if there are no links, but never {@code null}.
     */
    public List<Link> asList() {
        List<Link> list = new LinkedList<>(links.values());
        return Collections.unmodifiableList(list);
    }

    /**
     * Convenient method to get the "self" link out of this {@link LinksObject}.
     *
     * @return the "self" link as String or {@code null} if this {@link LinksObject} does not contain a "self" link.
     */
    public String getSelfLink() {
        return getHref(Link.SELF);
    }

    /**
     * @return {@code true} if this {@link LinksObject} does not contain any links.
     */
    public boolean isEmpty() {
        return links.isEmpty();
    }

    /**
     * Used internally for serialization.
     *
     * @return an unmodifiable representation of this {@link LinksObject}.
     * Each entry of the returned map contains the relation of the link as key and the href of the link as value.
     */
    @JsonValue
    Map<String, Link> getLinks() {
        return isEmpty() ? null : Collections.unmodifiableMap(links);
    }
}
