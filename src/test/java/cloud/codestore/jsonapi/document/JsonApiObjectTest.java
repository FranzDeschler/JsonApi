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
    @DisplayName("returns 1.1 as version")
    void version1() {
        String json = TestObjectWriter.write(new JsonApiObject());
        assertThat(json).isEqualToIgnoringNewLines("""
                {
                  "version" : "1.1"
                }""");
    }

    @Test
    @DisplayName("may contain a meta object")
    void containsMeta() {
        String json = TestObjectWriter.write(new JsonApiObject().setMeta(new DummyMetaInformation()));
        assertThat(json).isEqualToIgnoringNewLines("""
                {
                  "version" : "1.1",
                  "meta" : {
                    "info" : "dummy meta info"
                  }
                }""");
    }

    @Test
    @DisplayName("may contain an array or URIs for all applied extensions")
    void containsExtensionUris() {
        JsonApiObject jsonApiObject = new JsonApiObject()
                .setExtensions("https://jsonapi.org/ext/atomic", "https://jsonapi.org/ext/version");
        String json = TestObjectWriter.write(jsonApiObject);
        assertThat(json).isEqualToIgnoringNewLines("""
                {
                  "version" : "1.1",
                  "ext" : [ "https://jsonapi.org/ext/atomic", "https://jsonapi.org/ext/version" ]
                }""");
    }

    @Test
    @DisplayName("may contain an array or URIs for all applied profiles")
    void containsProfileUris() {
        JsonApiObject jsonApiObject = new JsonApiObject()
                .setProfiles("https://example.com/profiles/flexible-pagination", "https://example.com/profiles/resource-versioning");

        String json = TestObjectWriter.write(jsonApiObject);
        assertThat(json).isEqualToIgnoringNewLines("""
                {
                  "version" : "1.1",
                  "profile" : [ "https://example.com/profiles/flexible-pagination", "https://example.com/profiles/resource-versioning" ]
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
            assertThat(jsonApiObject.getExtensions()).isNull();
            assertThat(jsonApiObject.getProfiles()).isNull();
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

        @Test
        @DisplayName("which contains extension URIs")
        void withExtensions() {
            JsonApiObject jsonApiObject = TestObjectReader.read("""
                    {
                      "ext": [
                        "https://jsonapi.org/ext/atomic",
                        "https://jsonapi.org/ext/version"
                      ]
                    }""", JsonApiObject.class, path -> DummyMetaInformation.class);

            assertThat(jsonApiObject).isNotNull();
            assertThat(jsonApiObject.getExtensions())
                    .isNotNull()
                    .containsExactlyInAnyOrder(
                            "https://jsonapi.org/ext/atomic",
                            "https://jsonapi.org/ext/version"
                    );
        }

        @Test
        @DisplayName("which contains profile URIs")
        void withProfiles() {
            JsonApiObject jsonApiObject = TestObjectReader.read("""
                    {
                      "profile": [
                        "https://example.com/profiles/flexible-pagination",
                        "https://example.com/profiles/resource-versioning"
                      ]
                    }""", JsonApiObject.class, path -> DummyMetaInformation.class);

            assertThat(jsonApiObject).isNotNull();
            assertThat(jsonApiObject.getProfiles())
                    .isNotNull()
                    .containsExactlyInAnyOrder(
                            "https://example.com/profiles/flexible-pagination",
                            "https://example.com/profiles/resource-versioning"
                    );
        }
    }
}