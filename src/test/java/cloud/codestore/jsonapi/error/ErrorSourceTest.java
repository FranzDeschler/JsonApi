package cloud.codestore.jsonapi.error;

import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("An error source object")
class ErrorSourceTest {
    @Test
    @DisplayName("can contain a JSON pointer")
    void containsPointer() {
        String json = TestObjectWriter.write(
                new ErrorSource().setPointer("/foo/bar")
        );
        assertThat(json).isEqualTo("""
                {
                  "pointer" : "/foo/bar"
                }""");
    }

    @Test
    @DisplayName("can contain a parameter")
    void containsParameter() {
        String json = TestObjectWriter.write(
                new ErrorSource().setParameter("filter[title]")
        );
        assertThat(json).isEqualTo("""
                {
                  "parameter" : "filter[title]"
                }""");
    }

    @Test
    @DisplayName("can contain a header")
    void containsHeader() {
        String json = TestObjectWriter.write(
                new ErrorSource().setHeader("X-API-VERSION")
        );
        assertThat(json).isEqualTo("""
                {
                  "header" : "X-API-VERSION"
                }""");
    }

    @Nested
    @DisplayName("can be parsed from a JSON string")
    class DeserializeTest {
        @Test
        @DisplayName("which contains a pointer")
        void pointerErrorSource() {
            ErrorSource source = TestObjectReader.read("""
                    {
                      "pointer" : "/foo/bar"
                    }""", ErrorSource.class);

            assertThat(source).isNotNull();
            assertThat(source.getPointer()).isEqualTo("/foo/bar");
            assertThat(source.getParameter()).isNull();
        }

        @Test
        @DisplayName("which contains a parameter")
        void parameterErrorSource() {
            ErrorSource source = TestObjectReader.read("""
                    {
                      "parameter" : "filter[title]"
                    }""", ErrorSource.class);

            assertThat(source).isNotNull();
            assertThat(source.getParameter()).isEqualTo("filter[title]");
            assertThat(source.getPointer()).isNull();
            assertThat(source.getHeader()).isNull();
        }

        @Test
        @DisplayName("which contains a header")
        void pointerErrorHeader() {
            ErrorSource source = TestObjectReader.read("""
                    {
                      "header" : "X-API-VERSION"
                    }""", ErrorSource.class);

            assertThat(source).isNotNull();
            assertThat(source.getHeader()).isEqualTo("X-API-VERSION");
            assertThat(source.getParameter()).isNull();
            assertThat(source.getPointer()).isNull();
        }
    }
}