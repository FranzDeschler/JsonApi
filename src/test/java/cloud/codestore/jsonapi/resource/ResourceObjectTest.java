package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.*;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A resource object")
public class ResourceObjectTest {
    private static final String TYPE = "testType";
    private static final String ID = "testId";

    @Test
    @DisplayName("must have a type")
    void mustHaveType() {
        assertThatThrownBy(() -> new ResourceObject(null, ID) {})
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ResourceObject("", ID) {})
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ResourceObject(" ", ID) {})
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("must have an id")
    void mustHaveId() {
        assertThatThrownBy(() -> new ResourceObject(TYPE, null) {})
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ResourceObject(TYPE, "") {})
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ResourceObject(TYPE, " ") {})
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("must not have an \"attributes\" object if all fields are null")
    void emptyAttributeValues() {
        ResourceObject resourceObject = new ResourceObject(TYPE, ID) {
            public String attribute1;
            public String attribute2;
            public String attribute3;
        };

        String json = TestObjectWriter.write(resourceObject.asDocument());
        assertThat(json).isEqualTo("""
                {
                  "data" : {
                    "type" : "testType",
                    "id" : "testId"
                  }
                }""");
    }

    @Test
    @DisplayName("must not have a \"relationship\" object if all fields are null")
    void emptyRelationshipValues() {
        ResourceObject resourceObject = new ResourceObject(TYPE, ID) {
            public Relationship relationship1;
            public Relationship relationship2;
        };

        String json = TestObjectWriter.write(resourceObject.asDocument());
        assertThat(json).isEqualTo("""
                {
                  "data" : {
                    "type" : "testType",
                    "id" : "testId"
                  }
                }""");
    }

    @Nested
    @DisplayName("can be deserialized from a JSON object")
    class DeserializeTest {

        @Test
        @DisplayName("containing no client-generated id")
        void dynamicType() {
            Person person = TestObjectReader.read("""
                    {
                      "type": "person"
                    }""", Person.class);

            assertThat(person).isNotNull();
            assertThat(person.getType()).isEqualTo("person");
            assertThat(person.getId()).isNull();
        }

        @Test
        @DisplayName("ignoring unknown types")
        void ignoreUnknownType() {
            ResourceObject resourceObject = TestObjectReader.read("""
                    {
                      "type": "unknownType"
                    }""", ResourceObject.class);

            assertThat(resourceObject).isNull();
        }

        @Test
        @DisplayName("containing a client-generated id")
        void withId() {
            Person person = TestObjectReader.read("""
                    {
                      "type": "person",
                      "id": "12345"
                    }""", Person.class);

            assertThat(person).isNotNull();
            assertThat(person.getId()).isEqualTo("12345");
        }

        @Test
        @DisplayName("containing attributes")
        void withAttributes() {
            SingleResourceDocument<Person> document = TestObjectReader.read("""
                    {
                      "data": {
                        "type": "person",
                        "id": "123",
                        "attributes": {
                          "firstName": "John",
                          "lastName": "Doe"
                        }
                      }
                    }""", new TypeReference<>() {});

            Person person = document.getData();
            assertThat(person.getType()).isEqualTo("person");
            assertThat(person.getId()).isEqualTo("123");
            assertThat(person.firstName).isEqualTo("John");
            assertThat(person.lastName).isEqualTo("Doe");
        }

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
        }

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
        }

        @Nested
        @DisplayName("containing a relationship which is dynamically parsed")
        class DeserializeRelationshipTest {
            private static class DynamicRelationshipTestResource extends ResourceObject {
                public Relationship toOne;
                public Relationship toMany;

                public DynamicRelationshipTestResource() {
                    super("test");
                }
            }

            private ObjectMapper objectMapper;

            @BeforeEach
            void setUp() {
                objectMapper = new JsonApiObjectMapper()
                        .registerResourceType("test", DynamicRelationshipTestResource.class);
            }

            @Test
            @DisplayName("from to-one relationship")
            void toOneRelationships() throws JsonProcessingException {
                SingleResourceDocument<DynamicRelationshipTestResource> document = objectMapper.readValue("""
                        {
                          "data": {
                            "type": "test",
                            "id": "123",
                            "relationships": {
                              "toOne": {
                                "data": {"type":"address", "id":"54321"}
                              }
                            }
                          }
                        }""", new TypeReference<>() {});

                var resource = document.getData();
                assertToOneRelationship(resource.toOne, new ResourceIdentifierObject("address", "54321"));
            }

            @Test
            @DisplayName("from empty to-one relationship")
            void emptyToOneRelationships() throws JsonProcessingException {
                SingleResourceDocument<DynamicRelationshipTestResource> document = objectMapper.readValue("""
                        {
                          "data": {
                            "type": "test",
                            "id": "123",
                            "relationships": {
                              "toOne": {
                                "data": null
                              }
                            }
                          }
                        }""", new TypeReference<>() {});

                var resource = document.getData();
                assertToOneRelationship(resource.toOne, null);
            }

            @Test
            @DisplayName("from to-many relationship")
            void toManyRelationships() throws JsonProcessingException {
                SingleResourceDocument<DynamicRelationshipTestResource> document = objectMapper.readValue("""
                        {
                          "data": {
                            "type": "test",
                            "id": "123",
                            "relationships": {
                              "toMany": {
                                "data": [
                                  {"type":"address", "id":"1"},
                                  {"type":"address", "id":"2"},
                                  {"type":"address", "id":"3"}
                                ]
                              }
                            }
                          }
                        }""", new TypeReference<>() {});

                var resource = document.getData();
                assertToManyRelationship(resource.toMany);
            }

            @Test
            @DisplayName("from empty to-many relationship")
            void emptyToManyRelationships() throws JsonProcessingException {
                SingleResourceDocument<DynamicRelationshipTestResource> document = objectMapper.readValue("""
                        {
                          "data": {
                            "type": "test",
                            "id": "123",
                            "relationships": {
                              "toMany": {
                                "data": []
                              }
                            }
                          }
                        }""", new TypeReference<>() {});

                var resource = document.getData();
                assertEmptyToManyRelationship(resource.toMany);
            }
        }
    }

    private void assertToOneRelationship(Relationship relationship, ResourceIdentifierObject relatedData) {
        assertThat(relationship).isNotNull();
        assertThat(relationship).isInstanceOf(ToOneRelationship.class);
        ToOneRelationship toOneRelationship = (ToOneRelationship) relationship;
        assertThat(toOneRelationship.getData()).isEqualTo(relatedData);
    }

    private void assertToManyRelationship(Relationship relationship) {
        assertThat(relationship).isNotNull();
        assertThat(relationship).isInstanceOf(ToManyRelationship.class);
        ToManyRelationship toManyRelationship = (ToManyRelationship) relationship;
        assertThat(toManyRelationship.getData()).containsExactlyInAnyOrder(
                new ResourceIdentifierObject("address", "1"),
                new ResourceIdentifierObject("address", "2"),
                new ResourceIdentifierObject("address", "3")
        );
    }

    private void assertEmptyToManyRelationship(Relationship relationship) {
        assertThat(relationship).isNotNull();
        assertThat(relationship).isInstanceOf(ToManyRelationship.class);
        ToManyRelationship toManyRelationship = (ToManyRelationship) relationship;
        assertThat(toManyRelationship.getData()).isNotNull();
        assertThat(toManyRelationship.getData()).isEmpty();
    }
}