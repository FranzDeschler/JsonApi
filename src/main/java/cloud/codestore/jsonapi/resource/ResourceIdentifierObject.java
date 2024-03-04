package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

/**
 * Represents a {@code Resource Identifier Object}.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-identifier-objects">jsonapi.org</a>
 */
public class ResourceIdentifierObject {
    private String type;
    private String id;
    private MetaInformation meta;

    /**
     * Creates a new {@link ResourceIdentifierObject}.
     *
     * @param type the type of the resource.
     * @param id   the id of the resource.
     * @throws IllegalArgumentException if the type or id is {@code null} or blank.
     */
    @JsonCreator
    public ResourceIdentifierObject(
            @JsonProperty("type") String type,
            @JsonProperty("id") String id
    ) {
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Parameter 'type' must not be null or blank.");
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Parameter 'id' must not be null or blank.");

        this.type = type;
        this.id = id;
    }

    /**
     * @return the type of this {@link ResourceIdentifierObject}.
     */
    @JsonGetter("type")
    public String getType() {
        return type;
    }

    /**
     * @return the id of this {@link ResourceIdentifierObject}.
     */
    @JsonGetter("id")
    public String getId() {
        return id;
    }

    /**
     * @return a {@link MetaInformation} object which contains non-standard meta-information about this {@link ResourceIdentifierObject}.
     */
    @JsonGetter("meta")
    public MetaInformation getMeta() {
        return meta;
    }

    /**
     * @param meta a {@link MetaInformation} object which contains non-standard meta-information about this {@link ResourceIdentifierObject}.
     * @return this object.
     */
    @JsonSetter("meta")
    public ResourceIdentifierObject setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ResourceIdentifierObject that = (ResourceIdentifierObject) obj;
        return Objects.equals(type, that.type) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
