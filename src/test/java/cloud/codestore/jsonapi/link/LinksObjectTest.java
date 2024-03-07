package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.TestObjectReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A links object")
class LinksObjectTest {
    private static final String HREF = "http://localhost:8080";

    @Test
    @DisplayName("is empty after creation")
    void empty() {
        assertThat(new LinksObject().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("does not allow null links")
    void linkNotNull() {
        assertThatThrownBy(() -> new LinksObject().add("test", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("does not allow null link-names")
    void linkNameNotNull() {
        assertThatThrownBy(() -> new LinksObject().add(null, new Link(HREF)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("can find the \"self\" link")
    void getSelfLink() {
        LinksObject linksObject = new LinksObject();
        assertThat(linksObject.getSelfLink()).isNull();
        linksObject.add(Link.SELF, new Link(HREF));
        assertThat(linksObject.getSelfLink()).isEqualTo(HREF);
    }

    @Nested
    @DisplayName("can be parsed from JSON")
    class DeserializeTest {
        private LinksObject linksObject;

        @Test
        @DisplayName("which is empty")
        void emptyLinksObject() {
            linksObject = TestObjectReader.read("{}", LinksObject.class);

            assertThat(linksObject).isNotNull();
            assertThat(linksObject.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("containing a single link as string")
        void singleLink() {
            linksObject = TestObjectReader.read("""
                    {
                      "self": "http://localhost:8080"
                    }""", LinksObject.class);

            assertThat(linksObject).isNotNull();
            Link link = linksObject.get(Link.SELF);
            assertThat(link).isNotNull();
            assertThat(link.getHref()).isEqualTo(HREF);
            assertThat(link.getRelation()).isEqualTo(Link.SELF);
            assertThat(link.getTitle()).isNull();
            assertThat(link.getDescribedby()).isNull();
            assertThat(link.getHreflang()).isNull();
            assertThat(link.getType()).isNull();
        }

        @Test
        @DisplayName("containing multiple links as string")
        void multipleLinks() {
            linksObject = TestObjectReader.read("""
                    {
                      "self": "http://localhost:8080",
                      "customRelation": "http://localhost:8080"
                    }""", LinksObject.class);

            assertThat(linksObject).isNotNull();
            assertThat(linksObject.getValues()).hasSize(2);
            assertThat(linksObject.get(Link.SELF)).isNotNull();
            assertThat(linksObject.get("customRelation")).isNotNull();
        }

        @Test
        @DisplayName("containing a link as object")
        void singleLinkAsObject() {
            linksObject = TestObjectReader.read("""
                    {
                      "self": {
                        "href": "http://localhost:8080"
                      }
                    }""", LinksObject.class);


            assertThat(linksObject).isNotNull();
            assertThat(linksObject.getValues()).hasSize(1);
            Link link = linksObject.get(Link.SELF);
            assertThat(link).isNotNull();
            assertThat(link.getRelation()).isEqualTo(Link.SELF);
            assertThat(link.getHref()).isEqualTo(HREF);
        }
    }
}