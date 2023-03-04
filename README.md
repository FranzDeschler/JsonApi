# JSON:API library

This library provides serialization and deserialization of [JSON:API](https://jsonapi.org) documents based on [Jackson](https://github.com/FasterXML/jackson-docs).

What this library does:
- It can be used on both, client and server side.
- It´s independent of any specific framework (like Spring).
- It only depends on Jackson and therefore is perfect for applications that already use Jackson.
- It claims to create fully compatible JSON objects according to the JSON:API specification.

What this library does not:
- It is not a client that dynamically loads resources from the server.
- It does not magically map JSON objects to business objects.
  Its only purpose is to be used as DTO between client and server.
  Thus, the structure of the Java objects corresponds to the JSON structure (with some exceptions).

## JSON:API Version
The current version of the library only supports the JSON:API format [1.0](https://jsonapi.org/format/1.0/)

## Maven
The library can be included via Maven. It contains Jackson as its only dependency. 
```xml
<dependency>
    <groupId>cloud.codestore</groupId>
    <artifactId>jsonapi</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
If your application already includes Jackson and you want to avoid dependency conflicts, simply exclude Jackson.
```xml
<dependency>
    <groupId>cloud.codestore</groupId>
    <artifactId>jsonapi</artifactId>
    <version>1.0-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

# Serialization
## Object Mapper
To make the serialization work correctly, the provided `JsonApiObjectMapper` needs to be used.
```java
ObjectMapper objectMapper = new JsonApiObjectMapper();
```

## Resource Objects
Resource objects are the central objects in JSON:API.
You simply create them like any other Jackson object. 
All properties that are processed by Jackson are included in the "attributes" of the resulting JSON (except relationships).
```java
public class Person extends ResourceObject  {
    @JsonProperty("firstName") private String firstName;
    @JsonProperty("lastName")  private String lastName;

    public Person(String id) {
        super("person", id);
    }
    
    // Getter / Setter ...
}
```

## JSON:API Documents
### Single Resource Object
To create a `JsonApiDocument` which contains a single resource object, create a new `SingleResourceDocument` instance
or use the more convenient factory method `of()` in the `JsonApiDocument` class.
```java
var person = new Person("1");
var document = JsonApiDocument.of(person);
```
```json
{
  "data": {
    "type": "person",
    "id": "1",
    "attributes": {
      "firstName": "John",
      "lastName": "Doe"
    }
  }
}
```

### Resource Object Collection
To create a `JsonApiDocument` which contains multiple resource objects, create a new `ResourceCollectionDocument` instance
or use the more convenient factory method `of()` in the `JsonApiDocument` class.
```java
var persons = new Person[]{new Person("1"), new Person("2")};
var document = JsonApiDocument.of(persons);
```
```json
{
  "data": [{
    "type": "person",
    "id": "1",
    "attributes": {...}
  }, {
    "type": "person",
    "id": "2",
    "attributes": {...}
  }]
}
```

## Links
The JSON:API is quite restrictive about the allowed links relations.
However, where links are available, this library also allows to set custom links.
`JsonApiDocument`, `ResourceObject` and `Relationship` object also provide a convenient method to set 
the "self" link on the corresponding object. 

```java
var person = new Person("1").setSelfLink("https://example.com/persons/1");
var document = JsonApiDocument.of(person).addLink(new Link("home", "https://example.com"));
```
```json
{
  "data": {
    "type": "person",
    "id": "1",
    "attributes": {...},
    "links": {
      "self": "https://example.com/persons/1"
    }
  },
  "links": {
    "home": "https://example.com"
  }
}
```

## Relationships
In their simplest way, relationships only contains the URL to the related resource.
```java
public class Article extends ResourceObject {
    @JsonProperty("title") private String title;
    @JsonProperty("author") private Relationship author;
    @JsonProperty("author") private Relationship comments;

    public Article(String id, String title) {
        super("article", id);
        this.title = title;
        this.author = new Relationship("https://example.com/articles/" + id + "/author");
        this.comments = new Relationship("https://example.com/articles/" + id + "/comments");
    }
}

public class Comment extends ResourceObject {
    @JsonProperty("text") private String text;

    public Comment(String id, String text) {
        super("comment", id);
        this.text = text;
    }
}
```
```json
{
  "data": {
    "type": "article",
    "id": "1",
    "attributes": {
      "title": "My first article!"
    },
    "relationships": {
      "author": {
        "links": {
          "related": "https://example.com/articles/1/author"
        }
      },
      "comments": {
        "links": {
          "related": "https://example.com/articles/1/comments"
        }
      }
    }
  }
}
```

### Including related resources
To include the related resources, use the `ToOneRelationship` and `ToManyRelationship` classes for
to-one and to-many relationships respectively.
```java
public class Article extends ResourceObject {
    @JsonProperty("title") private String title;
    @JsonProperty("author") private Relationship author;
    @JsonProperty("author") private Relationship comments;

    public Article(String id, String title, Person author, Comment[] comments) {
        super("article", id);
        this.title = title;
        this.author = new ToOneRelationship(author);
        this.comments = new ToManyRelationship(comments);
    }
}
```
```json
{
  "data": {
    "type": "article",
    "id": "1",
    "relationships": {
      "author": {
        "data": {"type": "person", "id": "1"}
      },
      "comments": {
        "data": [
          {"type": "comment", "id": "1"},
          {"type": "comment", "id": "2"}
        ]
      }
    }
  },
  "included": [{
    "type": "person",
    "id": "1",
    "attributes": {...}
  }, {
    "type": "comment",
    "id": "1",
    "attributes": {...}
  }, {
    "type": "comment",
    "id": "2",
    "attributes": {...}
  }]
}
```

## Meta Informations
Meta information objects are regular Jackson objects with the only condition that they need to implement the `MetaInformation` interface.
That interface contains no methods and has only a declarative function.
```java
public class CustomMetaInformation implements MetaInformation {
    @JsonProperty("info") private String info;

    public CustomMetaInformation(String info) {
        this.info = info;
    }
}
```
```java
var meta = new CustomMetaInformation("Read the JSON:API documentation at jsonapi.org");
var document = JsonApiDocument.of(person).setMeta(meta);
```
```json
{
  "data": {...},
  "meta": {
    "info": "Read the JSON:API documentation at jsonapi.org"
  }
}
```

## JSON:API Objects
A `JsonApiObject` can be placed in the top level `JsonApiDocument` and include implementation information.
```java
var document = JsonApiDocument.of(person).setJsonapiObject(new JsonApiObject());
```
```json
{
  "jsonapi": {
    "version": "1.0"
  },
  ...
}
```

## Error Objects
Error objects work pretty straightforward. There is nothing special to know about it.
```java
var error = new ErrorObject().setId("12345")
                             .setStatus("404")
                             .setTitle("Resource Not Found")
                             .setDetail("The requested resource does not exist.");

var document = new ErrorDocument(error);
```


# Deserialization
## Resource Objects
Client side resource objects have the same structure as the server side ones.
Remember to use the appropriate Jackson annotations for setting the fields.
```java
public class Article extends ResourceObject {
    private String title;
    private Relationship author;
    private Relationship comments;

    @JsonCreator
    public Article(
            @JsonProperty("id") String id,
            @JsonProperty("author") Relationship author,
            @JsonProperty("comments") Relationship comments
    ) {
        super("article", id);
        this.author = author;
        this.comments = comments;
    }
    
    // Getter / Setter ...
}

public class Person extends ResourceObject {
    private String firstName;
    private String lastName;

    @JsonCreator
    public Person(
            @JsonProperty("id") String id,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName
    ) {
        super("person", id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getter / Setter ...
}

public class Comment extends ResourceObject {
    private String text;

    @JsonCreator
    public Comment(@JsonProperty("id") String id, @JsonProperty("text") String text) {
        super("comment", id);
        this.text = text;
    }

    // Getter / Setter ...
}
```

## Object Mapper
To be able to deserialize resource objects, the types need to be registered in the `ObjectMapper`.
```java
ObjectMapper objectMapper = new JsonApiObjectMapper()
        .registerResourceType("person", Person.class)
        .registerResourceType("article", Article.class)
        .registerResourceType("comment", Comment.class);
```

## JSON:API Documents
Usually, the client knows whether the requested resource is a single resource or a collection of resources.
If this is not the case, specify `JsonApiDocument` as value type and check the concrete type manually.
```java
JsonApiDocument document = objectMapper.readValue("{...}", JsonApiDocument.class);
if (document instanceof SingleResourceDocument document) {
    // ...
} else if (document instanceof ResourceCollectionDocument document) {
    // ...
}
```

### Single Resource Object
```java
SingleResourceDocument<Person> document = objectMapper.readValue("{...}", SingleResourceDocument.class);
Person person = document.getData();
```

### Resource Object Collection
```java
ResourceCollectionDocument<Comment> document = objectMapper.readValue("{...}", ResourceCollectionDocument.class);
Comment[] comments = document.getData();
```

## Relationships
If relationships are declared as `Relationship` in the resource object, they don´t provide access to the referred,
included object unless the relationship object is casted manually.

### Included Resources
To get the included resource object which is referred by a specific relationship, it needs to be declared either
as `ToOneRelationship` or `ToManyRelationship`. Since relationships don´t contain type information,
the value type needs to be specified when retrieving the related resource.
```java
public class Article extends ResourceObject {
    private String title;
    private ToOneRelationship author;
    private ToManyRelationship comments;

    @JsonCreator
    public Article(
            @JsonProperty("id") String id,
            @JsonProperty("author") ToOneRelationship author,
            @JsonProperty("comments") ToManyRelationship comments
    ) {/* ... */}
}
```
```java
SingleResourceDocument<Article> document = objectMapper.readValue("{...}", SingleResourceDocument.class);
var authorRelationship = document.getData().getAuthor();
var author = authorRelationship.getRelatedResource(Person.class);
```

## Meta Information
Deserializing meta information objects is not as straightforward as other objects. The problem is that they are
not standardized and therefore can be arbitrary complex. Furthermore, the content of the objects differs depending on
the location within the document. The meta information of a relationship is most likely different from the meta
information of a resource object.

By default, meta information object are not deserialized. To do that, the application needs to provide a custom
deserializer for meta information objects and pass it to the `JsonApiObjectMapper`.
```java
public class CustomMetaInformation implements MetaInformation {
    private String info;

    @JsonCreator
    public CustomMetaInformation(@JsonProperty("info") String info) {
        this.info = info;
    }
}
```
```java
public class CustomMetaDeserializer implements MetaDeserializer {
    @Override
    public Class<? extends MetaInformation> getClass(String pointer) {
        return "/data/meta".equals(pointer) ? CustomMetaInformation.class : null;
    }

    @Override
    public MetaInformation deserialize(String pointer, ObjectNode node) {
        // dynamically parse the meta information
    }
}
```
```java
ObjectMapper objectMapper = new JsonApiObjectMapper(new CustomMetaDeserializer());
```

When the library finds a meta information object in the JSON string during deserialization, it will first call
the `getClass()` method, and passes the location (a [JSON pointer](https://www.rfc-editor.org/rfc/rfc6901)) 
of the object within the document.
The custom deserializer can then return the class of the meta information object based on that pointer.
If the `getClass()` method returns `null`, the `deserialize()` method will be called.
In this case, the library provides the location and the Jackson `ObjectNode` of the meta information object.
The custom deserializer can then try to dynamically parse the object on its own.

Here are some examples of the possible values of the JSON pointers. But there are many more.
- `/meta`
- `/links/self/meta`
- `/data/meta`
- `/data/0/meta`
- `/data/relationships/author/meta`

## Error Objects
```java
var document = objectMapper.readValue("{...}", ErrorDocument.class);
var error = document.getErrors()[0];
```