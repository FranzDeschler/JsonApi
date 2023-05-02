package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.*;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A to-many relationship")
class ToManyRelationshipTest {
    private ToManyRelationship<ResourceObject> relationship = new ToManyRelationship<>();

    @Test
    @DisplayName("is empty after creation")
    void isEmpty() {
        assertThat(relationship.getLinks()).isNotNull();
        assertThat(relationship.getLinks().isEmpty()).isTrue();
        assertThat(relationship.getData()).isNull();
        assertThat(relationship.getRelatedResource()).isNull();
        assertThat(relationship.isIncluded()).isFalse();
    }

    @Test
    @DisplayName("is included after setting a related resource")
    void include() {
        assertThat(relationship.isIncluded()).isFalse();
        ResourceObject[] relatedResources = {
                new ResourceObject("test", "1") {},
                new ResourceObject("test", "2") {},
                new ResourceObject("test", "3") {}
        };
        relationship.setRelatedResource(relatedResources);
        assertThat(relationship.isIncluded()).isTrue();
        assertThat(relationship.getRelatedResource()).isSameAs(relatedResources);
        assertThat(relationship.getData()).containsExactlyInAnyOrder(
                new ResourceIdentifierObject("test", "1"),
                new ResourceIdentifierObject("test", "2"),
                new ResourceIdentifierObject("test", "3")
        );
    }

    @Test
    @DisplayName("must have a resource identifier when a related resource is set")
    void setRelatedResource() {
        ResourceObject[] relatedResource = {new ResourceObject("test", "123") {}};
        relationship.setRelatedResource(relatedResource);
        assertThatThrownBy(() -> relationship.setData(null)).isInstanceOf(IllegalStateException.class)
                                                            .hasMessage("Relationships that contain related resources must contain a resource identifier objects to provide resource linkage.");
    }

    @Test
    @DisplayName("removes the resource identifier when the resource is removed")
    void resetRelatedResource() {
        ResourceObject[] relatedResource = {new ResourceObject("test", "123") {}};
        relationship.setRelatedResource(relatedResource);
        assertThat(relationship.getRelatedResource()).isNotNull();
        assertThat(relationship.getData()).isNotNull();

        relationship.setRelatedResource(null);

        assertThat(relationship.getRelatedResource()).isNull();
        assertThat(relationship.getData()).isNull();
    }

    @Test
    @DisplayName("can contain a single resource identifier object")
    void containsResourceIdentifier() {
        relationship.setData(new ResourceIdentifierObject[]{
                new ResourceIdentifierObject("snippet", "12345"),
                new ResourceIdentifierObject("snippet", "54321"),
                new ResourceIdentifierObject("snippet", "32154")
        });
        String json = TestObjectWriter.write(relationship);
        Assertions.assertThat(json).isEqualTo("""
                {
                  "data" : [ {
                    "type" : "snippet",
                    "id" : "12345"
                  }, {
                    "type" : "snippet",
                    "id" : "54321"
                  }, {
                    "type" : "snippet",
                    "id" : "32154"
                  } ]
                }""");
    }

    @Nested
    @DisplayName("can be deserialized from a JSON object")
    class DeserializeTest {

        @Test
        @DisplayName("containing a to-many relationship")
        void withToManyRelationships() {
            SingleResourceDocument<Article> document = TestObjectReader.read("""
                    {
                      "data": {
                        "type": "article",
                        "id": "1",
                        "relationships": {
                          "comments": {
                            "data": [
                              {"type":"comment", "id":"1"},
                              {"type":"comment", "id":"2"},
                              {"type":"comment", "id":"3"}
                            ]
                          }
                        }
                      }
                    }""", new TypeReference<>() {});

            Article article = document.getData();
            assertThat(article.comments).isNotNull();
            assertThat(article.comments.getData()).containsExactlyInAnyOrder(
                    new ResourceIdentifierObject("comment", "1"),
                    new ResourceIdentifierObject("comment", "2"),
                    new ResourceIdentifierObject("comment", "3")
            );

            assertThat(document.getRelationshipBacklinks()).contains(article.comments);
        }

        @Test
        @DisplayName("containing included resource object")
        void containsIncludedResource() {
            SingleResourceDocument<Article> document = TestObjectReader.read("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "comments": {
                        "data": [{
                          "type": "comment",
                          "id": "5"
                        }, {
                          "type": "comment",
                          "id": "12"
                        }]
                      }
                    }
                  },
                  "included": [{
                    "type": "comment",
                    "id": "5",
                    "attributes": {
                      "body": "First!"
                    }
                  }, {
                    "type": "comment",
                    "id": "12",
                    "attributes": {
                      "body": "I like XML better"
                    }
                  }]
                }""", new TypeReference<>() {});

            Article article = document.getData();
            assertThat(article).isNotNull();

            ToManyRelationship<Comment> relationship = article.comments;
            assertThat(relationship.isIncluded()).isTrue();

            Comment[] comments = relationship.getRelatedResource();
            assertThat(comments).hasSize(2);
            assertThat(comments[0]).isInstanceOf(Comment.class);
            assertThat(comments[1]).isInstanceOf(Comment.class);

            assertThat(document.getRelationshipBacklinks()).contains(article.comments);
        }

        @Test
        @DisplayName("based on the 'data' object")
        void dynamicDeserialization() throws JsonProcessingException {
            var objectMapper = new JsonApiObjectMapper().registerResourceType("test", DynamicRelationshipTestResource.class);

            SingleResourceDocument<DynamicRelationshipTestResource> document = objectMapper.readValue("""
                        {
                          "data": {
                            "type": "test",
                            "id": "123",
                            "relationships": {
                              "relationshipWithData": {
                                "data": [
                                  {"type":"address", "id":"1"},
                                  {"type":"address", "id":"2"},
                                  {"type":"address", "id":"3"}
                                ]
                              },
                              "emptyRelationship": {
                                "data": []
                              }
                            }
                          }
                        }""", new TypeReference<>() {});

            var resource = document.getData();
            assertThat(resource.relationshipWithData).isNotNull();
            assertThat(resource.relationshipWithData).isInstanceOf(ToManyRelationship.class);
            assertThat(((ToManyRelationship<ResourceObject>) resource.relationshipWithData).getData()).hasSize(3);

            assertThat(resource.emptyRelationship).isNotNull();
            assertThat(resource.emptyRelationship).isInstanceOf(ToManyRelationship.class);
            assertThat(((ToManyRelationship<ResourceObject>) resource.emptyRelationship).getData()).isEmpty();

            assertThat(document.getRelationshipBacklinks()).contains(resource.relationshipWithData, resource.emptyRelationship);
        }

        private static class DynamicRelationshipTestResource extends ResourceObject {
            public Relationship<ResourceObject> relationshipWithData;
            public Relationship<ResourceObject> emptyRelationship;

            public DynamicRelationshipTestResource() {
                super("test");
            }
        }
    }
}