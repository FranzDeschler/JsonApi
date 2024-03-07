package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-object-related-resource-links">jsonapi.org</a>
 */
@DisplayName("Resource Linkage")
class ResourceLinkageSerializationTest {
    @Nested
    @DisplayName("must be represented as")
    class RequiredMembers {
        @Test
        @DisplayName("null for empty to-one relationships")
        void emptyToOneRelationship() {
            var relationship = new ToOneRelationship<>();
            assertEquals("{}", relationship);
        }

        @Test
        @DisplayName("a single resource identifier object for non-empty to-one relationships")
        void nonEmptyToOneRelationship() {
            var resourceIdentifier = new ResourceIdentifierObject("article", "1");
            var relationship = new ToOneRelationship<>().setData(resourceIdentifier);
            assertEquals("""
                    {
                      "data": {
                        "type": "article",
                        "id": "1"
                      }
                    }""", relationship);
        }

        @Test
        @DisplayName("an empty array for empty to-many relationships")
        void emptyToManyRelationship() {
            var relationship = new ToManyRelationship<>().setData(new ResourceIdentifierObject[0]);
            assertEquals("""
                    {
                      "data": []
                    }""", relationship);
        }

        @Test
        @DisplayName("an array of resource identifier objects for non-empty to-many relationships")
        void nonEmptyToManyRelationship() {
            var id1 = new ResourceIdentifierObject("article", "1");
            var id2 = new ResourceIdentifierObject("article", "2");
            var id3 = new ResourceIdentifierObject("article", "3");
            var relationship = new ToManyRelationship<>().setData(new ResourceIdentifierObject[]{id1, id2, id3});

            assertEquals("""
                    {
                      "data": [
                        {"type": "article", "id": "1"},
                        {"type": "article", "id": "2"},
                        {"type": "article", "id": "3"}
                      ]
                    }""", relationship);
        }
    }
}
