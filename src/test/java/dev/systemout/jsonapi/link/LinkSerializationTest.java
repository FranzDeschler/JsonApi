package dev.systemout.jsonapi.link;

import dev.systemout.jsonapi.DummyMetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static dev.systemout.jsonapi.JsonAssertion.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-links">jsonapi.org</a>
 */
@DisplayName("A link")
class LinkSerializationTest {
    @Test
    @DisplayName("must contain a string pointing to the link’s target")
    void hrefRequired() {
        assertThatThrownBy(() -> new Link(null))
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
            var links = new LinksObject().add(Link.SELF, new Link("https://systemout.dev"));
            assertEquals("""
                    {
                      "self": "https://systemout.dev"
                    }""", links);
        }

        @Nested
        @DisplayName("a link object that may also contain")
        class OptionalMembers {
            final Link link = new Link("https://systemout.dev");

            @Test
            @DisplayName("a string indicating the link’s relation type")
            void relationType() {
                link.setRelation(Link.ABOUT);
                assertEquals("""
                        {
                          "href": "https://systemout.dev",
                          "rel": "about"
                        }""", link);
            }

            @Test
            @DisplayName("a link to a description document")
            void describedbyLink() {
                link.setDescribedby(new Link("https://jsonapi.org"));
                assertEquals("""
                        {
                          "href": "https://systemout.dev",
                          "describedby": "https://jsonapi.org"
                        }""", link);
            }

            @Test
            @DisplayName("a human-readable label for the destination")
            void title() {
                link.setTitle("The SystemOut home page");
                assertEquals("""
                        {
                          "href": "https://systemout.dev",
                          "title": "The SystemOut home page"
                        }""", link);
            }

            @Test
            @DisplayName("a string indicating the media type of the link’s target")
            void type() {
                link.setType("text/html");
                assertEquals("""
                        {
                          "href": "https://systemout.dev",
                          "type": "text/html"
                        }""", link);
            }

            @Test
            @DisplayName("a string or an array of strings indicating the language(s) of the link’s target")
            void hreflangs() {
                link.setHreflang("de");
                assertEquals("""
                        {
                          "href": "https://systemout.dev",
                          "hreflang": "de"
                        }""", link);

                link.setHreflang("de", "en");
                assertEquals("""
                        {
                          "href": "https://systemout.dev",
                          "hreflang": ["de", "en"]
                        }""", link);
            }

            @Test
            @DisplayName("a meta object")
            void metaObject() {
                link.setMeta(new DummyMetaInformation());
                assertEquals("""
                        {
                          "href": "https://systemout.dev",
                          "meta": {
                            "info": "dummy meta info"
                          }
                        }""", link);
            }
        }
    }
}
