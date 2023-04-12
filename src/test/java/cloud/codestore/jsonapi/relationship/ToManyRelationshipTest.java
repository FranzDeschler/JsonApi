package cloud.codestore.jsonapi.relationship;

import cloud.codestore.jsonapi.Article;
import cloud.codestore.jsonapi.Comment;
import cloud.codestore.jsonapi.TestObjectReader;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.document.SingleResourceDocument;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A to-many relationship")
class ToManyRelationshipTest
{
    private ToManyRelationship relationship = new ToManyRelationship();

    @Test
    @DisplayName("is empty after creation")
    void isEmpty() {
        assertThat(relationship.getLinks()).isNotNull();
        assertThat(relationship.getLinks().isEmpty()).isTrue();
        assertThat(relationship.getData()).isNull();
        assertThat(relationship.getRelatedResource()).isNull();
        assertThat(relationship.isIncluded()).isFalse();
    }

    @Test
    @DisplayName("is included after setting a related resource")
    void include()
    {
        assertThat(relationship.isIncluded()).isFalse();
        ResourceObject[] relatedResources = {
                new ResourceObject("test", "1") {},
                new ResourceObject("test", "2") {},
                new ResourceObject("test", "3") {}
        };
        relationship.setRelatedResource(relatedResources);
        assertThat(relationship.isIncluded()).isTrue();
        assertThat(relationship.getRelatedResource()).isSameAs(relatedResources);
        assertThat(relationship.getData()).containsExactlyInAnyOrder(
                new ResourceIdentifierObject("test", "1"),
                new ResourceIdentifierObject("test", "2"),
                new ResourceIdentifierObject("test", "3")
        );
    }

    @Test
    @DisplayName("must have a resource identifier when a related resource is set")
    void setRelatedResource()
    {
        ResourceObject[] relatedResource = {new ResourceObject("test", "123") {}};
        relationship.setRelatedResource(relatedResource);
        assertThatThrownBy(() -> relationship.setData(null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("removes the resource identifier when the resource is removed")
    void resetRelatedResource()
    {
        ResourceObject[] relatedResource = {new ResourceObject("test", "123") {}};
        relationship.setRelatedResource(relatedResource);
        assertThat(relationship.getRelatedResource()).isNotNull();
        assertThat(relationship.getData()).isNotNull();

        relationship.setRelatedResource(null);

        assertThat(relationship.getRelatedResource()).isNull();
        assertThat(relationship.getData()).isNull();
    }

    @Test
    @DisplayName("can contain a single resource identifier object")
    void containsResourceIdentifier()
    {
        relationship.setData(new ResourceIdentifierObject[]{
                new ResourceIdentifierObject("snippet", "12345"),
                new ResourceIdentifierObject("snippet", "54321"),
                new ResourceIdentifierObject("snippet", "32154")
        });
        String json = TestObjectWriter.write(relationship);
        Assertions.assertThat(json).isEqualTo("""
                {
                  "data" : [ {
                    "type" : "snippet",
                    "id" : "12345"
                  }, {
                    "type" : "snippet",
                    "id" : "54321"
                  }, {
                    "type" : "snippet",
                    "id" : "32154"
                  } ]
                }""");
    }

    @Test
    @DisplayName("contains included resource object")
    void containsIncludedResource() {
        SingleResourceDocument<Article> document = TestObjectReader.read("""
                {
                  "data": {
                    "type": "article",
                    "id": "1",
                    "relationships": {
                      "comments": {
                        "data": [{
                          "type": "comment",
                          "id": "5"
                        }, {
                          "type": "comment",
                          "id": "12"
                        }]
                      }
                    }
                  },
                  "included": [{
                    "type": "comment",
                    "id": "5",
                    "attributes": {
                      "body": "First!"
                    }
                  }, {
                    "type": "comment",
                    "id": "12",
                    "attributes": {
                      "body": "I like XML better"
                    }
                  }]
                }""", new TypeReference<>() {});

        Article article = document.getData();
        assertThat(article).isNotNull();

        ToManyRelationship relationship = article.comments;
        assertThat(relationship.isIncluded()).isTrue();

        Comment[] comments = relationship.getRelatedResource(Comment.class);
        assertThat(comments).hasSize(2);
        assertThat(comments[0]).isInstanceOf(Comment.class);
        assertThat(comments[1]).isInstanceOf(Comment.class);
    }
}