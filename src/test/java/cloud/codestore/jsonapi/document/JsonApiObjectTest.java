package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A jsonapi object")
class JsonApiObjectTest {
    @Test
    @DisplayName("returns 1.0 as version")
    void version1() {
        String json = TestObjectWriter.write(new JsonApiObject());
        assertThat(json).isEqualToIgnoringNewLines("""
                {
                  "version" : "1.0"
                }""");
    }

    @Test
    @DisplayName("may contain a meta object")
    void containsMeta() {
        String json = TestObjectWriter.write(new JsonApiObject().setMeta(new DummyMetaInformation()));
        assertThat(json).isEqualToIgnoringNewLines("""
                {
                  "version" : "1.0",
                  "meta" : {
                    "info" : "dummy meta info"
                  }
                }""");
    }

    @Nested
    @DisplayName("can be deserialized from a JSON string")
    class DeserializationTest {
        @Test
        @DisplayName("which is empty")
        void emptyJson() {
            JsonApiObject jsonApiObject = TestObjectReader.read("{}", JsonApiObject.class);

            assertThat(jsonApiObject).isNotNull();
            assertThat(jsonApiObject.getVersion()).isEqualTo("1.0");
            assertThat(jsonApiObject.getMeta()).isNull();
        }

        @Test
        @DisplayName("which contains a version")
        void withVersion() {
            JsonApiObject jsonApiObject = TestObjectReader.read("""
                    {
                      "version" : "2.0"
                    }""", JsonApiObject.class);

            assertThat(jsonApiObject).isNotNull();
            assertThat(jsonApiObject.getVersion()).isEqualTo("2.0");
            assertThat(jsonApiObject.getMeta()).isNull();
        }

        @Test
        @DisplayName("which contains meta information")
        void withMeta() {
            JsonApiObject jsonApiObject = TestObjectReader.read("""
                    {
                      "meta" : {
                        "info" : "dummy meta info"
                      }
                    }""", JsonApiObject.class, path -> DummyMetaInformation.class);

            assertThat(jsonApiObject).isNotNull();
            assertThat(jsonApiObject.getVersion()).isEqualTo("1.0");
            assertThat(jsonApiObject.getMeta()).isNotNull();
        }
    }
}