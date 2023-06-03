package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

/**
 * Represents a JSON:API document containing a single resource object as primary data.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.0/#document-top-level">jsonapi.org</a>
 */
@JsonDeserialize //override deserializer from base class to avoid recursive call
public class SingleResourceDocument<T extends ResourceObject> extends JsonApiDocument {
    private T data;

    /** Used internally for deserialization. */
    SingleResourceDocument() {}

    /**
     * Creates a new {@link SingleResourceDocument} with the given primary data.
     *
     * @param data the primary data of this JSON:API document.
     * @throws NullPointerException if {@code data} is {@code null}.
     */
    public SingleResourceDocument(T data) {
        setData(data);
    }

    /**
     * Creates a new {@link SingleResourceDocument} with the given meta-information.
     * This should be used if the JSON:API document does not contain primary data.
     * According to the JSON:API specification, a JSON:API document must contain at least
     * a "data" or "meta" property as top-level member.
     *
     * @param meta the meta-information of this JSON:API document.
     * @throws NullPointerException if {@code meta} is {@code null}.
     */
    public SingleResourceDocument(MetaInformation meta) {
        Objects.requireNonNull(meta);
        setMeta(meta);
    }

    /**
     * @param data the primary data of this JSON:API document.
     * @throws NullPointerException if {@code data} is {@code null}.
     * @return this object.
     */
    @JsonSetter("data")
    public JsonApiDocument setData(T data) {
        Objects.requireNonNull(data);

        this.data = data;
        data.setParent(this);

        return this;
    }

    /**
     * @return the {@link ResourceObject} which was set as the primary data of this JSON:API document
     * or {@code null} if no primary data was set.
     */
    @JsonGetter("data")
    public T getData() {
        return data;
    }
}
