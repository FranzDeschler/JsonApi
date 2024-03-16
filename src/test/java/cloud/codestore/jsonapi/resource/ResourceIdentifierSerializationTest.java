package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.DummyMetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-identifier-objects">jsonapi.org</a>
 */
@DisplayName("A resource identifier object")
class ResourceIdentifierSerializationTest {
    private static final String TYPE = "testType";
    private static final String ID = "123";

    @Test
    @DisplayName("must contain a type and id member")
    void typeMember() {
        assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, " "))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ResourceIdentifierObject(null, ID))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceIdentifierObject("", ID))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceIdentifierObject(" ", ID))
                .isInstanceOf(IllegalArgumentException.class);

        assertEquals("""
                {
                  "type": "testType",
                  "id": "123"
                }""", new ResourceIdentifierObject(TYPE, ID));
    }

    @Test
    @DisplayName("may not contain an id if a lid member is present")
    void localId() {
        assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, "", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, " ", " "))
                .isInstanceOf(IllegalArgumentException.class);

        assertEquals("""
                {
                  "type": "testType",
                  "lid": "123"
                }""", new ResourceIdentifierObject(TYPE, null, ID));
    }

    @Test
    @DisplayName("may contain a meta member")
    void metaInfo() {
        var resourceIdentifier = new ResourceIdentifierObject(TYPE, ID).setMeta(new DummyMetaInformation());
        assertEquals("""
                {
                  "type": "testType",
                  "id": "123",
                  "meta": {
                    "info": "dummy meta info"
                  }
                }""", resourceIdentifier);
    }
}
