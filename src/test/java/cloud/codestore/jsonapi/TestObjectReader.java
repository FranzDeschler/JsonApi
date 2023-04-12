package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.meta.MetaDeserializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Type;

public class TestObjectReader {
    private static final DynamicMetaDeserializer metaDeserializer = new DynamicMetaDeserializer();
    private static final ObjectMapper INSTANCE = objectMapper();

    private static ObjectMapper objectMapper() {
        return new JsonApiObjectMapper(metaDeserializer)
                .registerResourceType("article", Article.class)
                .registerResourceType("person", Person.class)
                .registerResourceType("comment", Comment.class);
    }

    public static <T> T read(String json, Class<T> type) {
        return read(json, toTypeReference(type), null);
    }

    public static <T> T read(String json, TypeReference<T> typeReference) {
        return read(json, typeReference, null);
    }

    public static <T> T read(String json, Class<T> type, MetaDeserializer metaDeserializer) {
        return read(json, toTypeReference(type), metaDeserializer);
    }

    public static <T> T read(String json, TypeReference<T> type, MetaDeserializer metaDeserializer) {
        try {
            TestObjectReader.metaDeserializer.delegate = metaDeserializer;
            return INSTANCE.readValue(json, type);
        } catch (JsonProcessingException exception) {
            Assertions.fail("Parsing JSON failed!", exception);
            return null;
        }
    }

    private static class DynamicMetaDeserializer implements MetaDeserializer {
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

    private static <T> TypeReference<T> toTypeReference(Class<T> type) {
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return type;
            }
        };
    }
}
