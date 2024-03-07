package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-compound-documents">jsonapi.org</a>
 */
@DisplayName("A compound document")
class CompoundDocumentSerializationTest {
    @Test
    @DisplayName("must contain an array of resource objects in a top-level included member")
    void includeResources() {
        Person john = new Person("1", "John Doe");
        Comment comment = new Comment("1", "Nice Article, John!");
        var article = new Article("1", "John's article", john, comment);
        var document = new SingleResourceDocument<>(article);

        assertEquals("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "attributes" : {
                      "title" : "John's article"
                    },
                    "relationships": {
                      "author": {
                        "data": {"type":"person", "id":"1"}
                      },
                      "comments": {
                        "data":[{"type":"comment", "id":"1"}]
                      }
                    }
                  },
                  "included": [{
                    "type": "person",
                    "id": "1",
                    "attributes": {
                      "name": "John Doe"
                    }
                  }, {
                    "type": "comment",
                    "id": "1",
                    "attributes": {
                      "text": "Nice Article, John!"
                    }
                  }]
                }""", document);
    }

    @Test
    @DisplayName("must not include more than one resource object for each type and id pair")
    void uniqueIncludedResource() {
        Person john = new Person("1", "John Doe");
        var article1 = new Article("1", "John's first article", john);
        var article2 = new Article("2", "Another article", john);
        var document = new ResourceCollectionDocument<>(new Article[]{article1, article2});

        assertEquals("""
                {
                  "data": [{
                    "type": "article",
                    "id": "1",
                    "attributes" : {
                      "title" : "John's first article"
                    },
                    "relationships": {
                      "author": {
                        "data": {"type":"person", "id":"1"}
                      }
                    }
                  }, {
                    "type": "article",
                    "id": "2",
                    "attributes" : {
                      "title" : "Another article"
                    },
                    "relationships": {
                      "author": {
                        "data": {"type":"person", "id":"1"}
                      }
                    }
                  }],
                  "included": [{
                    "type": "person",
                    "id": "1",
                    "attributes": {
                      "name": "John Doe"
                    }
                  }]
                }""", document);
    }

    private static class Article extends ResourceObject {
        @JsonProperty("title")
        public String title;
        @JsonProperty("author")
        public ToOneRelationship<Person> author;
        @JsonProperty("comments")
        public ToManyRelationship<Comment> comments;

        Article(String id, String title, Person author, Comment... comments) {
            super("article", id);
            this.title = title;
            this.author = ResourceObject.asRelationship(author);
            this.comments = comments.length == 0 ? null : ResourceObject.asRelationship(comments);
        }
    }

    private static class Person extends ResourceObject {
        @JsonProperty("name")
        public String name;

        Person(String id, String name) {
            super("person", id);
            this.name = name;
        }
    }

    private static class Comment extends ResourceObject {
        @JsonProperty("text")
        public String text;

        Comment(String id, String text) {
            super("comment", id);
            this.text = text;
        }
    }
}
