package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.Person;
import cloud.codestore.jsonapi.TestObjectReader;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A single-resource document")
class SingleResourceDocumentTest {

    @Nested
    @DisplayName("can be deserialized from a JSON string")
    class DeserializeTest {

        @Test
        @DisplayName("containing an jsonapi object")
        void withJsonApiObject() {
            JsonApiDocument jsonApiDocument = TestObjectReader.read("""
                    {
                      "jsonapi" : {
                        "version" : "1.0"
                      }
                    }""", JsonApiDocument.class);

            assertThat(jsonApiDocument).isNotNull();
            assertThat(jsonApiDocument.getJsonApiObject()).isNotNull();
            assertThat(jsonApiDocument.getJsonApiObject().getVersion()).isEqualTo("1.0");
        }

        @Test
        @DisplayName("containing a links object")
        void containingLinks() {
            JsonApiDocument jsonApiDocument = TestObjectReader.read("""
                    {
                      "links" : {
                        "self" : "http://localhost:8080"
                      },
                      "meta" : {}
                    }""", JsonApiDocument.class);

            assertThat(jsonApiDocument).isNotNull();
            assertThat(jsonApiDocument.getMeta()).isNull();
            assertThat(jsonApiDocument.getLinks()).isNotNull();
            assertThat(jsonApiDocument.getLinks().getSelfLink()).isEqualTo("http://localhost:8080");
        }

        @Test
        @DisplayName("containing a single resource object")
        void containingAResourceObject() {
            SingleResourceDocument<Person> jsonApiDocument = TestObjectReader.read("""
                    {
                      "data" : {
                        "type": "person",
                        "id": "123",
                        "attributes": {
                          "firstName": "John",
                          "lastName": "Doe"
                        }
                      }
                    }""", new TypeReference<>() {});

            assertThat(jsonApiDocument).isNotNull();
            Person person = jsonApiDocument.getData();
            assertThat(person).isNotNull();
            assertThat(person.firstName).isEqualTo("John");
            assertThat(person.lastName).isEqualTo("Doe");
        }
    }
}