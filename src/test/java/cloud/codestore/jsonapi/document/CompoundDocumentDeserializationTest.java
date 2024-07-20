package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See <a href="https://jsonapi.org/format/1.1/#document-compound-documents">jsonapi.org</a>
 */
@DisplayName("A compound document")
class CompoundDocumentDeserializationTest {
    private static final TestObjectReader reader = new TestObjectReader(Article.class, Person.class, Comment.class);

    @Test
    @DisplayName("must contain an array of resource objects in a top-level included member")
    void includeResources() {
        var document = reader.read("""
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
                }""", new TypeReference<SingleResourceDocument<Article>>() {});

        var article = document.getData();
        assertThat(article).isNotNull();
        assertThat(article.getId()).isEqualTo("1");
        assertThat(article.title).isEqualTo("John's article");

        var authorRelationship = article.author;
        assertThat(authorRelationship.getData()).isEqualTo(new ResourceIdentifierObject("person", "1"));
        assertThat(authorRelationship.getRelatedResource().name).isEqualTo("John Doe");

        var commentsRelationship = article.comments;
        assertThat(commentsRelationship.getData()).containsExactly(new ResourceIdentifierObject("comment", "1"));
        assertThat(commentsRelationship.getRelatedResource()).hasSize(1);
        assertThat(commentsRelationship.getRelatedResource()[0].text).isEqualTo("Nice Article, John!");
    }

    @Test
    @DisplayName("must not include more than one resource object for each type and id pair")
    void uniqueIncludedResource() {
        var document = reader.read("""
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
                  }, {
                    "type": "person",
                    "id": "1",
                    "attributes": {
                      "name": "John Doe"
                    }
                  }]
                }""", new TypeReference<ResourceCollectionDocument<Article>>() {});

        assertThat(document.getIncludedResources()).hasSize(1);

        var articles = document.getData();
        assertThat(articles).isNotNull().hasSize(2);

        assertThat(articles[0].getId()).isEqualTo("1");
        assertThat(articles[0].title).isEqualTo("John's first article");
        assertThat(articles[0].author.getData()).isEqualTo(new ResourceIdentifierObject("person", "1"));

        assertThat(articles[1].getId()).isEqualTo("2");
        assertThat(articles[1].title).isEqualTo("Another article");
        assertThat(articles[1].author.getData()).isEqualTo(new ResourceIdentifierObject("person", "1"));

        assertThat(articles[0].author.getRelatedResource()).isSameAs(articles[1].author.getRelatedResource());

    }

    private static class Article extends ResourceObject {
        String title;
        ToOneRelationship<Person> author;
        ToManyRelationship<Comment> comments;

        @JsonCreator
        Article(
                @JsonProperty("title") String title,
                @JsonProperty("author") ToOneRelationship<Person> author,
                @JsonProperty("comments") ToManyRelationship<Comment> comments
        ) {
            super("article");
            this.title = title;
            this.author = author;
            this.comments = comments;
        }
    }

    private static class Person extends ResourceObject {
        String name;

        @JsonCreator
        Person(@JsonProperty("name") String name) {
            super("person");
            this.name = name;
        }
    }

    private static class Comment extends ResourceObject {
        String text;

        @JsonCreator
        Comment(@JsonProperty("text") String text) {
            super("comment");
            this.text = text;
        }
    }
}
