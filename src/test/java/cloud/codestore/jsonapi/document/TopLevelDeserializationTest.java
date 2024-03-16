package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.error.ErrorDocument;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-top-level">jsonapi.org</a>
 */
class TopLevelDeserializationTest {
    private static final TestObjectReader reader = new TestObjectReader(Map.of(
            "article", Article.class,
            "person", Person.class
    ));

    @Nested
    @DisplayName("A JSON:API document")
    class DocumentTest {
        @Nested
        @DisplayName("must contain at least")
        class RequiredMembers {
            @Test
            @DisplayName("the document’s primary data")
            void primaryData() {
                var document = reader.read("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1",
                            "attributes": {
                              "title": "The article's title"
                            }
                          }
                        }""", new TypeReference<SingleResourceDocument<Article>>() {});

                var article = document.getData();
                assertThat(article).isNotNull();
                assertThat(article.getId()).isEqualTo("1");
                assertThat(article.title).isEqualTo("The article's title");
            }

            @Test
            @DisplayName("an array of error objects.")
            void errors() {
                var document = reader.read("""
                        {
                          "errors": [{
                            "code": "1"
                          }, {
                            "code": "2"
                          }]
                        }""", ErrorDocument.class);

                assertThat(document.getErrors()).hasSize(2);
                assertThat(document.getErrors()[0].getCode()).isEqualTo("1");
                assertThat(document.getErrors()[1].getCode()).isEqualTo("2");
            }

            @Test
            @DisplayName("a meta object")
            void metaObject() {
                var document = reader.read("""
                        {
                          "meta": {
                            "info": "Top-level meta information"
                          }
                        }""", JsonApiDocument.class, pointer -> DummyMetaInformation.class);

                assertThat(document).isNotNull();
                assertThat(document.getMeta()).isNotNull().isInstanceOf(DummyMetaInformation.class);
                assertThat(((DummyMetaInformation) document.getMeta()).info).isEqualTo("Top-level meta information");
            }

            @Test
            @DisplayName("a member defined by an applied extension")
            void extensionMember() {
                var document = reader.read("""
                        {
                          "version:id": "42"
                        }""", JsonApiDocument.class);

                assertThat(document.getExtensionMember("version:id")).isEqualTo("42");
            }
        }

        @Nested
        @DisplayName("may contain")
        class OptionalMembers {
            @Test
            @DisplayName("an object describing the server’s implementation")
            void jsonapiObject() {
                var document = reader.read("""
                        {
                          "jsonapi": {
                            "version": "1.1"
                          }
                        }""", JsonApiDocument.class);

                assertThat(document.getJsonApiObject()).isNotNull();
                assertThat(document.getJsonApiObject().getVersion()).isEqualTo("1.1");
            }

            @Test
            @DisplayName("a links object related to the primary data")
            void linksObject() {
                var document = reader.read("""
                        {
                          "links": {
                            "self": "https://codestore.cloud"
                          }
                        }""", JsonApiDocument.class);

                assertThat(document.getSelfLink()).isNotNull();
                assertThat(document.getSelfLink().getHref()).isEqualTo("https://codestore.cloud");
            }

            @Test
            @DisplayName("an array of included resource objects")
            void includedResources() {
                var document = reader.read("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1",
                            "relationships": {
                              "author": {
                                "data": {"type":"person", "id":"5"}
                              }
                            }
                          },
                          "included": [{"type": "person", "id": "5"}]
                        }""", new TypeReference<SingleResourceDocument<Article>>() {});

                assertThat(document.getData()).isNotNull();
                var relationship = document.getData().author;
                assertThat(relationship).isNotNull();
                assertThat(relationship.getData()).isNotNull();
                assertThat(relationship.getData()).isEqualTo(new ResourceIdentifierObject("person", "5"));
                assertThat(relationship.getRelatedResource()).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("the top-level links object may contain")
    class TopLevelLinks {
        @Test
        @DisplayName("the link that generated the current response document")
        void selfLink() {
            var document = reader.read("""
                    {
                      "links": {
                        "self": "/articles/1"
                      }
                    }""", JsonApiDocument.class);

            Link link = document.getSelfLink();
            assertThat(link).isNotNull();
            assertThat(link.getHref()).isEqualTo("/articles/1");
        }

        @Test
        @DisplayName("a related resource link")
        void relatedLink() {
            var document = reader.read("""
                    {
                      "links": {
                        "related": "/articles/1"
                      }
                    }""", JsonApiDocument.class);

            Link link = document.getRelatedLink();
            assertThat(link).isNotNull();
            assertThat(link.getHref()).isEqualTo("/articles/1");
        }

        @Test
        @DisplayName("a link to a description document")
        void describedbyLink() {
            var document = reader.read("""
                    {
                      "links": {
                        "describedby": "https://codestore.cloud"
                      }
                    }""", JsonApiDocument.class);

            Link link = document.getDescribedbyLink();
            assertThat(link).isNotNull();
            assertThat(link.getHref()).isEqualTo("https://codestore.cloud");
        }

        @Test
        @DisplayName("pagination links for the primary data")
        void paginationLinks() {
            var document = reader.read("""
                    {
                      "links": {
                        "first": "/articles?page[number]=1",
                        "prev": "/articles?page[number]=5",
                        "next": "/articles?page[number]=7",
                        "last": "/articles?page[number]=12"
                      }
                    }""", JsonApiDocument.class);

            assertThat(document.getFirstPageLink().getHref()).isEqualTo("/articles?page[number]=1");
            assertThat(document.getPreviousPageLink().getHref()).isEqualTo("/articles?page[number]=5");
            assertThat(document.getNextPageLink().getHref()).isEqualTo("/articles?page[number]=7");
            assertThat(document.getLastPageLink().getHref()).isEqualTo("/articles?page[number]=12");
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
                var document = reader.read("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1",
                            "attributes": {
                              "title": "The article's title"
                            }
                          }
                        }""", new TypeReference<SingleResourceDocument<Article>>() {});

                Article article = document.getData();
                assertThat(article).isNotNull();
                assertThat(article.getId()).isEqualTo("1");
                assertThat(article.title).isEqualTo("The article's title");
            }

            /**
             * Currently, setting {@link ResourceIdentifierObject}s as primary data is not supported.
             * Instead, use {@link ResourceObject}s that only contain type and id.
             */
            @Test
            @DisplayName("a single resource identifier object")
            void resourceIdentifierObject() {
                var document = reader.read("""
                        {
                          "data": {
                            "type": "article",
                            "id": "1"
                          }
                        }""", new TypeReference<SingleResourceDocument<Article>>() {});


                Article article = document.getData();
                assertThat(article).isNotNull();
                assertThat(article.getId()).isEqualTo("1");
                assertThat(article.title).isNull();
            }

            @Test
            @DisplayName("null")
            void noPrimaryData() {
                var document = reader.read("""
                        {
                          "data": null
                        }""", new TypeReference<SingleResourceDocument<Article>>() {});

                assertThat(document.getData()).isNull();
            }
        }

        @Nested
        @DisplayName("for requests that target resource collections")
        class ResourceCollection {
            @Test
            @DisplayName("an array of resource objects")
            void resourceObjectArray() {
                var document = reader.read("""
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
                        }""", new TypeReference<ResourceCollectionDocument<Article>>() {});

                var articles = document.getData();
                assertThat(articles).isNotNull().hasSize(3);

                assertThat(articles[0].getId()).isEqualTo("1");
                assertThat(articles[0].title).isEqualTo("The article's title");
                assertThat(articles[1].getId()).isEqualTo("2");
                assertThat(articles[1].title).isEqualTo("A second article");
                assertThat(articles[2].getId()).isEqualTo("3");
                assertThat(articles[2].title).isEqualTo("Yet another article");
            }

            /**
             * Currently, setting {@link ResourceIdentifierObject}s as primary data is not supported.
             * Instead, use {@link ResourceObject}s that only contain type and id.
             */
            @Test
            @DisplayName("an array of resource identifier objects")
            void resourceIdentifierArray() {
                var document = reader.read("""
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
                        }""", new TypeReference<ResourceCollectionDocument<Article>>() {});

                var articles = document.getData();
                assertThat(articles).isNotNull().hasSize(3);

                assertThat(articles[0].getId()).isEqualTo("1");
                assertThat(articles[1].getId()).isEqualTo("2");
                assertThat(articles[2].getId()).isEqualTo("3");
            }

            @Test
            @DisplayName("an empty array")
            void emptyArray() {
                var document = reader.read("""
                        {
                          "data": []
                        }""", new TypeReference<ResourceCollectionDocument<Article>>() {});


                assertThat(document.getData()).isNotNull().isEmpty();
            }
        }
    }

    private static class Article extends ResourceObject {
        String title;
        ToOneRelationship<Person> author;

        @JsonCreator
        Article(
                @JsonProperty("title") String title,
                @JsonProperty("author") ToOneRelationship<Person> author
        ) {
            super("article");
            this.title = title;
            this.author = author;
        }
    }

    private static class Person extends ResourceObject {
        @JsonCreator
        Person() {
            super("person");
        }
    }
}
