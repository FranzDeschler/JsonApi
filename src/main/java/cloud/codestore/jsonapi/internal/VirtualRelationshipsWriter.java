package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.document.JsonApiDocument;
import cloud.codestore.jsonapi.relationship.Relationship;
import cloud.codestore.jsonapi.relationship.ToManyRelationship;
import cloud.codestore.jsonapi.relationship.ToOneRelationship;
import cloud.codestore.jsonapi.resource.ResourceIdentifierObject;
import cloud.codestore.jsonapi.resource.ResourceObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.Annotations;

import java.util.*;

/**
 * A custom {@link VirtualBeanPropertyWriter} implementation to serialize the virtual "relationships" object.
 * <br/><br/>
 * It also checks the "included" status of the relationships, loads the
 * related data and adds them into the {@link JsonApiDocument}.
 */
public class VirtualRelationshipsWriter extends VirtualBeanPropertyWriter {
    private List<BeanPropertyWriter> relationshipProperties = Collections.emptyList();

    VirtualRelationshipsWriter() {}

    private VirtualRelationshipsWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType) {
        super(propDef, contextAnnotations, declaredType);
    }

    void setRelationshipProperties(List<BeanPropertyWriter> relationshipProperties) {
        if (relationshipProperties != null)
            this.relationshipProperties = relationshipProperties;
    }

    @Override
    protected Object value(Object resourceObject, JsonGenerator json, SerializerProvider prov) throws Exception {
        if (relationshipProperties == null || relationshipProperties.isEmpty())
            return null;

        Map<String, Relationship<?>> relationships = new TreeMap<>();

        relationshipProperties.sort(Comparator.comparing(BeanPropertyWriter::getName)); //needed for ordering the included resources
        for (BeanPropertyWriter property : relationshipProperties) {
            property.getMember().fixAccess(true);
            Relationship<?> relationship = (Relationship<?>) property.get(resourceObject);
            if (relationship != null) {
                includeRelationship(relationship, ((ResourceObject) resourceObject).getParent());
                relationships.put(property.getName(), relationship);
            }
        }

        return relationships.isEmpty() ? null : relationships;
    }

    @Override
    public VirtualBeanPropertyWriter withConfig(
            MapperConfig<?> mapperConfig, AnnotatedClass annotatedClass,
            BeanPropertyDefinition beanPropertyDefinition, JavaType javaType
    ) {
        return new VirtualRelationshipsWriter(beanPropertyDefinition, annotatedClass.getAnnotations(), javaType);
    }

    private void includeRelationship(Relationship<?> relationship, JsonApiDocument document) {
        if (relationship.isIncluded() && document != null) {
            if (relationship instanceof ToOneRelationship)
                include((ToOneRelationship<?>) relationship, document);
            else if (relationship instanceof ToManyRelationship)
                include((ToManyRelationship<?>) relationship, document);
        }
    }

    private void include(ToOneRelationship<?> relationship, JsonApiDocument document) {
        ResourceObject relatedData = relationship.getRelatedResource();
        if (relatedData != null) {
            document.include(relatedData);
            relationship.setData(relatedData.getIdentifier());
        }
    }

    private void include(ToManyRelationship<?> relationship, JsonApiDocument document) {
        ResourceObject[] relatedData = relationship.getRelatedResource();
        if (relatedData != null) {
            document.include(relatedData);
            relationship.setData(
                    Arrays.stream(relatedData)
                            .map(ResourceObject::getIdentifier)
                            .toArray(ResourceIdentifierObject[]::new)
            );
        }
    }
}