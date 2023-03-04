package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

/**
 * Represents a JSON:API Object which contains information about the implementation of the JSON:API document.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.0/#document-jsonapi-object">jsonapi.org</a>
 */
public class JsonApiObject {
    private final String version;
    private MetaInformation meta;

    /**
     * Creates a new JSON:API Object with "version" set to "1.0".
     */
    public JsonApiObject() {
        this("1.0", null);
    }

    /**
     * Creates a new JSON:API Object with "version" set to "1.0" and the given {@link MetaInformation}.
     *
     * @param meta the {@link MetaInformation} to set.
     */
    public JsonApiObject(MetaInformation meta) {
        this();
        setMeta(meta);
    }

    /**
     * Used internally for deserialization.
     */
    @JsonCreator
    JsonApiObject(@JsonProperty("version") String version, @JsonProperty("meta") MetaInformation meta) {
        this.version = Objects.requireNonNullElse(version, "1.0");
        this.meta = meta;
    }

    /**
     * @return the "version" of this JSON:API Object indicating the highest JSON API version supported.
     */
    @JsonGetter("version")
    public String getVersion() {
        return version;
    }

    /**
     * @return a {@link MetaInformation meta object} which contains non-standard meta-information.
     */
    @JsonGetter("meta")
    public MetaInformation getMeta() {
        return meta;
    }

    /**
     * @param meta a {@link MetaInformation meta object} which contains non-standard meta-information.
     *
     * @return this object.
     */
    @JsonSetter("meta")
    public JsonApiObject setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }
}
