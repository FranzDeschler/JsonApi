package cloud.codestore.jsonapi.error;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("An error object")
class ErrorObjectTest
{
    @Test
    @DisplayName("can contain an id")
    void containsId()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setId("ERROR_ID")
        );
        assertThat(json).isEqualTo("""
                {
                  "id" : "ERROR_ID"
                }""");
    }
    
    @Test
    @DisplayName("can contain an \"about\" link")
    void containsAboutLink()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setAboutLink("https://codestore.cloud/help")
        );
        assertThat(json).isEqualTo("""
                {
                  "links" : {
                    "about" : "https://codestore.cloud/help"
                  }
                }""");
    }
    
    @Test
    @DisplayName("can contain a status")
    void containsStatus()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setStatus("400")
        );
        assertThat(json).isEqualTo("""
                {
                  "status" : "400"
                }""");
    }
    
    @Test
    @DisplayName("can contain a code")
    void containsCode()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setCode("12345")
        );
        assertThat(json).isEqualTo("""
                {
                  "code" : "12345"
                }""");
    }
    
    @Test
    @DisplayName("can contain a title")
    void containsTitle()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setTitle("An error occured")
        );
        assertThat(json).isEqualTo("""
                {
                  "title" : "An error occured"
                }""");
    }
    
    @Test
    @DisplayName("can contain a detailed description")
    void containsDescription()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setDetail("A detailed error description.")
        );
        assertThat(json).isEqualTo("""
                {
                  "detail" : "A detailed error description."
                }""");
    }
    
    @Test
    @DisplayName("can contain a parameter as source")
    void containsParameterSource()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setSource(new ErrorSource().setParameter("customQueryParameter"))
        );
        assertThat(json).isEqualTo("""
                {
                  "source" : {
                    "parameter" : "customQueryParameter"
                  }
                }""");
    }

    @Test
    @DisplayName("can contain a JSON pointer as source")
    void containsPointerSource()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setSource(new ErrorSource().setPointer("/foo/bar"))
        );
        assertThat(json).isEqualTo("""
                {
                  "source" : {
                    "pointer" : "/foo/bar"
                  }
                }""");
    }
    
    @Test
    @DisplayName("can contain meta-information")
    void containsMetaInformation()
    {
        String json = TestObjectWriter.write(
                new ErrorObject().setMeta(new DummyMetaInformation())
        );
        assertThat(json).isEqualTo("""
                {
                  "meta" : {
                    "info" : "dummy meta info"
                  }
                }""");
    }

    @Nested
    @DisplayName("can be parsed from a JSON string")
    class DeserializeTest {
        @Test
        @DisplayName("which contains an empty object")
        void emptyObject() {
            ErrorObject errorObject = TestObjectReader.read("{}", ErrorObject.class);

            assertThat(errorObject).isNotNull();
            assertThat(errorObject.getId()).isNull();
            assertThat(errorObject.getStatus()).isNull();
            assertThat(errorObject.getCode()).isNull();
            assertThat(errorObject.getTitle()).isNull();
            assertThat(errorObject.getDetail()).isNull();
            assertThat(errorObject.getSource()).isNull();
            assertThat(errorObject.getLinks()).isNull();
            assertThat(errorObject.getMeta()).isNull();
        }

        @Test
        @DisplayName("which contains all fields")
        void fullErrorObject() {
            ErrorObject errorObject = TestObjectReader.read("""
                    {
                      "id": "12345",
                      "status": "400",
                      "code": "ERROR_CODE",
                      "title": "error title",
                      "detail": "a detailed description",
                      "source": {
                        "pointer": "/foo/bar"
                      },
                      "links": {
                        "about": "http://localhost:8080/help"
                      },
                      "meta" : {
                        "info" : "dummy meta info"
                      }
                    }""", ErrorObject.class, pointer -> DummyMetaInformation.class);

            assertThat(errorObject).isNotNull();
            assertThat(errorObject.getId()).isEqualTo("12345");
            assertThat(errorObject.getStatus()).isEqualTo("400");
            assertThat(errorObject.getCode()).isEqualTo("ERROR_CODE");
            assertThat(errorObject.getTitle()).isEqualTo("error title");
            assertThat(errorObject.getDetail()).isEqualTo("a detailed description");
            assertThat(errorObject.getSource()).isNotNull();
            assertThat(errorObject.getSource().getPointer()).isEqualTo("/foo/bar");
            assertThat(errorObject.getLinks()).isNotNull();
            assertThat(errorObject.getLinks().get("about")).isNotNull();
            assertThat(errorObject.getMeta()).isNotNull();
        }
    }
}