package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.TestObjectReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A links object")
class LinksObjectTest
{
    private static final String HREF = "http://localhost:8080";
    
    @Test
    @DisplayName("is empty after creation")
    void empty()
    {
        assertThat(new LinksObject().isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("does not allow null links")
    void linksNotNull()
    {
        assertThatThrownBy(() -> new LinksObject().add(null))
                  .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("can find the \"self\" link")
    void getSelfLink()
    {
        LinksObject linksObject = new LinksObject();
        assertThat(linksObject.getSelfLink()).isNull();
        linksObject.add(new Link(Link.SELF, HREF));
        assertThat(linksObject.getSelfLink()).isEqualTo(HREF);
    }

    @Nested
    @DisplayName("can be parsed from JSON")
    class DeserializeTest
    {
        private LinksObject linksObject;

        @Test
        @DisplayName("which is empty")
        void emptyLinksObject() {
            linksObject = TestObjectReader.read("{}", LinksObject.class);
            expectLinks();
        }

        @Test
        @DisplayName("containing a single link as string")
        void singleLink() {
            linksObject = TestObjectReader.read("""
                    {
                      "self": "http://localhost:8080"
                    }""", LinksObject.class);

            expectLinks(new Link(Link.SELF, "http://localhost:8080"));
        }

        @Test
        @DisplayName("containing multiple links as string")
        void multipleLinks() {
            linksObject = TestObjectReader.read("""
                    {
                      "self": "http://localhost:8080",
                      "customRelation": "http://localhost:8080"
                    }""", LinksObject.class);

            expectLinks(
                    new Link(Link.SELF, "http://localhost:8080"),
                    new Link("customRelation", "http://localhost:8080")
            );
        }

        @Test
        @DisplayName("containing a single link as object")
        void singleLinkAsObject() {
            linksObject = TestObjectReader.read("""
                    {
                      "self": {
                        "href": "http://localhost:8080"
                      }
                    }""", LinksObject.class);


            expectLinks(new Link(Link.SELF, "http://localhost:8080"));
        }

        private void expectLinks(Link... links) {
            assertThat(linksObject).isNotNull();
            if (links.length == 0) {
                assertThat(linksObject.isEmpty()).isTrue();
            } else {
                assertThat(linksObject.isEmpty()).isFalse();
                assertThat(linksObject.getLinks()).hasSize(links.length);
                for (Link link : links) {
                    assertThat(linksObject.getLinks()).containsEntry(link.getRelation(), link);
                }
            }
        }
    }
}