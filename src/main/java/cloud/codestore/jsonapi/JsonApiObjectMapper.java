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
}
