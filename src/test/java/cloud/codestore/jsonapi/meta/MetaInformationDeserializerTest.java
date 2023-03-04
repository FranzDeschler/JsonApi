package cloud.codestore.jsonapi.meta;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.document.JsonApiDocument;
import cloud.codestore.jsonapi.document.JsonApiObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A meta object is deserialized")
class MetaInformationDeserializerTest {

    private MetaDeserializer metaDeserializer;

    @Test
    @DisplayName("based on the concrete type")
    void deserializeBasedOnType() {
        metaDeserializer = pointer -> DummyMetaInformation.class;
        JsonApiObject jsonApiObject = TestObjectReader.read("""
                {
                  "meta" : {
                    "info" : "This is a custom meta info."
                  }
                }""", JsonApiObject.class, metaDeserializer);

        assertThat(jsonApiObject).isNotNull();
        assertMetaEquals(jsonApiObject.getMeta(), "This is a custom meta info.");
    }

    @Test
    @DisplayName("based on its content")
    void deserializeBasedOnContent() {
        metaDeserializer = new MetaDeserializer() {
            @Override
            public Class<? extends MetaInformation> getClass(String pointer) {
                return null;
            }

            @Override
            public MetaInformation deserialize(String pointer, ObjectNode node) {
                if (node.has("info")) {
                    DummyMetaInformation meta = new DummyMetaInformation();
                    meta.info = node.get("info").textValue();
                    return meta;
                }
                return null;
            }
        };

        JsonApiObject jsonApiObject = TestObjectReader.read("""
                {
                  "meta" : {
                    "info" : "This meta object is deserialized dynamically."
                  }
                }""", JsonApiObject.class, metaDeserializer);

        assertThat(jsonApiObject).isNotNull();
        assertMetaEquals(jsonApiObject.getMeta(), "This meta object is deserialized dynamically.");
    }

    @Nested
    @DisplayName("providing the pointer of the meta object")
    class MetaPathTest {
        private String pointer;

        @BeforeEach
        void setUp() {
            metaDeserializer = pointer -> {
                MetaPathTest.this.pointer = pointer;
                return DummyMetaInformation.class;
            };
        }

        @Test
        @DisplayName("of the document")
        void documentMetaInfo() {
            TestObjectReader.read("""
                {
                  "meta" : {
                    "info" : "/meta"
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/meta");
        }

        @Test
        @DisplayName("of the jsonapi object")
        void jsonapiObjectMetaInfo() {
            TestObjectReader.read("""
                {
                  "jsonapi": {
                    "meta" : {
                      "info" : "/jsonapi/meta"
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/jsonapi/meta");
        }

        @Test
        @DisplayName("of a document link")
        void documentLinkMetaInfo() {
            TestObjectReader.read("""
                {
                  "links": {
                    "self": {
                      "href": "http://localhost:8080",
                      "meta" : {
                        "info" : "/links/self/meta"
                      }
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/links/self/meta");
        }

        @Test
        @DisplayName("of a resource object")
        void resourceMetaInfo() {
            TestObjectReader.read("""
                {
                  "data": {
                    "type": "person",
                    "id": "1",
                    "meta" : {
                      "info" : "/data/meta"
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/data/meta");
        }

        @Test
        @DisplayName("of a collection resource object")
        void collectionResourceMetaInfo() {
            TestObjectReader.read("""
                {
                  "data": [{
                    "type": "person",
                    "id": "1",
                    "meta" : {
                      "info" : "/data/0/meta"
                    }
                  }]
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/data/0/meta");
        }

        @Test
        @DisplayName("of an included resource object")
        void includedResourceMetaInfo() {
            TestObjectReader.read("""
                {
                  "included": [{
                    "type": "person",
                    "id": "1",
                    "meta" : {
                      "info" : "/included/0/meta"
                    }
                  }]
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/included/0/meta");
        }

        @Test
        @DisplayName("of a resource link")
        void resourceLinkMetaInfo() {
            TestObjectReader.read("""
                {
                  "data": {
                    "type": "person",
                    "id": "1",
                    "links": {
                      "self": {
                        "href": "http://localhost:8080",
                        "meta" : {
                          "info" : "/data/links/self/meta"
                        }
                      }
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/data/links/self/meta");
        }

        @Test
        @DisplayName("of an included resource link")
        void includedResourceLinkMetaInfo() {
            TestObjectReader.read("""
                {
                  "included": [{
                    "type": "person",
                    "id": "1",
                    "links": {
                      "self": {
                        "href": "http://localhost:8080",
                        "meta" : {
                          "info" : "/included/0/links/self/meta"
                        }
                      }
                    }
                  }]
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/included/0/links/self/meta");
        }

        @Test
        @DisplayName("of a relationship")
        void relationshipMetaInfo() {
            TestObjectReader.read("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "author": {
                        "meta" : {
                          "info" : "/data/relationships/author/meta"
                        }
                      }
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/data/relationships/author/meta");
        }

        @Test
        @DisplayName("of an included resource relationship")
        void includedRelationshipMetaInfo() {
            TestObjectReader.read("""
                {
                  "included": [{
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "author": {
                        "meta" : {
                          "info" : "/included/0/relationships/author/meta"
                        }
                      }
                    }
                  }]
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/included/0/relationships/author/meta");
        }

        @Test
        @DisplayName("of a relationship link")
        void relationshipLinkMetaInfo() {
            TestObjectReader.read("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "author": {
                        "links": {
                          "related": {
                            "href": "http://localhost:8080",
                            "meta" : {
                              "info" : "/data/relationships/author/links/related/meta"
                            }
                          }
                        }
                      }
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/data/relationships/author/links/related/meta");
        }

        @Test
        @DisplayName("of an included relationship link")
        void includedRelationshipLinkMetaInfo() {
            TestObjectReader.read("""
                {
                  "included": [{
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "author": {
                        "links": {
                          "related": {
                            "href": "http://localhost:8080",
                            "meta" : {
                              "info" : "/included/0/relationships/author/links/related/meta"
                            }
                          }
                        }
                      }
                    }
                  }]
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/included/0/relationships/author/links/related/meta");
        }

        @Test
        @DisplayName("of a resource identifier object")
        void resourceIdentifierMetaInfo() {
            TestObjectReader.read("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "author": {
                        "data": {
                          "type": "person",
                          "id": "2",
                          "meta" : {
                            "info" : "/data/relationships/author/data/meta"
                          }
                        }
                      }
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/data/relationships/author/data/meta");
        }

        @Test
        @DisplayName("of a resource identifier object inside a collection")
        void resourceIdentifierListMetaInfo() {
            TestObjectReader.read("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "comments": {
                        "data": [{
                          "type": "comment",
                          "id": "2",
                          "meta" : {
                            "info" : "/data/relationships/comments/data/0/meta"
                          }
                        }]
                      }
                    }
                  }
                }""", JsonApiDocument.class, metaDeserializer);

            assertThat(pointer).isEqualTo("/data/relationships/comments/data/0/meta");
        }
    }

    private void assertMetaEquals(MetaInformation meta, String content) {
        assertThat(meta).isNotNull();
        assertThat(meta).isInstanceOf(DummyMetaInformation.class);
        assertThat(((DummyMetaInformation) meta).info).isEqualTo(content);
    }
}