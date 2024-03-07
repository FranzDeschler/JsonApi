package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-resource-objects">jsonapi.org</a>
 */
@DisplayName("A JSON:API resource object")
class ResourceObjectSerializationTest {
    private static final String TYPE = "article";
    private static final String ID = "1";

    @Test
    @DisplayName("must contain at least type and id")
    void requiredMembers() {
        assertThatThrownBy(() -> new ResourceObject(null, ID) {})
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceObject(" ", ID) {})
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceObject(TYPE, null) {})
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ResourceObject(TYPE, " ") {})
                .isInstanceOf(IllegalArgumentException.class);

        var resource = new ResourceObject(TYPE, ID) {};
        assertEquals("""
                {
                  "type": "article",
                  "id": "1"
                }""", resource);
    }

    @Test
    @DisplayName("can not have an attribute and relationship with the same name")
    void uniqueFieldName() {
        var resource = new ResourceObject(TYPE, ID) {
            @JsonProperty("author")
            public String authorName;
            @JsonProperty("author")
            public ToOneRelationship<Person> authorRelationship;
        };

        assertThatThrownBy(() -> TestObjectWriter.write(resource))
                .rootCause()
                .isInstanceOf(InvalidDefinitionException.class)
                .message().startsWith("Multiple fields representing property \"author\"");
    }

    @Test
    @DisplayName("The id member is not required when the resource object originates at the client and represents a new resource to be created on the server")
    void optionalId() {
        var resource = new ResourceObject(TYPE) {};
        assertEquals("""
                {
                  "type": "article"
                }""", resource);
    }

    @Nested
    @DisplayName("may contain")
    class OptionalMembers {
        @Test
        @DisplayName("an attributes object")
        void attributeObject() {
            var article = new Article("The article's title");
            assertEquals("""
                    {
                      "type": "article",
                      "id": "1",
                      "attributes": {
                        "title": "The article's title"
                      }
                    }""", article);
        }

        @Test
        @DisplayName("a relationships object")
        void relationshipObject() {
            var author = new Person("5");
            var article = new Article(author);
            assertEquals("""
                    {
                      "type": "article",
                      "id": "1",
                      "relationships": {
                        "author": {
                          "data":{"type":"person", "id":"5"}
                        }
                      }
                    }""", article);
        }

        @Test
        @DisplayName("a links object")
        void linksObject() {
            var article = new Article().setSelfLink("/articles/1");
            assertEquals("""
                    {
                      "type": "article",
                      "id": "1",
                      "links": {
                        "self": "/articles/1"
                      }
                    }""", article);
        }

        @Test
        @DisplayName("a meta object")
        void metaObject() {
            var article = new Article().setMeta(new DummyMetaInformation());
            assertEquals("""
                    {
                      "type": "article",
                      "id": "1",
                      "meta": {
                        "info": "dummy meta info"
                      }
                    }""", article);
        }
    }

    private static class Article extends ResourceObject {
        @JsonProperty("title")
        public String title;
        @JsonProperty("author")
        public ToOneRelationship<Person> author;

        Article() {
            super(TYPE, ID);
        }

        Article(String title) {
            this();
            this.title = title;
        }

        Article(Person author) {
            this();
            this.author = ResourceObject.asRelationship(author);
        }
    }

    private static class Person extends ResourceObject {
        Person(String id) {
            super("person", id);
        }
    }
}
