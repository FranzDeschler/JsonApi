package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.*;
import cloud.codestore.jsonapi.link.Link;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
                                                  .addLink(Link.SELF, new Link("https://example.com/articles"))
                                                  .addLink(Link.NEXT, new Link("https://example.com/articles?page[offset]=2"))
                                                  .addLink(Link.LAST, new Link("https://example.com/articles?page[offset]=10"));

        JsonAssertion.assertEquals(EXPECTED_JSON, document);

        ResourceCollectionDocument<Article> deserializedDocument = TestObjectReader.read(EXPECTED_JSON, new TypeReference<>() {});
        assertThat(deserializedDocument.getData()).isNotNull();
        assertThat(deserializedDocument.getData()).hasSize(1);

        Article deserializedArticle = deserializedDocument.getData()[0];
        assertThat(deserializedArticle.getIdentifier()).isEqualTo(article.getIdentifier());

        List<ResourceObject> includedResources = deserializedDocument.getIncludedResources();
        assertThat(includedResources).isNotNull();
        assertThat(includedResources).hasSize(3);

        assertThat(deserializedArticle.author.getRelatedResourceLink()).isNotNull();
        assertThat(deserializedArticle.author.getRelatedResource()).isNotNull();
        assertThat(deserializedArticle.author.getRelatedResource()).isSameAs(includedResources.get(0));

        assertThat(deserializedArticle.comments.getRelatedResourceLink()).isNull();
        assertThat(deserializedArticle.comments.getRelatedResource()).isNotNull();
        assertThat(deserializedArticle.comments.getRelatedResource()).hasSize(2);
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
