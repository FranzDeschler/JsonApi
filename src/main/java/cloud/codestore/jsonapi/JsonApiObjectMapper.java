package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.meta.MetaDeserializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;

/**
 * An {@link ObjectMapper} that needs to be used for serializing and deserializing JSON:API documents.
 */
public class JsonApiObjectMapper extends ObjectMapper {
    /**
     * Creates a new instance without registering an application specific {@link MetaDeserializer}.
     * Thus, {@link MetaInformation} objects will not be deserialized.
     */
    public JsonApiObjectMapper() {
        this(null);
    }

    /**
     * Creates a new instance and registers the given {@link MetaDeserializer}.
     * @param metaDeserializer an application specific {@link MetaDeserializer}.
     */
    public JsonApiObjectMapper(MetaDeserializer metaDeserializer) {
        registerModule(new JsonApiModule(metaDeserializer));
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        enable(SerializationFeature.INDENT_OUTPUT);
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
    }

    /**
     * Binds the type of a JSON:API resource object with the corresponding Java class.
     * @param typeName the name of the type of a JSON:API resource object.
     * @param type a Java class.
     * @return this object.
     */
    public JsonApiObjectMapper registerResourceType(String typeName, Class<? extends ResourceObject> type) {
        registerSubtypes(new NamedType(type, typeName));
        return this;
    }

    /**
     * Binds the type of a JSON:API resource object with the corresponding Java class.
     * The name of the JSON:API resource object is derived from the name of the given class.
     * For example, {@code Article.class} will be bound to the resource name {@code "article"}.
     *
     * @param type a Java class.
     * @return this object.
     * @throws IllegalArgumentException if the given class is an anonymous class which has no name.
     */
    public JsonApiObjectMapper registerResourceType(Class<? extends ResourceObject> type) {
        String typeName = type.getSimpleName();
        if (typeName.isEmpty()) {
            throw new IllegalArgumentException("Anonymous classes are not allowed in this method.");
        }

        typeName = Character.toLowerCase(typeName.charAt(0)) + typeName.substring(1);
        registerSubtypes(new NamedType(type, typeName));
        return this;
    }
}
