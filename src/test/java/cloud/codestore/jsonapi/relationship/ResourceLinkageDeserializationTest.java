package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-object-related-resource-links">jsonapi.org</a>
 */
@DisplayName("Resource Linkage")
class ResourceLinkageDeserializationTest {
    private final TestObjectReader reader = new TestObjectReader();

    @Nested
    @DisplayName("must be represented as")
    class RequiredMembers {
        @Test
        @DisplayName("null for empty to-one relationships")
        void emptyToOneRelationship() {
            var relationship = reader.read("""
                    {
                      "data": null
                    }""", ToOneRelationship.class);

            assertThat(relationship.getData()).isNull();
        }

        @Test
        @DisplayName("a single resource identifier object for non-empty to-one relationships")
        void nonEmptyToOneRelationship() {
            var relationship = reader.read("""
                    {
                      "data": {
                        "type": "article",
                        "id": "1"
                      }
                    }""", ToOneRelationship.class);

            assertThat(relationship.getData()).isEqualTo(new ResourceIdentifierObject("article", "1"));
        }

        @Test
        @DisplayName("an empty array for empty to-many relationships")
        void emptyToManyRelationship() {
            var relationship = reader.read("""
                    {
                      "data": []
                    }""", ToManyRelationship.class);

            assertThat(relationship.getData()).isEmpty();
        }

        @Test
        @DisplayName("an array of resource identifier objects for non-empty to-many relationships")
        void nonEmptyToManyRelationship() {
            var relationship = reader.read("""
                    {
                      "data": [
                        {"type": "article", "id": "1"},
                        {"type": "article", "id": "2"},
                        {"type": "article", "id": "3"}
                      ]
                    }""", ToManyRelationship.class);

            assertThat(relationship.getData()).containsExactly(
                    new ResourceIdentifierObject("article", "1"),
                    new ResourceIdentifierObject("article", "2"),
                    new ResourceIdentifierObject("article", "3")
            );
        }
    }
}
