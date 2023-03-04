package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Article extends ResourceObject {
    @JsonProperty("title")
    public String title;
    @JsonProperty("author")
    public ToOneRelationship author;
    @JsonProperty("comments")
    public ToManyRelationship comments;

    @JsonCreator
    Article(
            @JsonProperty("title") String title,
            @JsonProperty("author") ToOneRelationship author,
            @JsonProperty("comments") ToManyRelationship comments
    ) {
        super("article");
        this.title = title;
        this.author = author;
        this.comments = comments;
    }

    public Article(String id, String title, Person author, Comment[] comments) {
        super("article", id);
        this.title = title;

        this.author = author.asRelationship();
        this.author.setRelatedResourceLink("https://example.com/articles/" + id + "/author");
        this.author.setSelfLink("https://example.com/articles/" + id + "/relationships/author");

        this.comments = new ToManyRelationship(comments);

        setSelfLink("https://example.com/articles/" + id);
    }
}
