module dev.systemout.jsonapi {
    requires transitive com.fasterxml.jackson.databind;

    exports dev.systemout.jsonapi;
    exports dev.systemout.jsonapi.document;
    exports dev.systemout.jsonapi.error;
    exports dev.systemout.jsonapi.link;
    exports dev.systemout.jsonapi.meta;
    exports dev.systemout.jsonapi.relationship;
    exports dev.systemout.jsonapi.resource;

    opens dev.systemout.jsonapi to com.fasterxml.jackson.databind;
    opens dev.systemout.jsonapi.document to com.fasterxml.jackson.databind;
    opens dev.systemout.jsonapi.error to com.fasterxml.jackson.databind;
    opens dev.systemout.jsonapi.link to com.fasterxml.jackson.databind;
    opens dev.systemout.jsonapi.meta to com.fasterxml.jackson.databind;
    opens dev.systemout.jsonapi.relationship to com.fasterxml.jackson.databind;
    opens dev.systemout.jsonapi.resource to com.fasterxml.jackson.databind;
    opens dev.systemout.jsonapi.internal to com.fasterxml.jackson.databind;
}