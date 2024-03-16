package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.meta.MetaInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-links">jsonapi.org</a>
 */
@DisplayName("A link")
class LinkDeserializationTest {
    private static final TestObjectReader reader = new TestObjectReader();

    @Test
    @DisplayName("must contain a string pointing to the link’s target")
    void hrefRequired() {
        var links = reader.read("""
                {
                  "self": null
                }""", LinksObject.class);
        assertThat(links.get(Link.SELF)).isNull();

        links = reader.read("""
                {
                  "self": " "
                }""", LinksObject.class);
        assertThat(links.get(Link.SELF)).isNull();

        links = reader.read("""
                {
                  "self": {
                    "href": null
                  }
                }""", LinksObject.class);
        assertThat(links.get(Link.SELF)).isNull();

        links = reader.read("""
                {
                  "self": {
                    "href": " "
                  }
                }""", LinksObject.class);
        assertThat(links.get(Link.SELF)).isNull();
    }

    @Nested
    @DisplayName("must be represented as")
    class LinkRepresentation {
        @Test
        @DisplayName("a string")
        void stringValue() {
            var links = reader.read("""
                    {
                      "self": "https://codestore.cloud"
                    }""", LinksObject.class);

            Link link = links.get(Link.SELF);
            assertThat(link).isNotNull();
            assertThat(link.getRelation()).isEqualTo("self");
            assertThat(link.getHref()).isEqualTo("https://codestore.cloud");
        }

        @Test
        @DisplayName("null if the link does not exist")
        void nullValue() {
            var links = reader.read("""
                    {
                      "self": null
                    }""", LinksObject.class);

            Link link = links.get(Link.SELF);
            assertThat(link).isNull();
        }

        @Nested
        @DisplayName("a link object that may also contain")
        class OptionalMembers {
            @Test
            @DisplayName("a string indicating the link’s relation type")
            void relationType() {
                var links = reader.read("""
                        {
                          "self": {
                            "href": "https://codestore.cloud",
                            "rel": "about"
                          }
                        }""", LinksObject.class);

                Link link = links.get(Link.SELF);
                assertThat(link).isNotNull();
                assertThat(link.getRelation()).isEqualTo("about");
                assertThat(link.getHref()).isEqualTo("https://codestore.cloud");
            }

            @Test
            @DisplayName("a link to a description document")
            void describedbyLink() {
                var links = reader.read("""
                        {
                          "self": {
                            "href": "https://codestore.cloud",
                            "describedby": "https://jsonapi.org"
                          }
                        }""", LinksObject.class);

                Link link = links.get(Link.SELF);
                assertThat(link).isNotNull();
                Link describedby = link.getDescribedby();
                assertThat(describedby).isNotNull();
                assertThat(describedby.getHref()).isEqualTo("https://jsonapi.org");
            }

            @Test
            @DisplayName("a human-readable label for the destination")
            void title() {
                var links = reader.read("""
                        {
                          "self": {
                            "href": "https://codestore.cloud",
                            "title": "The {CodeStore} home page"
                          }
                        }""", LinksObject.class);

                Link link = links.get(Link.SELF);
                assertThat(link).isNotNull();
                assertThat(link.getTitle()).isEqualTo("The {CodeStore} home page");
            }

            @Test
            @DisplayName("a string indicating the media type of the link’s target")
            void type() {
                var links = reader.read("""
                        {
                          "self": {
                            "href": "https://codestore.cloud",
                            "type": "text/html"
                          }
                        }""", LinksObject.class);

                Link link = links.get(Link.SELF);
                assertThat(link).isNotNull();
                assertThat(link.getType()).isEqualTo("text/html");
            }

            @Test
            @DisplayName("a string or an array of strings indicating the language(s) of the link’s target")
            void hreflangs() {
                var links = reader.read("""
                        {
                          "self": {
                            "href": "https://codestore.cloud",
                            "hreflang": "de"
                          }
                        }""", LinksObject.class);

                Link link = links.get(Link.SELF);
                assertThat(link).isNotNull();
                assertThat(link.getHreflang()).isNotNull().containsExactly("de");

                links = reader.read("""
                        {
                          "self": {
                            "href": "https://codestore.cloud",
                            "hreflang": ["de", "en"]
                          }
                        }""", LinksObject.class);

                link = links.get(Link.SELF);
                assertThat(link.getHreflang()).isNotNull().containsExactlyInAnyOrder("de", "en");
            }

            @Test
            @DisplayName("a meta object")
            void metaObject() {
                var links = reader.read("""
                        {
                          "self": {
                            "href": "https://codestore.cloud",
                            "meta": {
                              "info": "link meta info"
                            }
                          }
                        }""", LinksObject.class, pointer -> DummyMetaInformation.class);

                Link link = links.get(Link.SELF);
                assertThat(link).isNotNull();

                MetaInformation meta = link.getMeta();
                assertThat(meta).isNotNull().isInstanceOf(DummyMetaInformation.class);
                assertThat(((DummyMetaInformation) meta).info).isEqualTo("link meta info");
            }
        }
    }

    @Nested
    @DisplayName("relation type should be inferred from")
    class LinkRelation {
        @Test
        @DisplayName("the name of the link")
        void inferFromName() {
            var links = reader.read("""
                    {
                      "linkRelation": "https://codestore.cloud"
                    }""", LinksObject.class);

            Link link = links.get("linkRelation");
            assertThat(link).isNotNull();
            assertThat(link.getRelation()).isEqualTo("linkRelation");

            links = reader.read("""
                    {
                      "linkRelation": {
                        "href": "https://codestore.cloud"
                      }
                    }""", LinksObject.class);

            link = links.get("linkRelation");
            assertThat(link).isNotNull();
            assertThat(link.getRelation()).isEqualTo("linkRelation");
        }

        @Test
        @DisplayName("the link's rel member")
        void inferFromRel() {
            var links = reader.read("""
                    {
                      "self": {
                        "href": "https://codestore.cloud",
                        "rel": "linkRelation"
                      }
                    }""", LinksObject.class);

            Link link = links.get(Link.SELF);
            assertThat(link).isNotNull();
            assertThat(link.getRelation()).isEqualTo("linkRelation");
            assertThat(links.get("linkRelation")).isNull();
        }
    }
}
