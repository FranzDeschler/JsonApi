package dev.systemout.jsonapi.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.systemout.jsonapi.relationship.Relationship;
import dev.systemout.jsonapi.relationship.ToManyRelationship;
import dev.systemout.jsonapi.relationship.ToOneRelationship;
import dev.systemout.jsonapi.resource.ResourceObject;

import java.io.IOException;

/**
 * Deserializes a relationship based on the type of the "data" property.
 * If it is an object or {@code null}, this deserializer returns a {@link ToOneRelationship}.
 * If it is an array, this deserializer returns a {@link ToManyRelationship}.
 */
class DynamicRelationshipDeserializer extends StdDeserializer<Relationship> {
    DynamicRelationshipDeserializer() {
        super(Relationship.class);
    }

    @Override
    public Relationship deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        ObjectNode relationshipNode = mapper.readTree(jsonParser);

        if (relationshipNode.has("data")) {
            JsonNode dataNode = relationshipNode.get("data");
            if (dataNode.isObject()) {
                return mapper.treeToValue(relationshipNode, toOneRelationshipTypeReference(mapper));
            } else if (dataNode.isArray()) {
                return mapper.treeToValue(relationshipNode, toManyRelationshipTypeReference(mapper));
            }
        }

        return mapper.treeToValue(relationshipNode, toOneRelationshipTypeReference(mapper));
    }

    private JavaType toOneRelationshipTypeReference(ObjectMapper mapper) {
        return mapper.getTypeFactory().constructType(new TypeReference<ToOneRelationship<ResourceObject>>() {});
    }

    private JavaType toManyRelationshipTypeReference(ObjectMapper mapper) {
        return mapper.getTypeFactory().constructType(new TypeReference<ToManyRelationship<ResourceObject>>() {});
    }
}
