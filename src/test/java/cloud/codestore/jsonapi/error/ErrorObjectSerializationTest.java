package cloud.codestore.jsonapi.error;

import cloud.codestore.jsonapi.DummyMetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-object-relationships">jsonapi.org</a>
 */
@DisplayName("An error object")
class ErrorObjectSerializationTest {
    final ErrorObject errorObject = new ErrorObject();

    @Nested
    @DisplayName("must contain at least")
    class RequiredMembers {
        @Test
        @DisplayName("a unique identifier for this particular occurrence of the problem")
        void errorId() {
            errorObject.setId("12345");
            assertEquals("""
                    {
                      "id": "12345"
                    }""", errorObject);
        }

        @Nested
        @DisplayName("a links object that MAY contain")
        class LinksObject {
            @Test
            @DisplayName("a link that leads to further details")
            void aboutLink() {
                errorObject.setAboutLink("/documentation/errors/404");
                assertEquals("""
                        {
                          "links": {
                            "about": "/documentation/errors/404"
                          }
                        }""", errorObject);
            }

            @Test
            @DisplayName("a link that identifies the type of error")
            void typeLink() {
                errorObject.setTypeLink("/documentation/errors/404");
                assertEquals("""
                        {
                          "links": {
                            "type": "/documentation/errors/404"
                          }
                        }""", errorObject);
            }
        }

        @Test
        @DisplayName("the HTTP status code applicable to this problem")
        void statusCode() {
            errorObject.setStatus("401");
            assertEquals("""
                    {
                      "status": "401"
                    }""", errorObject);
        }

        @Test
        @DisplayName("an application-specific error code")
        void errorCode() {
            errorObject.setCode("0815");
            assertEquals("""
                    {
                      "code": "0815"
                    }""", errorObject);
        }

        @Test
        @DisplayName("a short, human-readable summary of the problem")
        void summary() {
            errorObject.setTitle("Permission denied");
            assertEquals("""
                    {
                      "title": "Permission denied"
                    }""", errorObject);
        }

        @Test
        @DisplayName("a human-readable explanation")
        void detail() {
            errorObject.setDetail("You don't have the permission to delete this resource.");
            assertEquals("""
                    {
                      "detail": "You don't have the permission to delete this resource."
                    }""", errorObject);
        }

        @Nested
        @DisplayName("an object containing references to the primary source of the error that may contain")
        class SourceObject {
            @Test
            @DisplayName("a JSON Pointer to the value in the request document that caused the error")
            void jsonPointer() {
                errorObject.setSource(new ErrorSource().setPointer("/data/relationships/comments"));
                assertEquals("""
                    {
                      "source": {
                        "pointer": "/data/relationships/comments"
                      }
                    }""", errorObject);
            }

            @Test
            @DisplayName("a string indicating which URI query parameter caused the error")
            void queryParameter() {
                errorObject.setSource(new ErrorSource().setParameter("filter[title]"));
                assertEquals("""
                    {
                      "source": {
                        "parameter": "filter[title]"
                      }
                    }""", errorObject);
            }

            @Test
            @DisplayName("a string indicating the name of a single request header which caused the error")
            void httpHeader() {
                errorObject.setSource(new ErrorSource().setHeader("X-API-VERSION"));
                assertEquals("""
                    {
                      "source": {
                        "header": "X-API-VERSION"
                      }
                    }""", errorObject);
            }
        }

        @Test
        @DisplayName("a meta object")
        void metaObject() {
            errorObject.setMeta(new DummyMetaInformation());
            assertEquals("""
                    {
                      "meta": {
                        "info": "dummy meta info"
                      }
                    }""", errorObject);
        }
    }
}
