package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.link.LinksObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A relationship")
class RelationshipTest {
    private static final String HREF = "http://localhost:8080";

    @Test
    @DisplayName("is not included")
    void notIncluded() {
        assertThat(new Relationship().isIncluded()).isFalse();
    }

    @Test
    @DisplayName("is empty after creation")
    void empty() {
        LinksObject links = new Relationship().getLinks();
        assertThat(links).isNotNull();
        assertThat(links.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("can contain a \"related\" link")
    void relatedLink() {
        String json = TestObjectWriter.write(new Relationship(HREF));
        assertThat(json).isEqualTo("""
                {
                  "links" : {
                    "related" : "http://localhost:8080"
                  }
                }""");
    }

    @Test
    @DisplayName("can contain a \"self\" link")
    void selfLink() {
        String json = TestObjectWriter.write(new Relationship().setSelfLink(HREF));
        assertThat(json).isEqualTo("""
                {
                  "links" : {
                    "self" : "http://localhost:8080"
                  }
                }""");
    }

    @Test
    @DisplayName("can contain meta-information")
    void canContainMeta() {
        String json = TestObjectWriter.write(new Relationship(HREF).setMeta(new DummyMetaInformation()));
        assertThat(json).isEqualTo("""
                {
                  "links" : {
                    "related" : "http://localhost:8080"
                  },
                  "meta" : {
                    "info" : "dummy meta info"
                  }
                }""");
    }

    @Test
    @DisplayName("provides the \"related\" link after deserialization")
    void deserializedRelatedLink() {
        Relationship relationship = TestObjectReader.read("""
                {
                  "links" : {
                    "related" : "http://localhost:8080"
                  }
                }""", Relationship.class);

        assertThat(relationship.getRelatedResourceLink()).isEqualTo("http://localhost:8080");
    }
}