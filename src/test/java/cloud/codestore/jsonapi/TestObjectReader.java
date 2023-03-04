package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.meta.MetaDeserializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.Assertions;

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
        return read(json, type, null);
    }

    public static <T> T read(String json, Class<T> type, MetaDeserializer metaDeserializer) {
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

}
