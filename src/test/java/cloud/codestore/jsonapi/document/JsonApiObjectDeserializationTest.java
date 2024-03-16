package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.meta.MetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-jsonapi-object">jsonapi.org</a>
 */
@DisplayName("A JSON:API object")
class JsonApiObjectDeserializationTest {
    private static final TestObjectReader reader = new TestObjectReader();

    @Nested
    @DisplayName("may contain")
    class OptionalMembers {
        @Test
        @DisplayName("the highest JSON:API version supported")
        void supportedVersion() {
            var jsonApiObject = reader.read("""
                    {
                      "version": "1.1"
                    }""", JsonApiObject.class);

            assertThat(jsonApiObject.getVersion()).isEqualTo("1.1");
        }

        @Test
        @DisplayName("an array of URIs for all applied extensions")
        void appliedExtensions() {
            var jsonApiObject = reader.read("""
                    {
                      "ext" : [
                        "https://jsonapi.org/ext/atomic",
                        "https://jsonapi.org/ext/version"
                      ]
                    }""", JsonApiObject.class);

            assertThat(jsonApiObject.getExtensions()).containsExactlyInAnyOrder(
                    "https://jsonapi.org/ext/atomic", "https://jsonapi.org/ext/version");
        }

        @Test
        @DisplayName("an array of URIs for all applied profiles")
        void appliedProfiles() {
            var jsonApiObject = reader.read("""
                    {
                      "ext" : [
                        "https://example.com/profiles/flexible-pagination",
                        "https://example.com/profiles/resource-versioning"
                      ]
                    }""", JsonApiObject.class);

            assertThat(jsonApiObject.getExtensions()).containsExactlyInAnyOrder(
                    "https://example.com/profiles/flexible-pagination",
                    "https://example.com/profiles/resource-versioning");
        }

        @Test
        @DisplayName("a meta object")
        void metaObject() {
            var jsonApiObject = reader.read("""
                    {
                      "meta": {
                        "info": "JSON:API related meta information"
                      }
                    }""", JsonApiObject.class, pointer -> DummyMetaInformation.class);

            MetaInformation meta = jsonApiObject.getMeta();
            assertThat(meta).isNotNull().isInstanceOf(DummyMetaInformation.class);
            assertThat(((DummyMetaInformation) meta).info).isEqualTo("JSON:API related meta information");
        }
    }
}
