module cloud.codestore.jsonapi {
    requires transitive com.fasterxml.jackson.databind;

    exports cloud.codestore.jsonapi;
    exports cloud.codestore.jsonapi.document;
    exports cloud.codestore.jsonapi.error;
    exports cloud.codestore.jsonapi.link;
    exports cloud.codestore.jsonapi.meta;
    exports cloud.codestore.jsonapi.relationship;
    exports cloud.codestore.jsonapi.resource;

    opens cloud.codestore.jsonapi to com.fasterxml.jackson.databind;
    opens cloud.codestore.jsonapi.document to com.fasterxml.jackson.databind;
    opens cloud.codestore.jsonapi.error to com.fasterxml.jackson.databind;
    opens cloud.codestore.jsonapi.link to com.fasterxml.jackson.databind;
    opens cloud.codestore.jsonapi.meta to com.fasterxml.jackson.databind;
    opens cloud.codestore.jsonapi.relationship to com.fasterxml.jackson.databind;
    opens cloud.codestore.jsonapi.resource to com.fasterxml.jackson.databind;
    opens cloud.codestore.jsonapi.internal to com.fasterxml.jackson.databind;
}