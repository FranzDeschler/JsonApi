package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.Article;
import cloud.codestore.jsonapi.Person;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import cloud.codestore.jsonapi.relationship.Relationship;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A resource object")
class ResourceObjectTest {
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
    @DisplayName("must not have an \"attributes\" object if all attributes are null")
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
    @DisplayName("must not have a \"relationship\" object if all relationships are null")
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
        @DisplayName("with an unknown types")
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
        @DisplayName("containing a relationship")
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

            assertThat(document).isNotNull();
            assertThat(document.getData().author).isNotNull();
        }

        @Test
        @DisplayName("containing a JsonApiObject with meta information")
        void withJsonApiObject() {
            SingleResourceDocument<Person> document = TestObjectReader.read("""
                    {
                      "jsonapi" : {
                        "version" : "1.0",
                        "meta" : {
                          "documentation" : "https://jsonapi.org/format/1.0/"
                        }
                      },
                      "data": {
                        "type": "person",
                        "id": "123",
                        "attributes": {
                          "firstName": "John",
                          "lastName": "Doe"
                        }
                      }
                    }""", new TypeReference<>() {});

            assertThat(document.getData()).isNotNull();
        }
    }
}