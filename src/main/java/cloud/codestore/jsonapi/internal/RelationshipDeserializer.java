package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * Deserializes a relationship based on the type of the "data" property.
 * If it is an object or {@code null}, this deserializer returns a {@link ToOneRelationship}.
 * If it is an array, this deserializer returns a {@link ToManyRelationship}.
 */
public class RelationshipDeserializer extends StdDeserializer<Relationship> {
    public RelationshipDeserializer() {
        super(Relationship.class);
    }

    @Override
    public Relationship deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        ObjectNode relationshipNode = mapper.readTree(jsonParser);

        if (relationshipNode.has("data")) {
            JsonNode dataNode = relationshipNode.get("data");
            if (dataNode.isObject()) {
                return mapper.treeToValue(relationshipNode, ToOneRelationship.class);
            } else if (dataNode.isArray()) {
                return mapper.treeToValue(relationshipNode, ToManyRelationship.class);
            }
        }

        return mapper.treeToValue(relationshipNode, ToOneRelationship.class);
    }
}
