package cloud.codestore.jsonapi.error;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.meta.MetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-object-relationships">jsonapi.org</a>
 */
@DisplayName("An error object")
class ErrorObjectDeserializationTest {
    private static final TestObjectReader reader = new TestObjectReader();

    @Nested
    @DisplayName("must contain at least")
    class RequiredMembers {
        @Test
        @DisplayName("a unique identifier for this particular occurrence of the problem")
        void errorId() {
            var error = reader.read("""
                    {
                      "id": "12345"
                    }""", ErrorObject.class);

            assertThat(error.getId()).isEqualTo("12345");
        }

        @Nested
        @DisplayName("a links object that MAY contain")
        class LinksObject {
            @Test
            @DisplayName("a link that leads to further details")
            void aboutLink() {
                var error = reader.read("""
                        {
                          "links": {
                            "about": "/documentation/errors/404"
                          }
                        }""", ErrorObject.class);

                Link link = error.getAboutLink();
                assertThat(link).isNotNull();
                assertThat(link.getHref()).isEqualTo("/documentation/errors/404");
            }

            @Test
            @DisplayName("a link that identifies the type of error")
            void typeLink() {
                var error = reader.read("""
                        {
                          "links": {
                            "type": "/documentation/errors/404"
                          }
                        }""", ErrorObject.class);


                Link link = error.getTypeLink();
                assertThat(link).isNotNull();
                assertThat(link.getHref()).isEqualTo("/documentation/errors/404");
            }
        }

        @Test
        @DisplayName("the HTTP status code applicable to this problem")
        void statusCode() {
            var error = reader.read("""
                    {
                      "status": "401"
                    }""", ErrorObject.class);

            assertThat(error.getStatus()).isEqualTo("401");
        }

        @Test
        @DisplayName("an application-specific error code")
        void errorCode() {
            var error = reader.read("""
                    {
                      "code": "0815"
                    }""", ErrorObject.class);

            assertThat(error.getCode()).isEqualTo("0815");
        }

        @Test
        @DisplayName("a short, human-readable summary of the problem")
        void summary() {
            var error = reader.read("""
                    {
                      "title": "Permission denied"
                    }""", ErrorObject.class);

            assertThat(error.getTitle()).isEqualTo("Permission denied");
        }

        @Test
        @DisplayName("a human-readable explanation")
        void detail() {
            var error = reader.read("""
                    {
                      "detail": "You don't have the permission to delete this resource."
                    }""", ErrorObject.class);

            assertThat(error.getDetail()).isEqualTo("You don't have the permission to delete this resource.");
        }

        @Nested
        @DisplayName("an object containing references to the primary source of the error that may contain")
        class SourceObject {
            @Test
            @DisplayName("a JSON Pointer to the value in the request document that caused the error")
            void jsonPointer() {
                var error = reader.read("""
                    {
                      "source": {
                        "pointer": "/data/relationships/comments"
                      }
                    }""", ErrorObject.class);

                assertThat(error.getSource().getPointer()).isEqualTo("/data/relationships/comments");
            }

            @Test
            @DisplayName("a string indicating which URI query parameter caused the error")
            void queryParameter() {
                var error = reader.read("""
                    {
                      "source": {
                        "parameter": "filter[title]"
                      }
                    }""", ErrorObject.class);

                assertThat(error.getSource().getParameter()).isEqualTo("filter[title]");
            }

            @Test
            @DisplayName("a string indicating the name of a single request header which caused the error")
            void httpHeader() {
                var error = reader.read("""
                    {
                      "source": {
                        "header": "X-API-VERSION"
                      }
                    }""", ErrorObject.class);

                assertThat(error.getSource().getHeader()).isEqualTo("X-API-VERSION");
            }
        }

        @Test
        @DisplayName("a meta object")
        void metaObject() {
            var error = reader.read("""
                    {
                      "meta": {
                        "info": "error object meta info"
                      }
                    }""", ErrorObject.class, pointer -> DummyMetaInformation.class);

            MetaInformation meta = error.getMeta();
            assertThat(meta).isNotNull().isInstanceOf(DummyMetaInformation.class);
            assertThat(((DummyMetaInformation) meta).info).isEqualTo("error object meta info");
        }
    }
}
