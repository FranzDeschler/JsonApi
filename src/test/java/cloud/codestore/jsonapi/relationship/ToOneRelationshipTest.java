package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.*;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A to-one relationship")
class ToOneRelationshipTest {
    private ToOneRelationship<ResourceObject> relationship = new ToOneRelationship<>();

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
        ResourceObject relatedResource = new ResourceObject("test", "123") {};
        relationship.setRelatedResource(relatedResource);
        assertThat(relationship.isIncluded()).isTrue();
        assertThat(relationship.getRelatedResource()).isSameAs(relatedResource);
        assertThat(relationship.getData()).isEqualTo(new ResourceIdentifierObject("test", "123"));
    }

    @Test
    @DisplayName("must have a resource identifier when a related resource is set")
    void setRelatedResource() {
        ResourceObject relatedResource = new ResourceObject("test", "123") {};
        relationship.setRelatedResource(relatedResource);
        assertThatThrownBy(() -> relationship.setData(null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("removes the resource identifier when the resource is removed")
    void resetRelatedResource() {
        ResourceObject relatedResource = new ResourceObject("test", "123") {};
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
        relationship.setData(new ResourceIdentifierObject("snippet", "12345"));
        String json = TestObjectWriter.write(relationship);
        assertThat(json).isEqualTo("""
                {
                  "data" : {
                    "type" : "snippet",
                    "id" : "12345"
                  }
                }""");
    }

    @Nested
    @DisplayName("can be deserialized from a JSON object")
    class DeserializeTest {

        @Test
        @DisplayName("containing a to-one relationship")
        void withToOneRelationships() {
            SingleResourceDocument<Article> document = TestObjectReader.read("""
                    {
                      "data": {
                        "type": "article",
                        "id": "1",
                        "relationships": {
                          "author": {
                            "data": {"type":"person", "id":"123"}
                          }
                        }
                      }
                    }""", new TypeReference<>() {});

            Article article = document.getData();
            assertThat(article.author).isNotNull();
            assertThat(article.author.getData()).isEqualTo(new ResourceIdentifierObject("person", "123"));
            assertThat(document.getRelationshipBacklinks()).contains(article.author);
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
                      "author": {
                        "data": {
                          "type": "person",
                          "id": "9"
                        }
                      }
                    }
                  },
                  "included": [{
                    "type": "person",
                    "id": "9",
                    "attributes": {
                      "firstName": "John",
                      "lastName": "Doe"
                    }
                  }]
                }""", new TypeReference<>() {});

            Article article = document.getData();
            assertThat(article).isNotNull();

            ToOneRelationship<Person> relationship = article.author;
            assertThat(relationship.isIncluded()).isTrue();
            Person author = relationship.getRelatedResource();
            assertThat(author).isNotNull();
            assertThat(author.firstName).isEqualTo("John");
            assertThat(author.lastName).isEqualTo("Doe");

            assertThat(document.getRelationshipBacklinks()).contains(article.author);
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
                                "data": {"type":"address", "id":"54321"}
                              },
                              "emptyRelationship": {
                                "data": null
                              }
                            }
                          }
                        }""", new TypeReference<>() {});

            var resource = document.getData();
            assertThat(resource.relationshipWithData).isNotNull();
            assertThat(resource.relationshipWithData).isInstanceOf(ToOneRelationship.class);
            assertThat(((ToOneRelationship<ResourceObject>) resource.relationshipWithData).getData()).isEqualTo(new ResourceIdentifierObject("address", "54321"));

            assertThat(resource.emptyRelationship).isNotNull();
            assertThat(resource.emptyRelationship).isInstanceOf(ToOneRelationship.class);
            assertThat(((ToOneRelationship<ResourceObject>) resource.emptyRelationship).getData()).isNull();

            assertThat(document.getRelationshipBacklinks()).contains(resource.relationshipWithData, resource.emptyRelationship);
        }

        private static class DynamicRelationshipTestResource extends ResourceObject {
            public Relationship relationshipWithData;
            public Relationship emptyRelationship;

            public DynamicRelationshipTestResource() {
                super("test");
            }
        }
    }
}