package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.TestObjectReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A resource collection")
class ResourceCollectionDocumentTest {
    @Nested
    @DisplayName("can be deserialized from a JSON string")
    class DeserializeTest {
        @Test
        @DisplayName("containing an empty array as primary resource")
        void emptyDataArray() {
            JsonApiDocument jsonApiDocument = TestObjectReader.read("""
                    {
                      "data" : []
                    }""", JsonApiDocument.class);

            assertThat(jsonApiDocument).isNotNull();
            assertThat(jsonApiDocument).isInstanceOf(ResourceCollectionDocument.class);
            assertThat(((ResourceCollectionDocument<?>) jsonApiDocument).getData()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("containing multiple resource objects")
        void multipleResourceObjects() {
            ResourceCollectionDocument<?> jsonApiDocument = TestObjectReader.read("""
                    {
                      "data" : [{
                        "type": "person",
                        "id": "123",
                        "attributes": {
                          "name": "John Doe",
                          "age": 45
                        }
                      }, {
                        "type": "person",
                        "id": "321",
                        "attributes": {
                          "name": "Jane Doe",
                          "age": 40
                        }
                      }]
                    }""", ResourceCollectionDocument.class);

            assertThat(jsonApiDocument).isNotNull();
            assertThat(jsonApiDocument.getData()).isNotNull().hasSize(2);
        }
    }
}
