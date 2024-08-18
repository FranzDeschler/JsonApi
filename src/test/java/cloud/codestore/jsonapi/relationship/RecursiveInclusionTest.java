package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.document.JsonApiDocument;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static cloud.codestore.jsonapi.JsonAssertion.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Relationships of included resource objects")
class RecursiveInclusionTest {

    private final SingleResourceDocument<Article> document;

    RecursiveInclusionTest() {
        var article = new Article(
                "JUnit in a nutshell",
                new Comment("1", "What a great article!",
                        new Comment("11", "Nice and helpful feedback."),
                        new Comment("12", "Does anyone read these comments?"),
                        new Comment("13", "Thank you. It was a lot of work.",
                                new Comment("131", "You are welcome!"),
                                new Comment("132", "How long did it take?"))),
                new Comment("2", "I was missing a topic.",
                        new Comment("21", "What topic did you miss?")),
                new Comment("3", "I agree with both above.")
        );

        document = JsonApiDocument.of(article);
    }

    @Test
    @DisplayName("are included recursively")
    void includeRecursive() {
        assertEquals("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "attributes": {
                      "title": "JUnit in a nutshell"
                    },
                    "relationships": {
                      "comments": {
                        "data": [
                          {"type": "comment", "id": "1"},
                          {"type": "comment", "id": "2"},
                          {"type": "comment", "id": "3"}
                        ]
                      }
                    }
                  },
                  "included": [{
                    "type": "comment",
                    "id": "1",
                    "attributes": {
                      "text": "What a great article!"
                    },
                    "relationships": {
                      "answers": {
                        "data": [
                          {"type": "comment", "id": "11"},
                          {"type": "comment", "id": "12"},
                          {"type": "comment", "id": "13"}
                        ]
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "2",
                    "attributes": {
                      "text": "I was missing a topic."
                    },
                    "relationships": {
                      "answers": {
                        "data": [{"type": "comment", "id": "21"}]
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "3",
                    "attributes": {
                      "text": "I agree with both above."
                    },
                    "relationships": {
                      "answers": {
                        "data": []
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "11",
                    "attributes": {
                      "text": "Nice and helpful feedback."
                    },
                    "relationships": {
                      "answers": {
                        "data": []
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "12",
                    "attributes": {
                      "text": "Does anyone read these comments?"
                    },
                    "relationships": {
                      "answers": {
                        "data": []
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "13",
                    "attributes": {
                      "text": "Thank you. It was a lot of work."
                    },
                    "relationships": {
                      "answers": {
                        "data": [
                          {"type": "comment", "id": "131"},
                          {"type": "comment", "id": "132"}
                        ]
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "131",
                    "attributes": {
                      "text": "You are welcome!"
                    },
                    "relationships": {
                      "answers": {
                        "data": []
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "132",
                    "attributes": {
                      "text": "How long did it take?"
                    },
                    "relationships": {
                      "answers": {
                        "data": []
                      }
                    }
                  }, {
                    "type": "comment",
                    "id": "21",
                    "attributes": {
                      "text": "What topic did you miss?"
                    },
                    "relationships": {
                      "answers": {
                        "data": []
                      }
                    }
                  }]
                }""", TestObjectWriter.write(document));
    }

    @Test
    @DisplayName("are linked recursively")
    void linkIncludedResources() {
        String json = TestObjectWriter.write(document);
        Article article = new TestObjectReader(Article.class, Comment.class)
                .read(json, new TypeReference<SingleResourceDocument<Article>>() {})
                .getData();

        Article expectedArticle = document.getData();
        assertCommentsEquality(expectedArticle.comments.getRelatedResource(), article.comments.getRelatedResource());
    }

    private void assertCommentsEquality(Comment[] expectedComments, Comment[] actualComments) {
        assertThat(expectedComments).containsExactlyInAnyOrder(actualComments);

        for (int i = 0; i < expectedComments.length; i++) {
            ToManyRelationship<Comment> expectedAnswers = expectedComments[i].answers;
            ToManyRelationship<Comment> actualAnswers = actualComments[i].answers;
            if (isNotEmpty(expectedAnswers) && isNotEmpty(actualAnswers))
                assertCommentsEquality(expectedAnswers.getRelatedResource(), actualAnswers.getRelatedResource());
        }
    }

    private boolean isNotEmpty(ToManyRelationship<?> relationship) {
        ResourceObject[] relatedResource = relationship.getRelatedResource();
        return relatedResource != null && relatedResource.length != 0;
    }

    private static class Article extends ResourceObject {
        @JsonProperty String title;
        @JsonProperty ToManyRelationship<Comment> comments;

        Article(String title, Comment... comments) {
            super("article", "1");
            this.title = title;
            this.comments = new ToManyRelationship<>(comments);
        }

        @JsonCreator
        Article(@JsonProperty("title") String title, @JsonProperty("comments") ToManyRelationship<Comment> comments) {
            super("article");
            this.title = title;
            this.comments = comments;
        }
    }

    private static class Comment extends ResourceObject {
        @JsonProperty String text;
        @JsonProperty ToManyRelationship<Comment> answers;

        Comment(String id, String text, Comment... answers) {
            super("comment", id);
            this.text = text;
            this.answers = new ToManyRelationship<>(answers);
        }

        @JsonCreator
        Comment(@JsonProperty("text") String text, @JsonProperty("answers") ToManyRelationship<Comment> answers) {
            super("comment");
            this.text = text;
            this.answers = answers;
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;

            Comment comment = (Comment) obj;
            return text.equals(comment.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, answers);
        }
    }
}
