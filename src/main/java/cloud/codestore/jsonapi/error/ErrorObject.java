package cloud.codestore.jsonapi.error;

import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.link.LinksObject;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.annotation.*;

/**
 * Represents a JSON:API error object.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.1/#error-objects">jsonapi.org</a>
 */
@JsonPropertyOrder({"id", "links", "status", "code", "title", "detail", "source", "meta"})
public class ErrorObject {
    private String id;
    private LinksObject links = new LinksObject();
    private String status;
    private String code;
    private String title;
    private String detail;
    private ErrorSource source;
    private MetaInformation meta;

    /**
     * @return a unique identifier for this particular occurrence of the problem.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id a unique identifier for this particular occurrence of the problem.
     * @return this object.
     */
    public ErrorObject setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * @return the HTTP status code applicable to this problem, expressed as a string value.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the HTTP status code applicable to this problem, expressed as a string value.
     * @return this object.
     */
    public ErrorObject setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * @return an application-specific error code, expressed as a string value.
     */
    @JsonGetter("code")
    public String getCode() {
        return code;
    }

    /**
     * @param code an application-specific error code, expressed as a string value.
     * @return this object.
     */
    public ErrorObject setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * @return a short, human-readable summary of the problem that SHOULD NOT change from occurrence to occurrence
     * of the problem, except for purposes of localization.
     */
    @JsonGetter("title")
    public String getTitle() {
        return title;
    }

    /**
     * @param title a short, human-readable summary of the problem that SHOULD NOT change from occurrence to occurrence
     *              of the problem, except for purposes of localization.
     * @return this object.
     */
    public ErrorObject setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @return a human-readable explanation specific to this occurrence of the problem.
     * Like title, this field’s value can be localized.
     */
    @JsonGetter("detail")
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail a human-readable explanation specific to this occurrence of the problem.
     *               Like title, this field’s value can be localized.
     * @return this object.
     */
    public ErrorObject setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * @return a {@link LinksObject} containing the links of this error object.
     */
    @JsonGetter("links")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    public LinksObject getLinks() {
        return links;
    }

    /**
     * @param link a link that leads to further details about this particular occurrence of the problem.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or blank.
     */
    public ErrorObject setAboutLink(String link) {
        return setAboutLink(new Link(link));
    }

    /**
     * @param link a {@link Link} that leads to further details about this particular occurrence of the problem.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     */
    public ErrorObject setAboutLink(Link link) {
        links.add(Link.ABOUT, link);
        return this;
    }

    /**
     * @return a link that leads to further details about this particular occurrence of the problem.
     * May be {@code null}.
     */
    @JsonIgnore
    public Link getAboutLink() {
        return links.get(Link.ABOUT);
    }

    /**
     * @param link a link that identifies the type of error that this particular error is an instance of.
     * @return this object.
     * @throws IllegalArgumentException if the link is {@code null} or blank.
     * @since 1.1
     */
    public ErrorObject setTypeLink(String link) {
        return setTypeLink(new Link(link));
    }

    /**
     * @param link a {@link Link} that identifies the type of error that this particular error is an instance of.
     * @return this object.
     * @throws NullPointerException if the link is {@code null}.
     * @since 1.1
     */
    public ErrorObject setTypeLink(Link link) {
        links.add(Link.TYPE, link);
        return this;
    }

    /**
     * @return a link that identifies the type of error that this particular error is an instance of.
     * May be {@code null}.
     * @since 1.1
     */
    @JsonIgnore
    public Link getTypeLink() {
        return links.get(Link.TYPE);
    }

    /**
     * Adds a {@link Link link} to this error object.
     * Existing links with the same name will be replaced.
     *
     * @param linkName the name of the link.
     * @param link     a link.
     * @return this object.
     * @throws NullPointerException if {@code linkName} or {@code link} is {@code null}.
     */
    public ErrorObject addLink(String linkName, Link link) {
        links.add(linkName, link);
        return this;
    }

    /**
     * @param linkName the name of a link.
     * @return the corresponding link of this error object or {@code null} if the link does not exist.
     */
    public Link getLink(String linkName) {
        return links.get(linkName);
    }

    /**
     * @return a {@link ErrorSource} object containing references to the source of the error.
     */
    @JsonGetter("source")
    public ErrorSource getSource() {
        return source;
    }

    /**
     * @param source a {@link ErrorSource} object containing references to the source of the error.
     * @return this object.
     */
    public ErrorObject setSource(ErrorSource source) {
        this.source = source;
        return this;
    }

    /**
     * @return a {@link MetaInformation meta object} containing non-standard meta-information about the error.
     */
    @JsonGetter("meta")
    public MetaInformation getMeta() {
        return meta;
    }

    /**
     * @param meta a {@link MetaInformation meta object} containing non-standard meta-information about the error.
     * @return this object.
     */
    @JsonSetter("meta")
    public ErrorObject setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }
}
