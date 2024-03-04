package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A JSON:API Link")
class LinkTest {
    private static final String HREF = "http://localhost:8080";

    @Test
    @DisplayName("must have a href")
    void hrefMandatory() {
        assertThatThrownBy(() -> new Link(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Link(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Link(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("can be a string")
    void asString() {
        LinksObject linksObject = new LinksObject().add(Link.SELF, new Link(HREF));
        String jsonString = TestObjectWriter.write(linksObject);
        assertThat(jsonString).isEqualToIgnoringWhitespace("""
                {
                  "self" : "http://localhost:8080"
                }""");
    }

    @Test
    @DisplayName("can be an object")
    void asObject() {
        // given
        Link link = new Link(HREF).setMeta(new DummyMetaInformation())
                                  .setTitle("A human readable title.")
                                  .setDescribedby(new Link("https://jsonapi.org"))
                                  .setHreflang("de")
                                  .setType("text/html")
                                  .setRelation("doc");

        LinksObject linksObject = new LinksObject().add("linkName", link);

        // when
        String jsonString = TestObjectWriter.write(linksObject);

        // then
        assertThat(jsonString).contains(
                "\"linkName\" : {",
                "\"rel\" : \"doc\"",
                "\"href\" : \"http://localhost:8080\"",
                "\"title\" : \"A human readable title.\"",
                "\"type\" : \"text/html\"",
                "\"describedby\" : \"https://jsonapi.org\"",
                "\"meta\" : {"
        );
    }

    @Test
    @DisplayName("can contain multiple hreflangs")
    void multipleHreflangs() {
        Link link = new Link(HREF).setHreflang("de", "en", "fr");
        String jsonString = TestObjectWriter.write(link);
        assertThat(jsonString).isEqualToIgnoringWhitespace("""
                {
                  "href" : "http://localhost:8080",
                  "hreflang" : ["de", "en", "fr"]
                }""");
    }

    @Nested
    @DisplayName("can be parsed from JSON")
    class DeserializeTest {
        @Test
        @DisplayName("which doesn't specify a custom relation other than the link-name")
        void noCustomRelation() {
            LinksObject linksObject = TestObjectReader.read("""
                    {
                      "customRelation": {
                        "href": "http://localhost:8080"
                      }
                    }""", LinksObject.class);

            assertThat(linksObject).isNotNull();
            Link link = linksObject.get("customRelation");
            assertThat(link).isNotNull();
            assertThat(link.getRelation()).isEqualTo("customRelation");
            assertThat(link.getHref()).isEqualTo(HREF);
        }

        @Test
        @DisplayName("which has a different relation than the name")
        void differentRelation() {
            LinksObject linksObject = TestObjectReader.read("""
                    {
                      "customRelation": {
                        "href": "http://localhost:8080",
                        "rel": "doc"
                      }
                    }""", LinksObject.class);

            assertThat(linksObject).isNotNull();
            Link link = linksObject.get("doc");
            assertThat(link).isNotNull();
            assertThat(link.getRelation()).isEqualTo("doc");
            assertThat(link.getHref()).isEqualTo(HREF);
        }

        @Test
        @DisplayName("that contains a single hreflang")
        void singleHreflang() {
            Link link = TestObjectReader.read("""
                    {
                      "href": "http://localhost:8080",
                      "hreflang": "de"
                    }""", Link.class);

            assertThat(link).isNotNull();
            assertThat(link.getHreflang()).containsExactly("de");
        }

        @Test
        @DisplayName("that contains multiple hreflangs")
        void multipleHreflangs() {
            Link link = TestObjectReader.read("""
                    {
                      "href": "http://localhost:8080",
                      "hreflang" : ["de", "en", "fr"]
                    }""", Link.class);

            assertThat(link).isNotNull();
            assertThat(link.getHreflang()).containsExactlyInAnyOrder("de", "en", "fr");
        }
    }
}