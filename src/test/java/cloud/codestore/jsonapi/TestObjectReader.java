package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.meta.MetaDeserializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Type;
import java.util.Map;

public class TestObjectReader {
    private final MetaDeserializerProxy metaDeserializer = new MetaDeserializerProxy();
    private final ObjectMapper INSTANCE;

    public TestObjectReader() {
        this(Map.of());
    }

    public TestObjectReader(Map<String, Class<? extends ResourceObject>> resourceMapping) {
        var objectMapper = new JsonApiObjectMapper(metaDeserializer);
        for (var mapping : resourceMapping.entrySet()) {
            objectMapper.registerResourceType(mapping.getKey(), mapping.getValue());
        }

        this.INSTANCE = objectMapper;
    }

    public <T> T read(String json, Class<T> type) {
        return read(json, toTypeReference(type), null);
    }

    public <T> T read(String json, TypeReference<T> typeReference) {
        return read(json, typeReference, null);
    }

    public <T> T read(String json, Class<T> type, MetaDeserializer metaDeserializer) {
        return read(json, toTypeReference(type), metaDeserializer);
    }

    private <T> T read(String json, TypeReference<T> type, MetaDeserializer metaDeserializer) {
        try {
            this.metaDeserializer.delegate = metaDeserializer;
            return INSTANCE.readValue(json, type);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace(System.err);
            Assertions.fail("Parsing JSON failed!");
            return null;
        }
    }

    private <T> TypeReference<T> toTypeReference(Class<T> type) {
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return type;
            }
        };
    }

    private static class MetaDeserializerProxy implements MetaDeserializer {
        MetaDeserializer delegate;

        @Override
        public Class<? extends MetaInformation> getClass(String path) {
            return delegate == null ? null : delegate.getClass(path);
        }

        @Override
        public MetaInformation deserialize(String path, ObjectNode node) throws Exception {
            return delegate == null ? null : delegate.deserialize(path, node);
        }
    }
}
