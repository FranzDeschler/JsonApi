package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Person extends ResourceObject {
    @JsonProperty("firstName")
    public String firstName;
    @JsonProperty("lastName")
    public String lastName;
    @JsonProperty("twitter")
    public String twitter;

    @JsonCreator
    Person(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("twitter") String twitter
    ) {
        super("person");
        this.firstName = firstName;
        this.lastName = lastName;
        this.twitter = twitter;
    }

    public Person(String id) {
        super("person", id);
        setSelfLink("https://example.com/people/" + id);
    }

    public Person(String id, String firstName, String lastName, String twitter) {
        this(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.twitter = twitter;
    }
}
