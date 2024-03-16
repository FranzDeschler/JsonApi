package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.meta.MetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-identifier-objects">jsonapi.org</a>
 */
@DisplayName("A resource identifier object")
class ResourceIdentifierDeserializationTest {
    private final TestObjectReader reader = new TestObjectReader();

    @Test
    @DisplayName("must contain a type and id member")
    void typeMember() {
        var resourceIdentifier = reader.read("""
                {
                  "type": "article",
                  "id": "1"
                }""", ResourceIdentifierObject.class);

        assertThat(resourceIdentifier).isEqualTo(new ResourceIdentifierObject("article", "1"));
    }

    @Test
    @DisplayName("may not contain an id if a lid member is present")
    void localId() {
        var resourceIdentifier = reader.read("""
                {
                  "type": "article",
                  "lid": "99"
                }""", ResourceIdentifierObject.class);

        assertThat(resourceIdentifier).isEqualTo(new ResourceIdentifierObject("article", null, "99"));

        resourceIdentifier = reader.read("""
                {
                  "type": "article",
                  "id": "123",
                  "lid": "99"
                }""", ResourceIdentifierObject.class);

        assertThat(resourceIdentifier).isEqualTo(new ResourceIdentifierObject("article", "123", "99"));
    }

    @Test
    @DisplayName("may contain a meta member")
    void metaInfo() {
        var resourceIdentifier = reader.read("""
                {
                  "type": "article",
                  "id": "1",
                  "meta": {
                    "info": "Resource-Identifier meta info"
                  }
                }""", ResourceIdentifierObject.class, pointer -> DummyMetaInformation.class);

        MetaInformation meta = resourceIdentifier.getMeta();
        assertThat(meta).isNotNull().isInstanceOf(DummyMetaInformation.class);
        assertThat(((DummyMetaInformation) meta).info).isEqualTo("Resource-Identifier meta info");
    }
}
