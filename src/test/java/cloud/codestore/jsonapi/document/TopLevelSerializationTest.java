package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.error.ErrorDocument;
import cloud.codestore.jsonapi.error.ErrorObject;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-top-level">jsonapi.org</a>
 */
class TopLevelSerializationTest {
    private final JsonApiDocument document = new JsonApiDocument() {};

    @Nested
    @DisplayName("A JSON:API document")
    class DocumentTest {
        @Test
        @DisplayName("must not be empty")
        void notEmpty() {
            assertThatThrownBy(() -> new SingleResourceDocument<>((ResourceObject) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new SingleResourceDocument<>((MetaInformation) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new SingleResourceDocument<>((Map<String, Object>) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new SingleResourceDocument<>(Collections.emptyMap()))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> new ResourceCollectionDocument<>((ResourceObject[]) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new ResourceCollectionDocument<>((MetaInformation) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new ResourceCollectionDocument<>((Map<String, Object>) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new ResourceCollectionDocument<>(Collections.emptyMap()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Nested
        @DisplayName("must contain at least")
        class RequiredMembers {
            @Test
            @DisplayName("the document’s primary data")
            void primaryData() {
                var article = new Article("1", "The article's title");
                var document = new SingleResourceDocument<>(article);
                assertEquals("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1",
                            "attributes": {
                              "title": "The article's title"
                            }
                          }
                        }""", document);
            }

            @Test
            @DisplayName("an array of error objects.")
            void errors() {
                var errors = new ErrorDocument(
                        new ErrorObject().setCode("1"),
                        new ErrorObject().setCode("2")
                );

                assertEquals("""
                        {
                          "errors": [{
                            "code": "1"
                          }, {
                            "code": "2"
                          }]
                        }""", errors);
            }

            @Test
            @DisplayName("a meta object")
            void metaObject() {
                document.setMeta(new DummyMetaInformation());
                assertEquals("""
                        {
                          "meta": {
                            "info": "dummy meta info"
                          }
                        }""", document);
            }

            @Test
            @DisplayName("a member defined by an applied extension")
            void extensionMember() {
                document.setExtensionMember("version:id", "42");
                assertEquals("""
                        {
                          "version:id": "42"
                        }""", document);
            }
        }

        @Nested
        @DisplayName("may contain")
        class OptionalMembers {
            @Test
            @DisplayName("an object describing the server’s implementation")
            void jsonapiObject() {
                document.setJsonapiObject(new JsonApiObject());
                assertEquals("""
                        {
                          "jsonapi": {
                            "version": "1.1"
                          }
                        }""", document);
            }

            @Test
            @DisplayName("a links object related to the primary data")
            void linksObject() {
                document.addLink(Link.SELF, new Link("https://codestore.cloud"));
                assertEquals("""
                        {
                          "links": {
                            "self": "https://codestore.cloud"
                          }
                        }""", document);
            }

            @Test
            @DisplayName("an array of included resource objects")
            void includedResources() {
                var article = new Article("1", new Person("5"));
                var document = new SingleResourceDocument<>(article);
                assertEquals("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1",
                            "relationships": {
                              "author": {
                                "data":{
                                  "type":"person",
                                  "id":"5"
                                }
                              }
                            }
                          },
                          "included": [{
                            "type": "person",
                            "id": "5"
                          }]
                        }""", document);
            }
        }
    }

    @Nested
    @DisplayName("the top-level links object may contain")
    class TopLevelLinks {
        @Test
        @DisplayName("the link that generated the current response document")
        void selfLink() {
            document.setSelfLink("/articles/1");
            assertEquals("""
                    {
                      "links": {
                        "self": "/articles/1"
                      }
                    }""", document);
        }

        @Test
        @DisplayName("a related resource link")
        void relatedLink() {
            document.setRelatedLink("/articles/1");
            assertEquals("""
                    {
                      "links": {
                        "related": "/articles/1"
                      }
                    }""", document);
        }

        @Test
        @DisplayName("a link to a description document")
        void describedbyLink() {
            document.setDescribedbyLink("https://codestore.cloud");
            assertEquals("""
                    {
                      "links": {
                        "describedby": "https://codestore.cloud"
                      }
                    }""", document);
        }

        @Test
        @DisplayName("pagination links for the primary data")
        void paginationLinks() {
            document.setFirstPageLink("/articles?page[number]=1")
                    .setPreviousPageLink("/articles?page[number]=5")
                    .setNextPageLink("/articles?page[number]=7")
                    .setLastPageLink("/articles?page[number]=12");

            assertEquals("""
                    {
                      "links": {
                        "first": "/articles?page[number]=1",
                        "prev": "/articles?page[number]=5",
                        "next": "/articles?page[number]=7",
                        "last": "/articles?page[number]=12"
                      }
                    }""", document);
        }
    }

    @Nested
    @DisplayName("the primary data must be either")
    class PrimaryData {
        @Nested
        @DisplayName("for requests that target single resources")
        class SingleResource {
            @Test
            @DisplayName("a single resource object")
            void singleResourceObject() {
                var article = new Article("1", "The article's title");
                var document = new SingleResourceDocument<>(article);
                assertEquals("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1",
                            "attributes": {
                              "title": "The article's title"
                            }
                          }
                        }""", document);
            }

            /**
             * Currently, setting {@link ResourceIdentifierObject}s as primary data is not supported.
             * Instead, use {@link ResourceObject}s that only contain type and id.
             */
            @Test
            @DisplayName("a single resource identifier object")
            void resourceIdentifierObject() {
                var article = new Article("1");
                var document = new SingleResourceDocument<>(article);
                assertEquals("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1"
                          }
                        }""", document);
            }

            @Test
            @DisplayName("null")
            void noPrimaryData() {
                var document = new SingleResourceDocument<>(new DummyMetaInformation());
                assertThat(TestObjectWriter.write(document)).doesNotContain("\"data\" : {");
            }
        }

        @Nested
        @DisplayName("for requests that target resource collections")
        class ResourceCollection {
            @Test
            @DisplayName("an array of resource objects")
            void resourceObjectArray() {
                var article1 = new Article("1", "The article's title");
                var article2 = new Article("2", "A second article");
                var article3 = new Article("3", "Yet another article");
                var document = new ResourceCollectionDocument<>(new Article[]{article1, article2, article3});
                assertEquals("""
                        {
                          "data": [{
                            "type": "article",
                            "id": "1",
                            "attributes": {
                              "title": "The article's title"
                            }
                          }, {
                            "type": "article",
                            "id": "2",
                            "attributes": {
                              "title": "A second article"
                            }
                          }, {
                            "type": "article",
                            "id": "3",
                            "attributes": {
                              "title": "Yet another article"
                            }
                          }]
                        }""", document);
            }

            /**
             * Currently, setting {@link ResourceIdentifierObject}s as primary data is not supported.
             * Instead, use {@link ResourceObject}s that only contain type and id.
             */
            @Test
            @DisplayName("an array of resource identifier objects")
            void resourceIdentifierArray() {
                var article1 = new Article("1");
                var article2 = new Article("2");
                var article3 = new Article("3");
                var document = new ResourceCollectionDocument<>(new Article[]{article1, article2, article3});
                assertEquals("""
                        {
                          "data": [{
                            "type": "article",
                            "id": "1"
                          }, {
                            "type": "article",
                            "id": "2"
                          }, {
                            "type": "article",
                            "id": "3"
                          }]
                        }""", document);
            }

            @Test
            @DisplayName("an empty array")
            void emptyArray() {
                var document = new ResourceCollectionDocument<>(new Article[0]);
                assertEquals("""
                        {
                          "data": []
                        }""", document);
            }
        }
    }

    private static class Article extends ResourceObject {
        @JsonProperty("title")
        public String title;
        @JsonProperty("author")
        public ToOneRelationship<Person> author;

        Article(String id) {
            super("article", id);
        }

        Article(String id, String title) {
            this(id);
            this.title = title;
        }

        Article(String id, Person author) {
            this(id);
            this.author = ResourceObject.asRelationship(author);
        }
    }

    private static class Person extends ResourceObject {
        Person(String id) {
            super("person", id);
        }
    }
}
