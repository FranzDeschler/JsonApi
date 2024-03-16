package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-object-relationships">jsonapi.org</a>
 */
@DisplayName("A relationship object")
class RelationshipDeserializationTest {
    private final TestObjectReader reader = new TestObjectReader();

    @Nested
    @DisplayName("must contain at least")
    class RequiredMembers {
        @Nested
        @DisplayName("a links object containing at least")
        class RequiredLinks {
            @Test
            @DisplayName("a link for the relationship itself")
            void selfLink() {
                var relationship = reader.read("""
                        {
                          "links": {
                            "self": "/articles/1/relationships/author"
                          }
                        }""", Relationship.class);

                Link link = relationship.getSelfLink();
                assertThat(link).isNotNull();
                assertThat(link.getHref()).isEqualTo("/articles/1/relationships/author");
            }

            @Test
            @DisplayName("a related resource link")
            void relatedResourceLink() {
                var relationship = reader.read("""
                        {
                          "links": {
                            "related": "/authors/5"
                          }
                        }""", Relationship.class);

                Link link = relationship.getRelatedResourceLink();
                assertThat(link).isNotNull();
                assertThat(link.getHref()).isEqualTo("/authors/5");
            }

            @Test
            @DisplayName("a member defined by an applied extension")
            void extensionMember() {
                var relationship = reader.read("""
                        {
                          "links": {
                            "dummy:ext": "test"
                          }
                        }""", Relationship.class);

                assertThat(relationship.getLinks().getExtensionMember("dummy:ext")).isEqualTo("test");
            }
        }

        @Test
        @DisplayName("resource linkage")
        void resourceLinkage() {
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
        @DisplayName("a meta object")
        void metaObject() {
            var relationship = reader.read("""
                    {
                      "meta": {
                        "info": "relationship meta info"
                      }
                    }""", Relationship.class, pointer -> DummyMetaInformation.class);

            MetaInformation meta = relationship.getMeta();
            assertThat(meta).isNotNull().isInstanceOf(DummyMetaInformation.class);
            assertThat(((DummyMetaInformation) meta).info).isEqualTo("relationship meta info");
        }

        @Test
        @DisplayName("a member defined by an applied extension")
        void extensionMember() {
            var relationship = reader.read("""
                    {
                      "dummy:ext": "test"
                    }""", Relationship.class);

            assertThat(relationship.getExtensionMember("dummy:ext")).isEqualTo("test");
        }
    }

    @Test
    @DisplayName("that represents a to-many relationship may contain pagination links")
    void optionalPaginationLinks() {
        var relationship = reader.read("""
                    {
                      "links": {
                        "first": "/comments?page[number]=1",
                        "prev": "/comments?page[number]=5",
                        "next": "/comments?page[number]=7",
                        "last": "/comments?page[number]=12"
                      }
                    }""", ToManyRelationship.class);

        assertThat(relationship.getFirstPageLink().getHref()).isEqualTo("/comments?page[number]=1");
        assertThat(relationship.getPreviousPageLink().getHref()).isEqualTo("/comments?page[number]=5");
        assertThat(relationship.getNextPageLink().getHref()).isEqualTo("/comments?page[number]=7");
        assertThat(relationship.getLastPageLink().getHref()).isEqualTo("/comments?page[number]=12");
    }

    @Test
    @DisplayName("may be to-one")
    void toOneRelationship() {
        var relationship = reader.read("""
                {
                  "data": {"type": "article", "id": "1"}
                }""", ToOneRelationship.class);

        assertThat(relationship.getData()).isNotNull();
        assertThat(relationship.getData()).isEqualTo(new ResourceIdentifierObject("article", "1"));
    }

    @Test
    @DisplayName("may be to-many")
    void toManyRelationship() {
        var relationship = reader.read("""
                {
                  "data": [
                    {"type": "comment", "id": "1"},
                    {"type": "comment", "id": "2"},
                    {"type": "comment", "id": "3"},
                    {"type": "comment", "id": "4"}
                  ]
                }""", ToManyRelationship.class);

        assertThat(relationship.getData()).isNotNull();
        assertThat(relationship.getData()).containsExactly(
                new ResourceIdentifierObject("comment", "1"),
                new ResourceIdentifierObject("comment", "2"),
                new ResourceIdentifierObject("comment", "3"),
                new ResourceIdentifierObject("comment", "4")
        );
    }
}
