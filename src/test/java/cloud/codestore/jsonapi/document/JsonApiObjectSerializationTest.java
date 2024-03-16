package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.DummyMetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-jsonapi-object">jsonapi.org</a>
 */
@DisplayName("A JSON:API object")
class JsonApiObjectSerializationTest {
    private JsonApiObject jsonApiObject = new JsonApiObject();

    @Nested
    @DisplayName("may contain")
    class OptionalMembers {
        @Test
        @DisplayName("the highest JSON:API version supported")
        void supportedVersion() {
            assertEquals("""
                    {
                      "version": "1.1"
                    }""", jsonApiObject);
        }

        @Test
        @DisplayName("an array of URIs for all applied extensions")
        void appliedExtensions() {
            jsonApiObject.setExtensions("https://jsonapi.org/ext/atomic", "https://jsonapi.org/ext/version");
            assertEquals("""
                    {
                      "version": "1.1",
                      "ext" : [
                        "https://jsonapi.org/ext/atomic",
                        "https://jsonapi.org/ext/version"
                      ]
                    }""", jsonApiObject);
        }

        @Test
        @DisplayName("an array of URIs for all applied profiles")
        void appliedProfiles() {
            jsonApiObject.setProfiles(
                    "https://example.com/profiles/flexible-pagination",
                    "https://example.com/profiles/resource-versioning"
            );

            assertEquals("""
                    {
                      "version": "1.1",
                      "profile" : [
                        "https://example.com/profiles/flexible-pagination",
                        "https://example.com/profiles/resource-versioning"
                      ]
                    }""", jsonApiObject);
        }

        @Test
        @DisplayName("a meta object")
        void metaObject() {
            jsonApiObject.setMeta(new DummyMetaInformation());
            assertEquals("""
                    {
                      "version": "1.1",
                      "meta": {
                        "info": "dummy meta info"
                      }
                    }""", jsonApiObject);
        }
    }
}
