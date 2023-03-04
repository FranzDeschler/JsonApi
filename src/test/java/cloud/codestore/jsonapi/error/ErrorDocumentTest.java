package cloud.codestore.jsonapi.error;

import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("An error document")
class ErrorDocumentTest {
    @Test
    @DisplayName("must not be empty")
    void mustNotBeEmpty() {
        assertThatThrownBy(() -> new ErrorDocument((ErrorObject) null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new ErrorDocument((ErrorObject[]) null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ErrorDocument(new ErrorObject[0]))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("can contain one error object")
    void oneError() {
        String json = TestObjectWriter.write(new ErrorDocument(new ErrorObject().setId("test")));

        assertThat(json).isEqualTo("""
                {
                  "errors" : [ {
                    "id" : "test"
                  } ]
                }""");
    }

    @Test
    @DisplayName("can contain multiple error objects")
    void multipleError() {
        String json = TestObjectWriter.write(
                new ErrorDocument(
                        new ErrorObject[]{
                                new ErrorObject().setId("test1"),
                                new ErrorObject().setId("test2"),
                                new ErrorObject().setId("test3")
                        }
                )
        );

        assertThat(json).isEqualTo("""
                {
                  "errors" : [ {
                    "id" : "test1"
                  }, {
                    "id" : "test2"
                  }, {
                    "id" : "test3"
                  } ]
                }""");
    }

    @Nested
    @DisplayName("can be parsed from a JSON array")
    class DeserializeTest {
        ErrorDocument errorDocument;

        @Test
        @DisplayName("which is an empty object")
        void emptyObject() {
            errorDocument = TestObjectReader.read("{}", ErrorDocument.class);
            assertThat(errorDocument).isNotNull();
            assertThat(errorDocument.getErrors()).isNull();
        }

        @Test
        @DisplayName("which is empty")
        void emptyArray() {
            errorDocument = TestObjectReader.read("""
                    {
                      "errors": []
                    }""", ErrorDocument.class);

            assertErrors(0);
        }

        @Test
        @DisplayName("which contains a single error")
        void oneError() {
            errorDocument = TestObjectReader.read("""
                    {
                      "errors": [{}]
                    }""", ErrorDocument.class);

            assertErrors(1);
        }

        @Test
        @DisplayName("which contains multiple errors")
        void multipleError() {
            errorDocument = TestObjectReader.read("""
                    {
                      "errors": [{},{},{}]
                    }""", ErrorDocument.class);

            assertErrors(3);
        }

        private void assertErrors(int count) {
            assertThat(errorDocument).isNotNull();
            assertThat(errorDocument.getErrors()).hasSize(count);
        }
    }
}