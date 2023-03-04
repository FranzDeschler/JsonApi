package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Modifies the way, how {@link ResourceObject}s are serialized.
 * <br/><br/>
 * Instances of {@link ResourceObject}s contain attributes and relationships in its own fields.
 * This {@link ResourceObject} wraps these fields in virtual objects.
 */
public class ResourceObjectSerializerModifier extends BeanSerializerModifier {
    // Do not change the order of this list. It is used for ordering the properties.
    private static final List<String> PREDEFINED_FIELDS = Arrays.asList(
            "type", "id", "attributes", "relationships", "links", "meta"
    );

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> properties) {
        if (isResourceObject(beanDesc.getBeanClass())) {
            List<BeanPropertyWriter> attributes = new LinkedList<>();
            VirtualAttributesWriter attributesWriter = null;

            List<BeanPropertyWriter> relationships = new LinkedList<>();
            VirtualRelationshipsWriter relationshipsWriter = null;

            for (int i = properties.size() - 1; i >= 0; i--) {
                BeanPropertyWriter property = properties.get(i);
                String propertyName = property.getName();

                if (PREDEFINED_FIELDS.contains(propertyName)) {
                    if ("attributes".equals(propertyName) && property instanceof VirtualAttributesWriter)
                        attributesWriter = (VirtualAttributesWriter) property;
                    else if ("relationships".equals(propertyName) && property instanceof VirtualRelationshipsWriter)
                        relationshipsWriter = (VirtualRelationshipsWriter) property;
                } else {
                    if (isRelationship(property.getMember().getRawType()))
                        relationships.add(property);
                    else
                        attributes.add(property);

                    properties.remove(i);
                }
            }

            if (attributesWriter != null)
                attributesWriter.setAttributeProperties(attributes);

            if (relationshipsWriter != null)
                relationshipsWriter.setRelationshipProperties(relationships);
        }

        return properties;
    }

    @Override
    public List<BeanPropertyWriter> orderProperties(
            SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties
    ) {
        if (isResourceObject(beanDesc.getBeanClass())) {
            BeanPropertyWriter[] orderedList = new BeanPropertyWriter[PREDEFINED_FIELDS.size()];

            for (BeanPropertyWriter property : beanProperties) {
                int index = PREDEFINED_FIELDS.indexOf(property.getName());
                orderedList[index] = property;
            }

            return Arrays.asList(orderedList);
        }

        return super.orderProperties(config, beanDesc, beanProperties);
    }

    /**
     * @param type a class type.
     * @return {@code true}, if the given type is a subtype of {@link ResourceObject}.
     */
    private static boolean isResourceObject(Class<?> type) {
        return ResourceObject.class.isAssignableFrom(type);
    }

    /**
     * @param type a class type.
     * @return {@code true}, if the given type is a subtype of {@link Relationship}.
     */
    private static boolean isRelationship(Class<?> type) {
        return Relationship.class.isAssignableFrom(type);
    }
}
