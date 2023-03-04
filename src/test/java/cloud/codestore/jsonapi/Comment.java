package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment extends ResourceObject {
    @JsonProperty("body")
    public String body;
    @JsonProperty("author")
    public Relationship author;

    @JsonCreator
    Comment(@JsonProperty("body") String body, @JsonProperty("author") Relationship author) {
        super("comment");
        this.body = body;
        this.author = author;
    }

    public Comment(String id, String body) {
        super("comment", id);
        this.body = body;
        this.author = new ToOneRelationship("https://example.com/comments/" + id + "/author");
        this.author.setSelfLink("https://example.com/comments/" + id + "/relationships/author");

        setSelfLink("https://example.com/comments/" + id);
    }
}
