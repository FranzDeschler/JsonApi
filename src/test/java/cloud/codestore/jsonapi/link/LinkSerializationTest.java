package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.DummyMetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-links">jsonapi.org</a>
 */
@DisplayName("A link")
class LinkSerializationTest {
    @Test
    @DisplayName("must contain a string pointing to the link’s target")
    void hrefRequired() {
        assertThatThrownBy(() -> new Link((String) null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Link(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Link(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Nested
    @DisplayName("must be represented as")
    class LinkRepresentation {
        @Test
        @DisplayName("a string")
        void stringValue() {
            var links = new LinksObject().add(Link.SELF, new Link("https://codestore.cloud"));
            assertEquals("""
                    {
                      "self": "https://codestore.cloud"
                    }""", links);
        }

        @Nested
        @DisplayName("a link object that may also contain")
        class OptionalMembers {
            final Link link = new Link("https://codestore.cloud");

            @Test
            @DisplayName("a string indicating the link’s relation type")
            void relationType() {
                link.setRelation(Link.ABOUT);
                assertEquals("""
                        {
                          "href": "https://codestore.cloud",
                          "rel": "about"
                        }""", link);
            }

            @Test
            @DisplayName("a link to a description document")
            void describedbyLink() {
                link.setDescribedby(new Link("https://jsonapi.org"));
                assertEquals("""
                        {
                          "href": "https://codestore.cloud",
                          "describedby": "https://jsonapi.org"
                        }""", link);
            }

            @Test
            @DisplayName("a human-readable label for the destination")
            void title() {
                link.setTitle("The {CodeStore} home page");
                assertEquals("""
                        {
                          "href": "https://codestore.cloud",
                          "title": "The {CodeStore} home page"
                        }""", link);
            }

            @Test
            @DisplayName("a string indicating the media type of the link’s target")
            void type() {
                link.setType("text/html");
                assertEquals("""
                        {
                          "href": "https://codestore.cloud",
                          "type": "text/html"
                        }""", link);
            }

            @Test
            @DisplayName("a string or an array of strings indicating the language(s) of the link’s target")
            void hreflangs() {
                link.setHreflang("de");
                assertEquals("""
                        {
                          "href": "https://codestore.cloud",
                          "hreflang": "de"
                        }""", link);

                link.setHreflang("de", "en");
                assertEquals("""
                        {
                          "href": "https://codestore.cloud",
                          "hreflang": ["de", "en"]
                        }""", link);
            }

            @Test
            @DisplayName("a meta object")
            void metaObject() {
                link.setMeta(new DummyMetaInformation());
                assertEquals("""
                        {
                          "href": "https://codestore.cloud",
                          "meta": {
                            "info": "dummy meta info"
                          }
                        }""", link);
            }
        }
    }
}
