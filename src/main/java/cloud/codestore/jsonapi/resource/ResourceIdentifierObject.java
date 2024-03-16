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
    private String lid;
    private MetaInformation meta;

    /**
     * @param type the type of the resource.
     * @param id   the id of the resource.
     * @throws IllegalArgumentException if the type or id is {@code null} or blank.
     */
    public ResourceIdentifierObject(String type, String id) {
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("'type' must not be null or blank.");
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("'id' must not be null or blank.");

        this.type = type;
        this.id = id;
    }

    /**
     * @param type the type of the resource.
     * @param id   the id of the resource.
     * @param lid  the local id of the resource if it represents a new resource to be created on the server.
     * @throws IllegalArgumentException if {@code type} or both, {@code id} and {@code lid} are {@code null} or blank.
     */
    @JsonCreator
    public ResourceIdentifierObject(
            @JsonProperty("type") String type,
            @JsonProperty("id") String id,
            @JsonProperty("lid") String lid
    ) {
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("'type' must not be null or blank.");

        this.type = type;
        if (id != null && !id.isBlank())
            this.id = id;
        if (lid != null && !lid.isBlank())
            this.lid = lid;

        if (this.id == null && this.lid == null)
            throw new IllegalArgumentException("'id' and 'lid' must not both be null or blank.");
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
     * @return the local id of this {@link ResourceIdentifierObject}.
     */
    @JsonGetter("lid")
    public String getLid() {
        return lid;
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
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        ResourceIdentifierObject that = (ResourceIdentifierObject) obj;
        return Objects.equals(type, that.type) && Objects.equals(id, that.id) && Objects.equals(lid, that.lid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, lid);
    }
}
