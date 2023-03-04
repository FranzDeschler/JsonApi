package cloud.codestore.jsonapi.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.Annotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A custom {@link VirtualBeanPropertyWriter} implementation to serialize the virtual "attributes" object.
 */
public class VirtualAttributesWriter extends VirtualBeanPropertyWriter {
    private List<BeanPropertyWriter> attributeProperties = Collections.emptyList();

    VirtualAttributesWriter() {}

    private VirtualAttributesWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType) {
        super(propDef, contextAnnotations, declaredType);
    }

    void setAttributeProperties(List<BeanPropertyWriter> attributeProperties) {
        if (attributeProperties != null)
            this.attributeProperties = attributeProperties;
    }

    @Override
    protected Object value(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        if (attributeProperties == null || attributeProperties.isEmpty())
            return null;

        Map<String, Object> attributes = new TreeMap<>();

        for (BeanPropertyWriter property : attributeProperties) {
            property.getMember().fixAccess(true);
            Object value = property.get(bean);
            if (value != null)
                attributes.put(property.getName(), value);
        }

        return attributes.isEmpty() ? null : attributes;
    }

    @Override
    public VirtualBeanPropertyWriter withConfig(
            MapperConfig<?> mapperConfig, AnnotatedClass annotatedClass,
            BeanPropertyDefinition beanPropertyDefinition, JavaType javaType
    ) {
        return new VirtualAttributesWriter(beanPropertyDefinition, annotatedClass.getAnnotations(), javaType);
    }
}