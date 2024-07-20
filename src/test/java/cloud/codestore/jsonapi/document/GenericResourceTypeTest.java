package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * The type of the resource objects needs to be defined at compile time.
 * When the type is specified as a generic type, it´s not possible for Jackson to determine the type of
 * the data-array. In this case, calling {@link ResourceCollectionDocument#getData()} will throw a {@link ClassCastException}.
 * To prevent this, the data-array needs to be cast dynamically by providing the concrete type.
 */
@DisplayName("When specifying the subtype of a collection as generic type")
class GenericResourceTypeTest {

    private ResourceCollectionDocument<Person> document = readCollection(Person.class);

    @Test
    @DisplayName("getData() fails with a ClassCastException")
    void castError() {
        assertThatExceptionOfType(ClassCastException.class).isThrownBy(() -> {
            Person[] cast = document.getData();
        });
    }

    @Test
    @DisplayName("the data needs to be cast dynamically")
    void dynamicCasting() {
        Person[] data = document.getData(Person.class);
        assertThat(data.getClass()).isEqualTo(Person[].class);
    }

    private <T extends ResourceObject> ResourceCollectionDocument<T> readCollection(Class<T> type) {
        return new TestObjectReader(Person.class).read("""
                {
                  "data" : [{
                    "type": "person",
                    "id": "123",
                    "attributes": {
                      "name": "John Doe",
                      "age": 45
                    }
                  }]
                }""", new TypeReference<>() {});
    }

    private static class Person extends ResourceObject {
        final String name;
        final int age;

        @JsonCreator
        Person(@JsonProperty("name") String name, @JsonProperty("age") int age) {
            super("person");
            this.name = name;
            this.age = age;
        }
    }
}
