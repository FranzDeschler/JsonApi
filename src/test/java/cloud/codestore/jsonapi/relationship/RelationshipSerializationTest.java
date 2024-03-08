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

    @Test
    @DisplayName("that represents a to-many relationship may contain pagination links")
    void optionalPaginationLinks() {
        var relationship = new ToManyRelationship<>().setFirstPageLink("/comments?page[number]=1")
                                                     .setPreviousPageLink("/comments?page[number]=5")
                                                     .setNextPageLink("/comments?page[number]=7")
                                                     .setLastPageLink("/comments?page[number]=12");

        assertEquals("""
                {
                  "links": {
                    "first": "/comments?page[number]=1",
                    "prev": "/comments?page[number]=5",
                    "next": "/comments?page[number]=7",
                    "last": "/comments?page[number]=12"
                  }
                }""", relationship);
    }

    @Test
    @DisplayName("may be to-one")
    void toOneRelationship() {
        var relationship = new ToOneRelationship<>().setData(new ResourceIdentifierObject("article", "1"));
        assertEquals("""
                {
                  "data": {"type": "article", "id": "1"}
                }""", relationship);
    }

    @Test
    @DisplayName("may be to-many")
    void toManyRelationship() {
        var relationship = new ToManyRelationship<>().setData(
                new ResourceIdentifierObject("comment", "1"),
                new ResourceIdentifierObject("comment", "2"),
                new ResourceIdentifierObject("comment", "3"),
                new ResourceIdentifierObject("comment", "4")
        );

        assertEquals("""
                {
                  "data": [
                    {"type": "comment", "id": "1"},
                    {"type": "comment", "id": "2"},
                    {"type": "comment", "id": "3"},
                    {"type": "comment", "id": "4"}
                  ]
                }""", relationship);
    }
}
