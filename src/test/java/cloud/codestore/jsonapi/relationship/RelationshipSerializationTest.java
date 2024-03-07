package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-object-relationships">jsonapi.org</a>
 */
@DisplayName("A relationship object")
class RelationshipSerializationTest {
    @Nested
    @DisplayName("must contain at least")
    class RequiredMembers {
        @Nested
        @DisplayName("a links object containing at least")
        class RequiredLinks {
            @Test
            @DisplayName("a link for the relationship itself")
            void selfLink() {
                var relationship = new Relationship().setSelfLink("/articles/1/relationships/author");
                assertEquals("""
                        {
                          "links": {
                            "self": "/articles/1/relationships/author"
                          }
                        }""", relationship);
            }

            @Test
            @DisplayName("a related resource link")
            void relatedResourceLink() {
                var relationship = new Relationship("/authors/5");
                assertEquals("""
                        {
                          "links": {
                            "related": "/authors/5"
                          }
                        }""", relationship);
            }

            @Test
            @DisplayName("a member defined by an applied extension")
            void extensionMember() {
                var relationship = new Relationship();
                relationship.getLinks().setExtensionMember("dummy:ext", "test");
                assertEquals("""
                        {
                          "links": {
                            "dummy:ext": "test"
                          }
                        }""", relationship);
            }
        }

        @Test
        @DisplayName("resource linkage")
        void resourceLinkage() {
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
        @DisplayName("a meta object")
        void metaObject() {
            var relationship = new Relationship().setMeta(new DummyMetaInformation());
            assertEquals("""
                    {
                      "meta": {
                        "info": "dummy meta info"
                      }
                    }""", relationship);
        }

        @Test
        @DisplayName("a member defined by an applied extension")
        void extensionMember() {
            var relationship = new Relationship(Map.of("dummy:ext", "test"));
            assertEquals("""
                    {
                      "dummy:ext": "test"
                    }""", relationship);
        }
    }
}
