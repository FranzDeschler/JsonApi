package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.Article;
import cloud.codestore.jsonapi.Comment;
import cloud.codestore.jsonapi.Person;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.resource.ResourceObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FullJsonApiDocumentTest {
    @Test
    void fullDocument() {
        Person author = new Person("9", "Dan", "Gebhardt", "dgeb");
        Comment[] comments = new Comment[]{
                new Comment("5", "First!"),
                new Comment("12", "I like XML better")
        };
        Article article = new Article("1", "JSON:API paints my bikeshed!", author, comments);

        JsonApiDocument document = JsonApiDocument.of(new ResourceObject[]{article})
                .addLink(new Link(Link.SELF, "https://example.com/articles"))
                .addLink(new Link(Link.NEXT, "https://example.com/articles?page[offset]=2"))
                .addLink(new Link(Link.LAST, "https://example.com/articles?page[offset]=10"));

        Assertions.assertThat(TestObjectWriter.write(document)).isEqualTo(EXPECTED_JSON);
    }

    private static final String EXPECTED_JSON = """
            {
              "data" : [ {
                "type" : "article",
                "id" : "1",
                "attributes" : {
                  "title" : "JSON:API paints my bikeshed!"
                },
                "relationships" : {
                  "author" : {
                    "links" : {
                      "related" : "https://example.com/articles/1/author",
                      "self" : "https://example.com/articles/1/relationships/author"
                    },
                    "data" : {
                      "type" : "person",
                      "id" : "9"
                    }
                  },
                  "comments" : {
                    "data" : [ {
                      "type" : "comment",
                      "id" : "5"
                    }, {
                      "type" : "comment",
                      "id" : "12"
                    } ]
                  }
                },
                "links" : {
                  "self" : "https://example.com/articles/1"
                }
              } ],
              "included" : [ {
                "type" : "person",
                "id" : "9",
                "attributes" : {
                  "firstName" : "Dan",
                  "lastName" : "Gebhardt",
                  "twitter" : "dgeb"
                },
                "links" : {
                  "self" : "https://example.com/people/9"
                }
              }, {
                "type" : "comment",
                "id" : "5",
                "attributes" : {
                  "body" : "First!"
                },
                "relationships" : {
                  "author" : {
                    "links" : {
                      "related" : "https://example.com/comments/5/author",
                      "self" : "https://example.com/comments/5/relationships/author"
                    }
                  }
                },
                "links" : {
                  "self" : "https://example.com/comments/5"
                }
              }, {
                "type" : "comment",
                "id" : "12",
                "attributes" : {
                  "body" : "I like XML better"
                },
                "relationships" : {
                  "author" : {
                    "links" : {
                      "related" : "https://example.com/comments/12/author",
                      "self" : "https://example.com/comments/12/relationships/author"
                    }
                  }
                },
                "links" : {
                  "self" : "https://example.com/comments/12"
                }
              } ],
              "links" : {
                "next" : "https://example.com/articles?page[offset]=2",
                "last" : "https://example.com/articles?page[offset]=10",
                "self" : "https://example.com/articles"
              }
            }""";
}
